package wycs.transforms;

import static wycc.lang.SyntaxError.*;
import static wycs.solver.Solver.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import wyautl.core.*;
import wyautl.io.PrettyAutomataWriter;
import wyautl.rw.*;
import wyautl.rw.Rewriter.Stats;
import wyautl.util.BigRational;
import wybs.lang.Builder;
import wycc.lang.SyntacticElement;
import wycc.lang.Transform;
import wycc.util.Logger;
import wycc.util.Pair;
import wycc.util.Triple;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.Code;
import wycs.core.NormalForms;
import wycs.core.SemanticType;
import wycs.core.Types;
import wycs.core.Value;
import wycs.core.WycsFile;
import wycs.io.WycsFilePrinter;
import wycs.solver.Solver;
import wycs.solver.SolverUtil;
import wyfs.util.Trie;

/**
 * Responsible for converting a <code>WycsFile</code> into an automaton that can
 * then be simplified to test for satisfiability. The key challenge here is to
 * break down the rich language of expressions described by the
 * <code>WycsFile</code> format, such that they can be handled effectively by
 * the <code>Solver</code>.
 *
 * @author David J. Pearce
 *
 */
public class VerificationCheck implements Transform<WycsFile> {
    private enum RewriteMode { SIMPLE, STATICDISPATCH, GLOBALDISPATCH };

	/**
	 * Determines whether this transform is enabled or not.
	 */
	private boolean enabled = getEnable();

	/**
	 * Determines whether debugging is enabled or not
	 */
	private boolean debug = getDebug();

	/**
	 * Determine what rewriter to use.
	 */
	private RewriteMode rwMode = RewriteMode.STATICDISPATCH;

	/**
	 * Determine the maximum number of reduction steps permitted
	 */
	private int maxReductions = getMaxReductions();

	/**
	 * Determine the maximum number of inference steps permitted
	 */
	private int maxInferences = getMaxInferences();

	private final Wyal2WycsBuilder builder;

	private String filename;

	// ======================================================================
	// Constructor(s)
	// ======================================================================

	public VerificationCheck(Builder builder) {
		this.builder = (Wyal2WycsBuilder) builder;
	}

	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public static String describeEnable() {
		return "Enable/disable verification";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	public static String describeDebug() {
		return "Enable/disable debugging information";
	}

	public static boolean getDebug() {
		return false; // default value
	}

	public void setDebug(boolean flag) {
		this.debug = flag;
	}

	public static String describeRwMode() {
		return "Set the rewrite mode to use (simple or static-dispatch)";
	}

	public static String getRwmode() {
		return "staticdispatch"; // default value
	}

	public void setRwmode(String mode) {
		for(RewriteMode rw : RewriteMode.values()) {
			if(mode.equals(rw.name().toLowerCase())) {
				this.rwMode = rw;
				return;
			}
		}
		throw new RuntimeException("unknown rewrite mode: " + mode);
	}

	public static String describeMaxReductions() {
		return "Limits the number of reduction steps permitted";
	}

	public static int getMaxReductions() {
		return 1000; // default value
	}

	public void setMaxReductions(int limit) {
		this.maxReductions = limit;
	}

	public static String describeMaxInferences() {
		return "Limits the number of inference steps permitted";
	}

	public static int getMaxInferences() {
		return 500; // default value
	}

	public void setMaxInferences(int limit) {
		this.maxInferences = limit;
	}


	// ======================================================================
	// Apply Method
	// ======================================================================

	/**
	 * Verify the given list of Wycs statements.
	 *
	 * @param statements
	 * @return the set of failing assertions (if any).
	 */
	public void apply(WycsFile wf) {
		if (enabled) {
			this.filename = wf.filename();

			// Traverse each statement and verify any assertions we
			// encounter.
			List<WycsFile.Declaration> statements = wf.declarations();
			int count = 0;
			for (int i = 0; i != statements.size(); ++i) {
				WycsFile.Declaration stmt = statements.get(i);

				if (stmt instanceof WycsFile.Assert) {
					checkValid((WycsFile.Assert) stmt, ++count);
				} else if (stmt instanceof WycsFile.Function
						|| stmt instanceof WycsFile.Macro
						|| stmt instanceof WycsFile.Type) {
					// TODO: we could try to verify that the function makes
					// sense (i.e. that it's specification is satisfiable for at
					// least one input).
				} else {
					internalFailure("unknown statement encountered " + stmt,
							filename, stmt);
				}
			}
		}
	}

	private void checkValid(WycsFile.Assert stmt, int number) {
		Runtime runtime = Runtime.getRuntime();
		long startTime = System.currentTimeMillis();
		long startMemory = runtime.freeMemory();

		Automaton automaton = new Automaton();
		Automaton original = null;

		Code neg = Code.Unary(SemanticType.Bool,
				Code.Op.NOT, stmt.condition);
		// The following conversion is potentially very expensive, but is
		// currently necessary for the instantiate axioms phase.
		Code nnf = NormalForms.negationNormalForm(neg);
		
		//debug(nnf,filename);
		int maxVar = findLargestVariable(nnf);

		Code vc = instantiateAxioms(nnf, maxVar + 1);
			
		//debug(vc,filename);

		int assertion = translate(vc,automaton,new HashMap<String,Integer>());
		automaton.setRoot(0, assertion);
		// NOTE: don't need to minimise or compact here since the rewriter does
		// this for me.
		//
		//		automaton.minimise();
		//		automaton.compact();

		if (debug) {
			debug(neg,filename);
			original = new Automaton(automaton);
			//debug(original);
		}

		Rewriter rewriter = createRewriter(automaton);
		boolean r = rewriter.apply();

		if(!r) {
			throw new AssertionFailure("timeout occurred during verification",stmt,rewriter,automaton,original);
		} else if(!automaton.get(automaton.getRoot(0)).equals(Solver.False)) {
			String msg = stmt.message;
			msg = msg == null ? "assertion failure" : msg;
			throw new AssertionFailure(msg,stmt,rewriter,automaton,original);
		}

		long endTime = System.currentTimeMillis();
		builder.logTimedMessage("[" + filename + "] Verified assertion #" + number,
				endTime - startTime, startMemory - runtime.freeMemory());
	}

	private int translate(Code expr, Automaton automaton, HashMap<String,Integer> environment) {
		int r;
		if(expr instanceof Code.Constant) {
			r = translate((Code.Constant) expr,automaton,environment);
		} else if(expr instanceof Code.Cast) {
			r = translate((Code.Cast) expr,automaton,environment);
		} else if(expr instanceof Code.Variable) {
			r = translate((Code.Variable) expr,automaton,environment);
		} else if(expr instanceof Code.Binary) {
			r = translate((Code.Binary) expr,automaton,environment);
		} else if(expr instanceof Code.Unary) {
			r = translate((Code.Unary) expr,automaton,environment);
		} else if(expr instanceof Code.Nary) {
			r = translate((Code.Nary) expr,automaton,environment);
		} else if(expr instanceof Code.Load) {
			r = translate((Code.Load) expr,automaton,environment);
		} else if(expr instanceof Code.Is) {
			r = translate((Code.Is) expr,automaton,environment);
		} else if(expr instanceof Code.Quantifier) {
			r = translate((Code.Quantifier) expr,automaton,environment);
		} else if(expr instanceof Code.FunCall) {
			r = translate((Code.FunCall) expr,automaton,environment);
		} else {
			internalFailure("unknown: " + expr.getClass().getName(),
					filename, expr);
			return -1; // dead code
		}

		//debug(automaton,r);
		return r;
	}

	private int translate(Code.Constant expr, Automaton automaton, HashMap<String,Integer> environment) {
		return convert(expr.value,expr,automaton);
	}

	private int translate(Code.Cast expr, Automaton automaton, HashMap<String,Integer> environment) {
		int e = translate(expr.operands[0],automaton,environment);
		// FIXME: implement cast expressions!
		return e;
	}
	
	private int translate(Code.Variable code, Automaton automaton, HashMap<String,Integer> environment) {
		if(code.operands.length > 0) {
			throw new RuntimeException("need to add support for variables with sub-components");
		}
		// TODO: just use an integer for variables directly
		String name = "r" + code.index;
		Integer idx = environment.get(name);
		// FIXME: need to handle code.operands as well!
		if(idx == null) {
			// FIXME: this is a hack to work around modified operands after a
			// loop.
			return Var(automaton,name);
		} else {
			return idx;
		}
	}

	private int translate(Code.Binary code, Automaton automaton, HashMap<String,Integer> environment) {
		int lhs = translate(code.operands[0],automaton,environment);
		int rhs = translate(code.operands[1],automaton,environment);

		int type = convert(automaton,code.type);

		switch(code.opcode) {
		case ADD:
			return SolverUtil.Add(automaton,lhs,rhs);
		case SUB:
			return SolverUtil.Sub(automaton,lhs,rhs);
		case MUL:
			return SolverUtil.Mul(automaton, lhs, rhs);
		case DIV:
			return SolverUtil.Div(automaton, lhs, rhs);
		case REM:
			return automaton.add(False);
		case EQ:
			return SolverUtil.Equals(automaton, type, lhs, rhs);
		case NEQ:
			return Not(automaton, SolverUtil.Equals(automaton, type, lhs, rhs));
		case LT:
			return SolverUtil.LessThan(automaton, type, lhs, rhs);
		case LTEQ:
			return SolverUtil.LessThanEq(automaton, type, lhs, rhs);
		case IN:
			return SubsetEq(automaton, type, Set(automaton, lhs), rhs);
		case SUBSET:
			return And(automaton,
					SubsetEq(automaton, type, lhs, rhs),
					Not(automaton, SolverUtil.Equals(automaton, type, lhs, rhs)));
		case SUBSETEQ:
			return SubsetEq(automaton, type, lhs, rhs);
		}
		internalFailure("unknown binary bytecode encountered (" + code + ")",
				filename, code);
		return -1;
	}

	private int translate(Code.Unary code, Automaton automaton, HashMap<String,Integer> environment) {
		int e = translate(code.operands[0],automaton,environment);
		switch(code.opcode) {
		case NOT:
			return Not(automaton, e);
		case NEG:
			return SolverUtil.Neg(automaton, e);
		case LENGTH:
			return LengthOf(automaton, e);
		}
		internalFailure("unknown unary bytecode encountered (" + code + ")",
				filename, code);
		return -1;
	}

	private int translate(Code.Nary code, Automaton automaton, HashMap<String,Integer> environment) {
		Code[] operands = code.operands;
		int[] es = new int[operands.length];
		for(int i=0;i!=es.length;++i) {
			es[i] = translate(operands[i],automaton,environment);
		}
		switch(code.opcode) {
		case AND:
			return And(automaton,es);
		case OR:
			return Or(automaton,es);
		case SET:
			return Set(automaton,es);
		case TUPLE:
			return Tuple(automaton,es);
		}
		internalFailure("unknown nary expression encountered (" + code + ")",
				filename, code);
		return -1;
	}

	private int translate(Code.Load code, Automaton automaton, HashMap<String,Integer> environment) {
		int e = translate(code.operands[0],automaton,environment);
		int i = automaton.add(new Automaton.Int(code.index));
		return Solver.Load(automaton,e,i);
	}

	private int translate(Code.Is code, Automaton automaton, HashMap<String,Integer> environment) {
		int e = translate(code.operands[0],automaton,environment);
		int t = convert(automaton,code.test);
		return Solver.Is(automaton,e,t);
	}
	
	private int translate(Code.FunCall code, Automaton automaton,
			HashMap<String, Integer> environment) {
		// uninterpreted function call
		int argument = translate(code.operands[0], automaton, environment);
		int[] es = new int[] {
				automaton.add(new Automaton.Strung(code.nid.toString())),
				argument };
		return Fn(automaton, es);
	}

	private int translate(Code.Quantifier code, Automaton automaton, HashMap<String,Integer> environment) {
		HashMap<String,Integer> nEnvironment = new HashMap<String,Integer>(environment);
		Pair<SemanticType,Integer>[] variables = code.types;
		int[] vars = new int[variables.length];
		
		//int[] typeTests = new int[variables.length];
		for (int i = 0; i != variables.length; ++i) {
			Pair<SemanticType,Integer> p = variables[i];
			// First, construct variable pairs
			SemanticType type = p.first();
			String var = "r" + p.second();
			int varIdx = Var(automaton, var);
			nEnvironment.put(var, varIdx);
			int typeIdx = convert(automaton,type);
			vars[i] = automaton.add(new Automaton.List(varIdx, typeIdx));
			// Second, construct the appropriate type test for each variable
			//typeTests[i] = Solver.Is(automaton,varIdx,convert(automaton,type));
		}

		int avars = automaton.add(new Automaton.Set(vars));
		//int typeTest = And(automaton,typeTests);
		int quantifiedExpression = translate(code.operands[0], automaton, nEnvironment);
				
		if(code.opcode == Code.Op.FORALL) {
			//return ForAll(automaton, avars, SolverUtil.Implies(automaton,typeTest, quantifiedExpression));
			return ForAll(automaton, avars, quantifiedExpression);
		} else {
			//return Exists(automaton, avars, And(automaton,typeTest, quantifiedExpression));
			return Exists(automaton, avars, quantifiedExpression);
		}
	}

	/**
	 * Convert between a WYIL value and a WYRL value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 *
	 * @param value
	 * @return
	 */
	private int convert(Value value, SyntacticElement element, Automaton automaton) {

		if (value instanceof Value.Null) {
			Value.Null b = (Value.Null) value;
			return automaton.add(Null);
		} else if (value instanceof Value.Bool) {
			Value.Bool b = (Value.Bool) value;
			return b.value ? automaton.add(True) : automaton.add(False);
		} else if (value instanceof Value.Integer) {
			Value.Integer v = (Value.Integer) value;
			return Num(automaton , BigRational.valueOf(v.value));
		} else if (value instanceof Value.Decimal) {
			Value.Decimal v = (Value.Decimal) value;
			return Num(automaton, new BigRational(v.value));
		} else if (value instanceof Value.String) {
			Value.String v = (Value.String) value;
			return Solver.String(automaton,v.value);
		} else if (value instanceof Value.Set) {
			Value.Set vs = (Value.Set) value;
			int[] vals = new int[vs.values.size()];
			int i = 0;
			for (Value c : vs.values) {
				vals[i++] = convert(c,element,automaton);
			}
			return Set(automaton , vals);
		} else if (value instanceof Value.Tuple) {
			Value.Tuple vt = (Value.Tuple) value;
			int[] vals = new int[vt.values.size()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = convert(vt.values.get(i),element,automaton);
			}
			return Tuple(automaton , vals);
		} else {
			internalFailure("unknown value encountered (" + value + ", " + value.getClass().getName() + ")",
					filename,element);
			return -1;
		}
	}


	/**
	 * Construct an automaton node representing a given semantic type.
	 *
	 * @param automaton
	 * @param type --- to be converted.
	 * @return the index of the new node.
	 */
	public int convert(Automaton automaton, SemanticType type) {
		Automaton type_automaton = type.automaton();
		// The following is important to make sure that the type is in minimised
		// form before verification begins. This firstly reduces the amount of
		// work during verification, and also allows the functions in
		// SolverUtils to work properly.
		Rewriter rewriter = createRewriter(type_automaton);
		rewriter.apply();
		return automaton.addAll(type_automaton.getRoot(0), type_automaton);
	}

	public static void debug(Automaton automaton) {
		try {
			// System.out.println(automaton);
			PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,
					SCHEMA, "Or", "And");
			writer.write(automaton);
			writer.flush();
		} catch(IOException e) {
			System.out.println("I/O Exception - " + e);
		}
	}

	public static void debug(Code code, String filename) {
		ArrayList<WycsFile.Declaration> tmpDecls = new ArrayList();
		tmpDecls.add(new WycsFile.Assert("", code));
		WycsFile tmp = new WycsFile(Trie.ROOT, filename, tmpDecls);
		try {
			new WycsFilePrinter(System.err).write(tmp);
		} catch (IOException e) {
		}
	}

	public static class AssertionFailure extends RuntimeException {
		private final WycsFile.Assert assertion;
		private final Rewriter rewriter;
		private final Automaton reduced;
		private final Automaton original;

		public AssertionFailure(String msg, WycsFile.Assert assertion,
				Rewriter rewriter, Automaton reduced, Automaton original) {
			super(msg);
			this.assertion = assertion;
			this.rewriter = rewriter;
			this.reduced = reduced;
			this.original = original;
		}

		public WycsFile.Assert assertion() {
			return assertion;
		}

		public Rewriter rewriter() {
			return rewriter;
		}

		public Automaton reduction() {
			return reduced;
		}

		public Automaton original() {
			return original;
		}
	}


	// =============================================================================
	// Axiom Instantiation
	// =============================================================================


	/**
	 * Blindly instantiate all axioms. Note, this function is assuming the
	 * verification condition has already been negated for
	 * proof-by-contradiction and converted into Negation Normal Form.
	 *
	 * @param condition
	 *            Condition over which all axioms should be instantiated.
	 * @param freeVariable
	 *            First available free variable which can be used when
	 *            instantiating axioms.
	 * @return
	 */
	public Code instantiateAxioms(Code condition, int freeVariable) {
		if (condition instanceof Code.Variable || condition instanceof Code.Constant) {
			// do nothing
			return condition;
		} else if (condition instanceof Code.Unary) {
			return instantiateAxioms((Code.Unary)condition, freeVariable);
		} else if (condition instanceof Code.Binary) {
			return instantiateAxioms((Code.Binary)condition, freeVariable);
		} else if (condition instanceof Code.Nary) {
			return instantiateAxioms((Code.Nary)condition, freeVariable);
		} else if (condition instanceof Code.Quantifier) {
			return instantiateAxioms((Code.Quantifier)condition, freeVariable);
		} else if (condition instanceof Code.FunCall) {
			return instantiateAxioms((Code.FunCall)condition, freeVariable);
		} else if (condition instanceof Code.Load) {
			return instantiateAxioms((Code.Load)condition, freeVariable);
		} else if (condition instanceof Code.Is) {
			return instantiateAxioms((Code.Is)condition, freeVariable);
		} else {
			internalFailure("invalid boolean expression encountered (" + condition
					+ ")", filename, condition);
			return null;
		}
	}

	private Code instantiateAxioms(Code.Unary condition, int freeVariable) {
		switch(condition.opcode) {
		case NOT:
			return Code.Unary(condition.type, condition.opcode,
					instantiateAxioms(condition.operands[0], freeVariable), condition.attributes());
		default:
			internalFailure("invalid boolean expression encountered (" + condition
					+ ")", filename, condition);
			return null;
		}
	}

	private Code instantiateAxioms(Code.Binary condition, int freeVariable) {
		switch (condition.opcode) {
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ: {
			ArrayList<Code> axioms = new ArrayList<Code>();
			instantiateFromExpression(condition, axioms, freeVariable);
			return and(axioms,condition);
		}
		default:
			internalFailure("invalid boolean expression encountered (" + condition
					+ ")", filename, condition);
			return null;
		}
	}

	private Code instantiateAxioms(Code.Nary condition, int freeVariable) {
		switch(condition.opcode) {
		case AND:
		case OR: {
			Code[] e_operands = new Code[condition.operands.length];
			for(int i=0;i!=e_operands.length;++i) {
				e_operands[i] = instantiateAxioms(condition.operands[i], freeVariable);
			}
			return Code.Nary(condition.type, condition.opcode, e_operands, condition.attributes());
		}
		default:
			internalFailure("invalid boolean expression encountered (" + condition
					+ ")", filename, condition);
			return null;
		}
	}

	private Code instantiateAxioms(Code.Is condition, int freeVariable) {
		ArrayList<Code> axioms = new ArrayList<Code>();
		instantiateFromExpression(condition.operands[0], axioms, freeVariable);
		return and(axioms,condition);
	}
	
	private Code instantiateAxioms(Code.Quantifier condition, int freeVariable) {
		return Code.Quantifier(condition.type, condition.opcode,
				instantiateAxioms(condition.operands[0], freeVariable), condition.types, condition.attributes());
	}

	private Code instantiateAxioms(Code.FunCall condition, int freeVariable) {
		ArrayList<Code> axioms = new ArrayList<Code>();
		try {
			WycsFile module = builder.getModule(condition.nid.module());
			// module should not be null if TypePropagation has already passed.
			Object d = module.declaration(condition.nid.name());
			if(d instanceof WycsFile.Function) {
				WycsFile.Function fn = (WycsFile.Function) d;
				if(fn.constraint != null) {
					// There are some axioms we can instantiate. First, we need to
					// construct the generic binding for this function.
					HashMap<String,SemanticType> generics = buildGenericBinding(fn.type.generics(),condition.binding);
					Code axiom = renameToAvoidCapture(fn.constraint,freeVariable);
					HashMap<Integer,Code> binding = new HashMap<Integer,Code>();
					binding.put(1, condition.operands[0]);
					binding.put(0, condition);
					axiom = axiom.substitute(binding).instantiate(
							generics);
					axioms.add(axiom);
				}
			} else if(d instanceof WycsFile.Macro){
				// we can ignore macros, because they are inlined separately by
				// MacroExpansion.
			} else {
				internalFailure("cannot resolve as function or macro call",
						filename, condition);
			}
		} catch(Exception ex) {
			internalFailure(ex.getMessage(), filename, condition, ex);
		}

		instantiateFromExpression(condition.operands[0], axioms, freeVariable);
		return and(axioms,condition);
	}

	private HashMap<String, SemanticType> buildGenericBinding(
			SemanticType[] from, SemanticType[] to) {
		HashMap<String, SemanticType> binding = new HashMap<String, SemanticType>();
		for (int i = 0; i != to.length; ++i) {
			SemanticType.Var v = (SemanticType.Var) from[i];
			binding.put(v.name(), to[i]);
		}
		return binding;
	}

	private Code instantiateAxioms(Code.Load condition, int freeVariable) {
		return Code.Load(condition.type, instantiateAxioms(condition.operands[0], freeVariable), condition.index,
				condition.attributes());
	}

	private void instantiateFromExpression(Code expression, ArrayList<Code> axioms, int freeVariable) {
		if (expression instanceof Code.Variable || expression instanceof Code.Constant) {
			// do nothing
		} else if (expression instanceof Code.Cast) {
			instantiateFromExpression((Code.Cast)expression,axioms, freeVariable);
		} else if (expression instanceof Code.Unary) {
			instantiateFromExpression((Code.Unary)expression,axioms, freeVariable);
		} else if (expression instanceof Code.Binary) {
			instantiateFromExpression((Code.Binary)expression,axioms, freeVariable);
		} else if (expression instanceof Code.Nary) {
			instantiateFromExpression((Code.Nary)expression,axioms, freeVariable);
		} else if (expression instanceof Code.Load) {
			instantiateFromExpression((Code.Load)expression,axioms, freeVariable);
		} else if (expression instanceof Code.Is) {
			instantiateFromExpression((Code.Is)expression,axioms, freeVariable);
		} else if (expression instanceof Code.FunCall) {
			instantiateFromExpression((Code.FunCall)expression,axioms, freeVariable);
		} else {
			internalFailure("invalid expression encountered (" + expression
					+ ", " + expression.getClass().getName() + ")", filename, expression);
		}
	}

	private void instantiateFromExpression(Code.Cast expression, ArrayList<Code> axioms, int freeVariable) {
		instantiateFromExpression(expression.operands[0],axioms, freeVariable);
	}
	
	private void instantiateFromExpression(Code.Unary expression, ArrayList<Code> axioms, int freeVariable) {
		instantiateFromExpression(expression.operands[0],axioms, freeVariable);

		if(expression.opcode == Code.Op.LENGTH) {
			Code lez = Code.Binary(SemanticType.Int, Code.Op.LTEQ,
					Code.Constant(Value.Integer(BigInteger.ZERO)), expression);
			axioms.add(lez);
		}
	}

	private void instantiateFromExpression(Code.Binary expression, ArrayList<Code> axioms, int freeVariable) {
		instantiateFromExpression(expression.operands[0],axioms, freeVariable);
		instantiateFromExpression(expression.operands[1],axioms, freeVariable);
	}

	private void instantiateFromExpression(Code.Nary expression, ArrayList<Code> axioms, int freeVariable) {
		Code[] e_operands = expression.operands;
		for(int i=0;i!=e_operands.length;++i) {
			instantiateFromExpression(e_operands[i],axioms, freeVariable);
		}
	}

	private void instantiateFromExpression(Code.Load expression, ArrayList<Code> axioms, int freeVariable) {
		instantiateFromExpression(expression.operands[0],axioms, freeVariable);
	}

	private void instantiateFromExpression(Code.Is expression, ArrayList<Code> axioms, int freeVariable) {
		instantiateFromExpression(expression.operands[0],axioms, freeVariable);
	}
	
	private void instantiateFromExpression(Code.FunCall expression, ArrayList<Code> axioms, int freeVariable) {
		instantiateFromExpression(expression.operands[0], axioms, freeVariable);

		try {
			WycsFile module = builder.getModule(expression.nid.module());
			// module should not be null if TypePropagation has already passed.
			WycsFile.Function fn = module.declaration(expression.nid.name(),
					WycsFile.Function.class);
			if (fn.constraint != null) {
				// There are some axioms we can instantiate. First, we need to
				// construct the generic binding for this function.
				HashMap<String, SemanticType> generics = buildGenericBinding(
						fn.type.generics(), expression.binding);
				Code axiom = renameToAvoidCapture(fn.constraint,freeVariable);
				HashMap<Integer,Code> binding = new HashMap<Integer,Code>();
				binding.put(1, expression.operands[0]);
				binding.put(0, expression);
				axiom = axiom.substitute(binding).instantiate(
						generics);
				axioms.add(axiom);
			}
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, expression, ex);
		}
	}

	private Code renameToAvoidCapture(Code code, int freeVariable) {
		HashSet<Pair<SemanticType,Integer>> usedVariables = new HashSet();
		findUsedVariables(code,usedVariables);
		return NormalForms.renameVariables(code,freeVariable);
	}

	/**
	 * Determine the largest variable index within a given bytecode. This is
	 * useful to avoid variable capture when importing axioms.
	 *
	 * @param condition
	 * @return
	 */
	private int findLargestVariable(Code condition) {
		if (condition instanceof Code.Variable) {
			// do nothing
			Code.Variable v = (Code.Variable) condition;
			return v.index;
		} else {
			int max = -1;
			for(Code c : condition.operands) {
				max = Math.max(findLargestVariable(c),max);
			}
			return max;
		}
	}

	/**
	 * Determine the set of used variables within a given bytecode. This is
	 * useful to avoid variable capture when importing axioms.
	 *
	 * @param condition
	 * @return
	 */
	private void findUsedVariables(Code condition, HashSet<Pair<SemanticType,Integer>> vars) {
		if (condition instanceof Code.Variable) {
			// do nothing
			Code.Variable v = (Code.Variable) condition;
			vars.add(new Pair<SemanticType,Integer>(v.type,v.index));
		} else {
			for(Code c : condition.operands) {
				findUsedVariables(c,vars);
			}
		}
	}

 	private Code and(ArrayList<Code> axioms, Code c) {
		if(axioms.size() == 0) {
			return c;
		} else {
			Code[] clauses = new Code[axioms.size()+1];
			clauses[0] = c;
			for(int i=0;i!=axioms.size();++i) {
				clauses[i+1] = axioms.get(i);
			}
			return Code.Nary(SemanticType.Bool,Code.Op.AND,clauses);
		}
	}

	private Rewriter createRewriter(Automaton automaton) {
		IterativeRewriter.Strategy<InferenceRule> inferenceStrategy;
		IterativeRewriter.Strategy<ReductionRule> reductionStrategy;

		// First, construct a fresh rewriter for this file.
		switch(rwMode) {
		case STATICDISPATCH:
			inferenceStrategy = new UnfairStateRuleRewriteStrategy<InferenceRule>(
					automaton, Solver.inferences,Solver.SCHEMA);
			reductionStrategy = new UnfairStateRuleRewriteStrategy<ReductionRule>(
					automaton, Solver.reductions,Solver.SCHEMA);
			break;
		case GLOBALDISPATCH:
			// NOTE: I don't supply a max steps value here because the
			// default value would be way too small for the simple rewriter.
			inferenceStrategy = new UnfairRuleStateRewriteStrategy<InferenceRule>(
					automaton, Solver.inferences);
			reductionStrategy = new UnfairRuleStateRewriteStrategy<ReductionRule>(
					automaton, Solver.reductions);
			break;
		default:
			// NOTE: I don't supply a max steps value here because the
			// default value would be way too small for the simple rewriter.
			inferenceStrategy = new SimpleRewriteStrategy<InferenceRule>(
					automaton, Solver.inferences);
			reductionStrategy = new SimpleRewriteStrategy<ReductionRule>(
					automaton, Solver.reductions);
			break;
		}

		IterativeRewriter rewriter = new IterativeRewriter(automaton, inferenceStrategy,
				reductionStrategy, Solver.SCHEMA);

		rewriter.setMaxReductionSteps(maxReductions);
		rewriter.setMaxInferenceSteps(maxInferences);

		return rewriter;
	}
}
