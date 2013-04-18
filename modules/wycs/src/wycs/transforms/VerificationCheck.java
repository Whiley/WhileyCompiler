package wycs.transforms;

import static wybs.lang.SyntaxError.*;
import static wycs.solver.Solver.*;

import java.io.IOException;
import java.util.*;

import wyautl.core.*;
import wyautl.io.PrettyAutomataWriter;
import wyautl.util.BigRational;
import wybs.lang.Builder;
import wybs.lang.Logger;
import wybs.lang.SyntacticElement;
import wybs.lang.Transform;
import wybs.util.Pair;
import wybs.util.Trie;
import wybs.util.Triple;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.Code;
import wycs.core.NormalForms;
import wycs.core.SemanticType;
import wycs.core.Value;
import wycs.core.WycsFile;
import wycs.io.WycsFilePrinter;
import wycs.solver.Solver;

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
	
	/**
	 * Determines whether this transform is enabled or not.
	 */
	private boolean enabled = getEnable();

	/**
	 * Determines whether debugging is enabled or not
	 */
	private boolean debug = getDebug();
	
	private Logger logger;
	
	private String filename;
	
	// ======================================================================
	// Constructor(s)
	// ======================================================================

	public VerificationCheck(Builder builder) {
		if(builder instanceof Logger) {
			this.logger = (Logger) builder;
		} else {
			this.logger = Logger.NULL;
		}
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
			
			List<WycsFile.Declaration> statements = wf.declarations();
			int count = 0;
			for (int i = 0; i != statements.size(); ++i) {
				WycsFile.Declaration stmt = statements.get(i);

				if (stmt instanceof WycsFile.Assert) {
					checkValid((WycsFile.Assert) stmt, ++count);
				} else if (stmt instanceof WycsFile.Function
						|| stmt instanceof WycsFile.Macro) {
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
		
		Code nnf = NormalForms.negationNormalForm(Code.Unary(SemanticType.Bool,
				Code.Op.NOT, stmt.condition));
		Code pnf = NormalForms.prefixNormalForm(nnf);		
		int assertion = translate(pnf,automaton,new HashMap<String,Integer>());
		//int assertion = translate(stmt.condition,automaton,new HashMap<String,Integer>());

		automaton.setRoot(0, assertion);
		// automaton.setRoot(0, Not(automaton, assertion));
		automaton.minimise();
		
		if (debug) {				
			ArrayList<WycsFile.Declaration> tmpDecls = new ArrayList();
			tmpDecls.add(new WycsFile.Assert("", pnf));
			WycsFile tmp = new WycsFile(Trie.ROOT,filename, tmpDecls);
			try {
				new WycsFilePrinter(System.err).write(tmp);
			} catch(IOException e) {}
			original = new Automaton(automaton);				
		}

		infer(automaton);
	
		if(!automaton.get(automaton.getRoot(0)).equals(Solver.False)) {
			String msg = stmt.message;
			msg = msg == null ? "assertion failure" : msg;
			throw new AssertionFailure(msg,stmt,automaton,original);
		}		
		
		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("[" + filename + "] Verified assertion #" + number,
				endTime - startTime, startMemory - runtime.freeMemory());		
	}
	
	private int translate(Code expr, Automaton automaton, HashMap<String,Integer> environment) {
		if(expr instanceof Code.Constant) {
			return translate((Code.Constant) expr,automaton,environment);
		} else if(expr instanceof Code.Variable) {
			return translate((Code.Variable) expr,automaton,environment);
		} else if(expr instanceof Code.Binary) {
			return translate((Code.Binary) expr,automaton,environment);
		} else if(expr instanceof Code.Unary) {
			return translate((Code.Unary) expr,automaton,environment);
		} else if(expr instanceof Code.Nary) {
			return translate((Code.Nary) expr,automaton,environment);
		} else if(expr instanceof Code.Load) {
			return translate((Code.Load) expr,automaton,environment);
		} else if(expr instanceof Code.Quantifier) {
			return translate((Code.Quantifier) expr,automaton,environment);
		} else if(expr instanceof Code.FunCall) {
			return translate((Code.FunCall) expr,automaton,environment);
		} else {
			internalFailure("unknown: " + expr.getClass().getName(),
					filename, expr);
			return -1; // dead code
		}
	}
	
	private int translate(Code.Constant expr, Automaton automaton, HashMap<String,Integer> environment) {
		return convert(expr.value,expr,automaton);
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
		SemanticType lhs_t = code.operands[0].type;
		SemanticType rhs_t = code.operands[1].type;
		boolean isInt = lhs_t instanceof SemanticType.Int
				&& rhs_t instanceof SemanticType.Int;
		
		switch(code.opcode) {		
		case ADD:
			return Sum(automaton, automaton.add(new Automaton.Real(0)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
		case SUB:
			return Sum(automaton, automaton.add(new Automaton.Real(0)),
					automaton.add(new Automaton.Bag(lhs, Mul(automaton,
							automaton.add(new Automaton.Real(-1)),
							automaton.add(new Automaton.Bag(rhs))))));
		case MUL:
			return Mul(automaton, automaton.add(new Automaton.Real(1)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
		case DIV:
			return Div(automaton, lhs, rhs);
		case REM:
			return automaton.add(False);
		case EQ:
			return Equals(automaton, lhs, rhs);
		case NEQ:
			if(isInt) {
				// FIXME: this could be improved!
				int l = LessThanEq(
						automaton,
						lhs,
						Sum(automaton, automaton.add(new Automaton.Real(-1)),
								automaton.add(new Automaton.Bag(rhs))));
				int r = LessThanEq(
						automaton,
						rhs,
						Sum(automaton, automaton.add(new Automaton.Real(-1)),
								automaton.add(new Automaton.Bag(lhs))));
				return Or(automaton, l, r);
			} else {
				return Not(automaton, Equals(automaton, lhs, rhs));
			}
		case LT:
			if(isInt) {
				lhs = Sum(automaton, automaton.add(new Automaton.Real(1)),
								automaton.add(new Automaton.Bag(lhs)));
				return LessThanEq(automaton, lhs, rhs);
			} else {
				return LessThan(automaton, lhs, rhs);
			}
		case LTEQ:
			return LessThanEq(automaton, lhs, rhs);
		case IN:
			return SubsetEq(automaton, Set(automaton, lhs), rhs);
		case SUBSET:
			return And(automaton,SubsetEq(automaton, lhs, rhs),Not(automaton,Equals(automaton,lhs,rhs)));
		case SUBSETEQ:
			return SubsetEq(automaton, lhs, rhs);							
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
			return Mul(automaton, automaton.add(new Automaton.Real(-1)),
					automaton.add(new Automaton.Bag(e)));
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
		return TupleLoad(automaton,e,i);
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
		for (int i = 0; i != variables.length; ++i) {
			Pair<SemanticType,Integer> p = variables[i];
			SemanticType type = p.first();
			String var = "r" + p.second();
			int varIdx = Var(automaton, var);
			nEnvironment.put(var, varIdx);
			int srcIdx;
			// FIXME: generate actual type of variable here
			srcIdx = automaton.add(AnyT);			
			vars[i] = automaton.add(new Automaton.List(varIdx, srcIdx));
		}

		int avars = automaton.add(new Automaton.Set(vars));
		
		return ForAll(automaton, avars, translate(code.operands[0], automaton, nEnvironment));
	}		
	
	/**
	 * Convert between a WYIL value and a WYONE value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 * 
	 * @param value
	 * @return
	 */
	private int convert(Value value, SyntacticElement element, Automaton automaton) {
		
		if (value instanceof Value.Bool) {
			Value.Bool b = (Value.Bool) value;
			return b.value ? automaton.add(True) : automaton.add(False);
		} else if (value instanceof Value.Integer) {
			Value.Integer v = (Value.Integer) value;
			return Num(automaton , BigRational.valueOf(v.value));
		} else if (value instanceof Value.Rational) {
			Value.Rational v = (Value.Rational) value;
			wyautl.util.BigRational br = v.value;
			return Num(automaton ,
					new BigRational(br.numerator(), br.denominator()));
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
	
	public static class AssertionFailure extends RuntimeException {
		private final WycsFile.Assert assertion;
		private final Automaton reduced;
		private final Automaton original;
		
		public AssertionFailure(String msg, WycsFile.Assert assertion,
				Automaton reduced, Automaton original) {
			super(msg);
			this.assertion = assertion;
			this.reduced = reduced;
			this.original = original;
		}
		
		public WycsFile.Assert assertion() {
			return assertion;
		}
		
		public Automaton reduction() {
			return reduced;
		}
		
		public Automaton original() {
			return original;
		}
	}
}
