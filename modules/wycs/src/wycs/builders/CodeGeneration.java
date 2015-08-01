package wycs.builders;

import java.math.BigInteger;
import java.util.*;

import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycc.util.Triple;
import wycs.core.*;
import wycs.syntax.*;
import wyfs.lang.Path;
import wyfs.util.Trie;
import static wycc.lang.SyntaxError.*;
import static wycs.transforms.TypePropagation.returnType;

/**
 * Responsible for translating an individual <code>WyalFile</code> into a
 * <code>WycsFile</code>. By the time this is run, both type propagation and
 * constraint expansion (for uninterpreted functions) must already have
 * occurred. In most cases, the translation is straightforward as there is a
 * one-one correspondence between many Wyal and Wycs constructs. However, some
 * differences exist such as, for example, the lack of an implication statement
 * and any notion of a list.
 *
 * @author David J. Pearce
 *
 */
public class CodeGeneration {
	private final Wyal2WycsBuilder builder;
	private String filename;

	public CodeGeneration(Wyal2WycsBuilder builder) {
		this.builder = builder;
	}

	public WycsFile generate(WyalFile file) {
		this.filename = file.filename();
		ArrayList<WycsFile.Declaration> declarations = new ArrayList();
		for(WyalFile.Declaration d : file.declarations()) {
			WycsFile.Declaration e = generate(d);
			if(e != null) {
				declarations.add(e);
			}
		}
		return new WycsFile(file.id(),file.filename(),declarations);
	}

	protected WycsFile.Declaration generate(WyalFile.Declaration declaration) {
		if(declaration instanceof WyalFile.Import) {
			// not sure what to do here?
			return null;
		} else if(declaration instanceof WyalFile.Macro) {
			return generate((WyalFile.Macro)declaration);
		} else if(declaration instanceof WyalFile.Function) {
			return generate((WyalFile.Function)declaration);
		} else if(declaration instanceof WyalFile.Type) {
			return generate((WyalFile.Type)declaration);
		} else if(declaration instanceof WyalFile.Assert) {
			return generate((WyalFile.Assert)declaration);
		} else {
			internalFailure("unknown declaration encounterd",filename,declaration);
			return null;
		}
	}

	protected WycsFile.Declaration generate(WyalFile.Macro d) {
		try {
			// First, determine function type
			SemanticType from = builder.convert(d.from, d.generics, d);
			SemanticType to = SemanticType.Bool;
			SemanticType.Var[] generics = new SemanticType.Var[d.generics.size()];
			for (int i = 0; i != generics.length; ++i) {
				generics[i] = SemanticType.Var(d.generics.get(i));
			}
			SemanticType.Function type = SemanticType.Function(from, to, generics);
			// Second, generate macro body
			HashMap<String,Code> environment = new HashMap<String,Code>();
			Code parameter = Code.Variable(from, new Code[0], 0,
					attributes(d.from));
			addDeclaredVariables(parameter,d.from,environment);
			Code condition = generate(d.body, environment, d);
			// Third, create declaration
			return new WycsFile.Macro(d.name, type, condition,
					attributes(d));
		} catch (ResolveError re) {
			// should be unreachable if type propagation is already succeeded.
			syntaxError("cannot resolve as function or definition call",
					filename, d, re);
			return null;
		}
	}

	protected WycsFile.Declaration generate(WyalFile.Function d) {
		try {
			// First, determine function type
			SemanticType from = builder.convert(d.from, d.generics, d);
			SemanticType to = builder.convert(d.to, d.generics, d);
			SemanticType.Var[] generics = new SemanticType.Var[d.generics.size()];
			for (int i = 0; i != generics.length; ++i) {
				generics[i] = SemanticType.Var(d.generics.get(i));
			}
			SemanticType.Function type = SemanticType.Function(from, to, generics);
			// Second, generate function condition (if applicable)
			Code condition = null;
			if (d.constraint != null) {
				HashMap<String,Code> environment = new HashMap<String,Code>();
				Code ret = Code.Variable(to, new Code[0], 0,
						attributes(d.to));
				Code parameter = Code.Variable(from, new Code[0], 1,
						attributes(d.from));
				addDeclaredVariables(parameter,d.from,environment);
				addDeclaredVariables(ret,d.to,environment);
				condition = generate(d.constraint, environment, d);
			}
			// Third, create declaration
			return new WycsFile.Function(d.name, type, condition,
					attributes(d));
		} catch (ResolveError re) {
			// should be unreachable if type propagation is already succeeded.
			syntaxError("cannot resolve as function or definition call",
					filename, d, re);
			return null;
		}
	}

	protected WycsFile.Declaration generate(WyalFile.Type d) {
		try {
			SemanticType from = builder.convert(d.type, d.generics, d);		
			SemanticType.Var[] generics = new SemanticType.Var[d.generics.size()];
			for (int i = 0; i != generics.length; ++i) {
				generics[i] = SemanticType.Var(d.generics.get(i));
			}		
			// Second, generate type invariant (if applicable)
			Code invariant = null;
			if (d.invariant != null) {
				HashMap<String,Code> environment = new HashMap<String,Code>();
				Code parameter = Code.Variable(from, new Code[0], 0,
						attributes(d.type));
				addDeclaredVariables(parameter,d.type,environment);			
				invariant = generate(d.invariant, environment, d);
			}
			// 
			return new WycsFile.Type(d.name, from, invariant, attributes(d));
		} catch (ResolveError re) {
			// should be unreachable if type propagation is already succeeded.
			syntaxError("cannot resolve as function or definition call",
					filename, d, re);
			return null;
		}
	}
	
	protected void addDeclaredVariables(Code root, TypePattern t,
			HashMap<String, Code> environment) {

		if(t instanceof TypePattern.Leaf) {
			TypePattern.Leaf tl = (TypePattern.Leaf) t;
			if(tl.var != null) {
				environment.put(tl.var.name, root);
			}
		} else if(t instanceof TypePattern.Rational) {
			// TODO: implement me!
		} else if(t instanceof TypePattern.Tuple) {
			TypePattern.Tuple tt = (TypePattern.Tuple) t;
			for (int i = 0; i != tt.elements.size(); ++i) {
				TypePattern p = tt.elements.get(i);
				addDeclaredVariables(
						Code.Load((SemanticType.Tuple) root.type, root, i,
								attributes(t)), p,
						environment);
			}
		} else if(t instanceof TypePattern.Record) {
			// TODO: implement me!
		} else if(t instanceof TypePattern.Intersection) {
			// TODO: implement me!
		} else if(t instanceof TypePattern.Union) {
			// TODO: implement me!
		}
	}

	protected WycsFile.Declaration generate(WyalFile.Assert d) {
		Code condition = generate(d.expr, new HashMap<String,Code>(),d);
		return new WycsFile.Assert(d.message, condition, attributes(d));
	}

	protected Code generate(Expr e, HashMap<String,Code> environment, WyalFile.Context context) {
		if (e instanceof Expr.Variable) {
			return generate((Expr.Variable) e, environment, context);
		} else if (e instanceof Expr.Constant) {
			return generate((Expr.Constant) e, environment, context);
		} else if (e instanceof Expr.Cast) {
			return generate((Expr.Cast) e, environment, context);
		} else if (e instanceof Expr.Unary) {
			return generate((Expr.Unary) e, environment, context);
		} else if (e instanceof Expr.Binary) {
			return generate((Expr.Binary) e, environment, context);
		} else if (e instanceof Expr.Nary) {
			return generate((Expr.Nary) e, environment, context);
		} else if (e instanceof Expr.Quantifier) {
			return generate((Expr.Quantifier) e, environment, context);
		} else if (e instanceof Expr.Invoke) {
			return generate((Expr.Invoke) e, environment, context);
		} else if (e instanceof Expr.IndexOf) {
			return generate((Expr.IndexOf) e, environment, context);
		} else if (e instanceof Expr.Is) {
			return generate((Expr.Is) e, environment, context);
		} else {
			internalFailure("unknown expression encountered (" + e + ")",
					filename, e);
			return null;
		}
	}

	protected Code generate(Expr.Variable e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		return environment.get(e.name);
	}

	protected Code generate(Expr.Constant v, HashMap<String,Code> environment, WyalFile.Context context) {
		return Code.Constant(v.value,
				attributes(v));
	}

	protected Code generate(Expr.Cast e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType type = e.operand.attribute(TypeAttribute.class).type;
		SemanticType target = e.attribute(TypeAttribute.class).type;
		Code operand = generate(e.operand,environment, context);
		return Code.Cast(type,operand,target,attributes(e));
	}
	
	protected Code generate(Expr.Unary e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code operand = generate(e.operand,environment, context);
		Code.Op opcode;
		switch(e.op) {
		case NEG:
			opcode = Code.Op.NEG;
			break;
		case NOT:
			opcode = Code.Op.NOT;
			break;
		case LENGTHOF:
			opcode = Code.Op.LENGTH;
			break;
		default:
			internalFailure("unknown unary opcode encountered (" + e + ")",
					filename, e);
			return null;
		}
		return Code.Unary(type, opcode, operand,
				attributes(e));
	}

	protected Code generate(Expr.Binary e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code lhs = generate(e.leftOperand,environment, context);
		Code rhs = generate(e.rightOperand,environment, context);
		Code.Op opcode;
		switch(e.op) {
		case AND:
			return Code.Nary(type, Code.Op.AND, new Code[] { lhs, rhs },
					attributes(e));
		case OR:
			return Code.Nary(type, Code.Op.OR, new Code[] { lhs, rhs },
					attributes(e));
		case ADD:
			opcode = Code.Op.ADD;
			break;
		case SUB:
			opcode = Code.Op.SUB;
			break;
		case MUL:
			opcode = Code.Op.MUL;
			break;
		case DIV:
			opcode = Code.Op.DIV;
			break;
		case REM:
			opcode = Code.Op.REM;
			break;
		case EQ:
			opcode = Code.Op.EQ;
			break;
		case NEQ:
			opcode = Code.Op.NEQ;
			break;
		case IMPLIES:
			lhs = Code.Unary(type, Code.Unary.Op.NOT, lhs);
			return Code.Nary(type, Code.Op.OR, new Code[] { lhs, rhs },
					attributes(e));
		case IFF:
			Code nLhs = Code.Unary(type, Code.Unary.Op.NOT,lhs);
			Code nRhs = Code.Unary(type, Code.Unary.Op.NOT,rhs);
			lhs = Code.Nary(type, Code.Op.AND, new Code[]{lhs,rhs});
			rhs = Code.Nary(type, Code.Op.AND, new Code[]{nLhs,nRhs});
			return Code.Nary(type, Code.Op.OR, new Code[]{lhs,rhs},
					attributes(e));
		case LT:
			opcode = Code.Op.LT;
			break;
		case LTEQ:
			opcode = Code.Op.LTEQ;
			break;
		case GT: {
			opcode = Code.Op.LT;
			Code tmp = lhs;
			lhs = rhs;
			rhs = tmp;
			break;
		}
		case GTEQ: {
			opcode = Code.Op.LTEQ;
			Code tmp = lhs;
			lhs = rhs;
			rhs = tmp;
			break;
		}		
		default:
			internalFailure("unknown binary opcode encountered (" + e + ")",
					filename, e);
			return null;
		}

		return Code.Binary(type, opcode, lhs, rhs,
				attributes(e));
	}

	protected Code generate(Expr.Nary e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code[] operands = new Code[e.operands.size()];
		for(int i=0;i!=operands.length;++i) {
			operands[i] = generate(e.operands.get(i),environment, context);
		}
		Code.Op opcode;
		switch(e.op) {
		case TUPLE:
			opcode = Code.Op.TUPLE;
			break;
		case LIST: {

			// The goal here is convert from a list of the form [x,y,z] into a
			// set of tuples of the form {(0,x),(1,y),(2,z)}.

			for (int i = 0; i != operands.length; ++i) {
				SemanticType.Tuple tt = SemanticType.Tuple(SemanticType.Int,
						operands[i].returnType());
				Code.Constant idx = Code.Constant(Value.Integer(BigInteger
						.valueOf(i)));
				operands[i] = Code.Nary(tt, Code.Op.TUPLE, new Code[] { idx,
						operands[i] });
			}
			
			opcode = Code.Op.ARRAY;
			break;
		}
		default:
			internalFailure("unknown unary opcode encountered (" + e + ")",
					filename, e);
			return null;
		}
		return Code.Nary(type, opcode, operands,
				attributes(e));
	}

	protected Code generate(Expr.Quantifier e,
			HashMap<String, Code> _environment, WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		HashMap<String, Code> environment = new HashMap<String, Code>(
				_environment);

		ArrayList<Pair<SemanticType,Integer>> variables = new ArrayList<Pair<SemanticType,Integer>>();
		addQuantifiedVariables(e.pattern, variables, environment);

		Pair<SemanticType, Integer>[] types = variables.toArray(new Pair[variables.size()]);

		Code operand = generate(e.operand, environment, context);

		if(e instanceof Expr.ForAll) {
			return Code.Quantifier(type, Code.Op.FORALL, operand, types,
					attributes(e));
		} else {
			return Code.Quantifier(type, Code.Op.EXISTS, operand, types,
					attributes(e));
		}
	}

	// FIXME: The following is a bit of a hack really. The purpose is to ensure
	// every quantified variable is unique through an entire expression. This is
	// necessary because the rewrite rules for quantifiers don't proper handle
	// name clashes between quantified variables.  See #389
	private static int freshVar = 0;
	private static int freshVar(HashMap<String, Code> environment) {
		if(freshVar < environment.size()) {
			freshVar = environment.size();
		} else {
			freshVar++;
		}
		return freshVar;
	}

	protected void addQuantifiedVariables(TypePattern t,
			ArrayList<Pair<SemanticType, Integer>> variables,
			HashMap<String, Code> environment) {

		if(t instanceof TypePattern.Leaf) {
			TypePattern.Leaf tl = (TypePattern.Leaf) t;
			if (tl.var != null) {
				int index = freshVar(environment);
				SemanticType type = tl.attribute(TypeAttribute.class).type;
				variables.add(new Pair<SemanticType,Integer>(type,index));
				environment.put(
						tl.var.name,
						Code.Variable(type, index,
								attributes(tl)));
			}
		} else if(t instanceof TypePattern.Tuple) {
			TypePattern.Tuple tt = (TypePattern.Tuple) t;
			for (int i = 0; i != tt.elements.size(); ++i) {
				TypePattern p = tt.elements.get(i);
				addQuantifiedVariables(p, variables, environment);
			}
		}
	}


	protected Code generate(Expr.Invoke e, HashMap<String, Code> environment,
			WyalFile.Context context) {
		Code operand = generate(e.operand, environment, context);
		try {
			ArrayList<SemanticType> generics = new ArrayList<SemanticType>();
			List<SyntacticType> e_generics = e.generics;
			for (int i = 0; i != e_generics.size(); ++i) {
				SyntacticType gt = e_generics.get(i);
				generics.add(gt.attribute(TypeAttribute.class).type);

			}
			NameID nid;
			SemanticType.Function fnType;
			
			if(e.qualification == null) {
				Pair<NameID, SemanticType.Function> p = builder
						.resolveAsFunctionType(e.name, operand.returnType(),
								generics, context);
				nid = p.first();
				fnType = p.second();
			} else {
				nid = new NameID(e.qualification, e.name);
				Pair<SemanticType.Function, Map<String, SemanticType>> p = builder
						.resolveAsFunctionType(nid, operand.returnType(),
								generics, context);
				fnType = p.first();
			}
			//
			return Code.FunCall(fnType, operand, nid,
					generics.toArray(new SemanticType[generics.size()]),
					attributes(e));
		} catch (ResolveError re) {
			// should be unreachable if type propagation is already succeeded.
			syntaxError(re.getMessage(), filename, e, re);
			return null;
		}
	}

	protected Code generate(Expr.IndexOf e, HashMap<String, Code> environment,
			WyalFile.Context context) {
		SemanticType operand_type = (SemanticType) e
				.attribute(TypeAttribute.class).type;
		Code source = generate(e.operand, environment, context);

		if (operand_type instanceof SemanticType.EffectiveTuple) {
			SemanticType.EffectiveTuple tt = (SemanticType.EffectiveTuple) operand_type;
			Value.Integer idx = (Value.Integer) ((Expr.Constant) e.index).value;
			return Code.Load(tt.tupleType(), source, idx.value.intValue(),
					attributes(e));
		} else {
			SemanticType.Array type = (SemanticType.Array) operand_type;
			Code index = generate(e.index, environment, context);
			return Code.IndexOf(type, source, index, attributes(e));
		}
	}

	protected Code generate(Expr.Is e, HashMap<String, Code> environment,
			WyalFile.Context context) {
		SemanticType test = e.rightOperand.attribute(TypeAttribute.class).type;
		Code source = generate(e.leftOperand, environment, context);
		return Code.Is(source.returnType(), source, test, attributes(e));
	}
	
	/**
	 * This function attempts to find an appropriate binding for the generic
	 * types accepted by a given function, and the supplied argument type. For
	 * example, consider a call
	 * <code>f(1)<code> for a function <code>f<T>(T)</code>. The appropriate
	 * binding for this call is <code>{T=>int}</code>.
	 *
	 * @param nid
	 *            --- name identifier for the named function
	 * @param type
	 *            --- the supplied argument type
	 * @return
	 */
	protected Pair<SemanticType.Function,SemanticType[]> bindGenerics(NameID nid, SemanticType argumentType,
			SyntacticElement elem) {
		try {
			WycsFile module = builder.getModule(nid.module());
			// module should not be null if TypePropagation has already passed.
			Object d = module.declaration(nid.name());
			SemanticType[] generics;
			SemanticType.Function funType;
			if(d instanceof WycsFile.Function) {
				WycsFile.Function fn = (WycsFile.Function) d;
				generics = fn.type.generics();
				funType = fn.type;
			} else if(d instanceof WycsFile.Macro) {
				WycsFile.Macro fn = (WycsFile.Macro) d;
				generics = fn.type.generics();
				funType = fn.type;
			} else {
				internalFailure("cannot resolve as function or macro call",
						filename, elem);
				return null; // dead-code
			}
			HashMap<String,SemanticType> binding = new HashMap<String,SemanticType>();
			if (!SemanticType.bind(funType.from(), argumentType, binding)) {
				internalFailure("cannot bind function or macro call", filename,
						elem);
			}
			SemanticType[] result = new SemanticType[generics.length];
			for(int i=0;i!=generics.length;++i) {
				SemanticType.Var v = (SemanticType.Var) generics[i];
				SemanticType type = binding.get(v.name());
				if(type == null) {
					internalFailure("cannot bind function or macro call",
							filename, elem);
				}
				result[i] = type;
			}
			
			return new Pair<SemanticType.Function,SemanticType[]>(funType,result);
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, elem, ex);
			return null; // dead-code
		}
	}
	
	protected Code.FunCall invokeInternal(Path.ID module, String name,
			Code argument, WyalFile.Context context) {
		SemanticType argType = argument.returnType();
		NameID nid = new NameID(module, name);
		Pair<SemanticType.Function, SemanticType[]> p = bindGenerics(nid,
				argType, context);			
		return Code.FunCall(p.first(), argument, nid, p.second(), attributes(context));
	}

	protected static Attribute[] attributes(SyntacticElement d) {
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		for(Attribute a : d.attributes()) {
			if(a instanceof Attribute.Source || a instanceof Attribute.Origin) {
				attrs.add(a);
			}
		}
		return attrs.toArray(new Attribute[attrs.size()]);
	}

	protected static Code implies(Code lhs, Code rhs) {
		lhs = Code.Unary(SemanticType.Bool, Code.Op.NOT, lhs);
		return Code
				.Nary(SemanticType.Bool, Code.Op.OR, new Code[] { lhs, rhs });
	}

	protected static Code and(Code... constraints) {
		int count = 0;
		for(Code c : constraints) {
			if(c == null) {
				count++;
			}
		}
		if(count == 0) {
			return Code.Nary(SemanticType.Bool, Code.Op.AND, constraints);
		} else if(constraints.length-count == 1){
			for(Code c : constraints) {
				if(c != null) {
					return c;
				}
			}
		} else if(constraints.length-count > 0){
			Code[] nconstraints = new Code[constraints.length-count];
			int i=0;
			for(Code c : constraints) {
				if(c != null) {
					nconstraints[i++] = c;
				}
			}
			return Code.Nary(SemanticType.Bool, Code.Op.AND, nconstraints);
		}
		return Code.Constant(Value.Bool(true));
	}


	private static final Trie WYCS_CORE_LIST = Trie.ROOT.append("wycs")
			.append("core").append("List");
}
