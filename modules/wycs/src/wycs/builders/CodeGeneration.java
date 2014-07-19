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
		} else if(declaration instanceof WyalFile.Assert) {
			return generate((WyalFile.Assert)declaration);
		} else {
			internalFailure("unknown declaration encounterd",filename,declaration);
			return null;
		}
	}
	
	protected WycsFile.Declaration generate(WyalFile.Macro d) {
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
				d.from.attribute(Attribute.Source.class));			
		addDeclaredVariables(parameter,d.from,environment);
		Code condition = generate(d.body, environment, d);
		// Third, create declaration
		return new WycsFile.Macro(d.name, type, condition,
				d.attribute(Attribute.Source.class));
	}

	protected WycsFile.Declaration generate(WyalFile.Function d) {
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
					d.to.attribute(Attribute.Source.class));
			Code parameter = Code.Variable(from, new Code[0], 1,
					d.from.attribute(Attribute.Source.class));			
			addDeclaredVariables(parameter,d.from,environment);
			addDeclaredVariables(ret,d.to,environment);
			condition = generate(d.constraint, environment, d);
		}
		// Third, create declaration		
		return new WycsFile.Function(d.name, type, condition,
				d.attribute(Attribute.Source.class));
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
								t.attribute(Attribute.Source.class)), p,
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
		return new WycsFile.Assert(d.message, condition, d.attribute(Attribute.Source.class));
	}
	
	protected Code generate(Expr e, HashMap<String,Code> environment, WyalFile.Context context) {
		if (e instanceof Expr.Variable) {
			return generate((Expr.Variable) e, environment, context);
		} else if (e instanceof Expr.Constant) {
			return generate((Expr.Constant) e, environment, context);
		} else if (e instanceof Expr.Unary) {
			return generate((Expr.Unary) e, environment, context);
		} else if (e instanceof Expr.Binary) {
			return generate((Expr.Binary) e, environment, context);
		} else if (e instanceof Expr.Ternary) {
			return generate((Expr.Ternary) e, environment, context);
		} else if (e instanceof Expr.Nary) {
			return generate((Expr.Nary) e, environment, context);
		} else if (e instanceof Expr.Quantifier) {
			return generate((Expr.Quantifier) e, environment, context);
		} else if (e instanceof Expr.Invoke) {
			return generate((Expr.Invoke) e, environment, context);
		} else if (e instanceof Expr.IndexOf) {
			return generate((Expr.IndexOf) e, environment, context);
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
				v.attribute(Attribute.Source.class));
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
				e.attribute(Attribute.Source.class));
	}
	
	protected Code generate(Expr.Binary e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code lhs = generate(e.leftOperand,environment, context);
		Code rhs = generate(e.rightOperand,environment, context);
		Code.Op opcode;
		switch(e.op) {
		case AND:
			return Code.Nary(type, Code.Op.AND, new Code[] { lhs, rhs },
					e.attribute(Attribute.Source.class));
		case OR:
			return Code.Nary(type, Code.Op.OR, new Code[] { lhs, rhs },
					e.attribute(Attribute.Source.class));
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
					e.attribute(Attribute.Source.class));
		case IFF:
			Code nLhs = Code.Unary(type, Code.Unary.Op.NOT,lhs);
			Code nRhs = Code.Unary(type, Code.Unary.Op.NOT,rhs);
			lhs = Code.Nary(type, Code.Op.AND, new Code[]{lhs,rhs});
			rhs = Code.Nary(type, Code.Op.AND, new Code[]{nLhs,nRhs});
			return Code.Nary(type, Code.Op.OR, new Code[]{lhs,rhs},
					e.attribute(Attribute.Source.class));
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
		case IN:
			opcode = Code.Op.IN;
			break;
		case SUBSET:
			opcode = Code.Op.SUBSET;
			break;
		case SUBSETEQ:
			opcode = Code.Op.SUBSETEQ;
			break;
		case SUPSET: {
			opcode = Code.Op.SUBSET;
			Code tmp = lhs;
			lhs = rhs;
			rhs = tmp;
			break;
		}
		case SUPSETEQ: {
			opcode = Code.Op.SUBSETEQ;
			Code tmp = lhs;
			lhs = rhs;
			rhs = tmp;
			break;
		}
		case SETUNION:
		case SETINTERSECTION:		
		case SETDIFFERENCE: {			 
			String fn;
			switch(e.op) {
			case SETUNION:
				fn = "Union";
				break;
			case SETINTERSECTION:
				fn = "Intersect";
				break;
			case SETDIFFERENCE:
				fn = "Difference";
				break;
			default:
				fn = ""; // deadcode
			}
			NameID nid = new NameID(WYCS_CORE_SET,fn);
			SemanticType.Tuple argType = SemanticType.Tuple(type, type);
			SemanticType.Function funType = SemanticType.Function(argType,
					type, ((SemanticType.Set)type).element()); 	
			Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] {
					lhs,rhs });
			return Code.FunCall(funType, argument, nid,
					e.attribute(Attribute.Source.class));
		}
		case LISTAPPEND: {			
			NameID nid = new NameID(WYCS_CORE_LIST,"Append");			
			SemanticType.Tuple argType = SemanticType.Tuple(type,type);
			SemanticType[] generics = bindGenerics(nid,argType,e);
			SemanticType.Function funType = SemanticType.Function(argType,
					type,generics);	
			Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] {
					lhs,rhs });
			return Code.FunCall(funType, argument, nid,
					e.attribute(Attribute.Source.class));
		}
		case RANGE: {			
			NameID nid = new NameID(WYCS_CORE_LIST,"Range");
			SemanticType.Tuple argType = SemanticType.Tuple(SemanticType.Int,SemanticType.Int);
			SemanticType.Function funType = SemanticType.Function(argType,
					type);	
			Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] {
					lhs,rhs });
			return Code.FunCall(funType, argument, nid,
					e.attribute(Attribute.Source.class));
		}
		default:
			internalFailure("unknown binary opcode encountered (" + e + ")",
					filename, e);
			return null;
		}
		
		return Code.Binary(type, opcode, lhs, rhs,
				e.attribute(Attribute.Source.class));
	}
	
	
	protected Code generate(Expr.Ternary e, HashMap<String, Code> environment,
			WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code first = generate(e.firstOperand, environment, context);
		Code second = generate(e.secondOperand, environment, context);
		Code third = generate(e.thirdOperand, environment, context);
		SemanticType.Tuple argType;
		NameID nid;
		switch (e.op) {
		case UPDATE:
			nid = new NameID(WYCS_CORE_LIST, "ListUpdate");
			// FIXME: problem here with effective types?
			argType = SemanticType.Tuple(type, SemanticType.Int,
					((SemanticType.Set) type).element());
			break;
		case SUBLIST:
			nid = new NameID(WYCS_CORE_LIST, "Sublist");
			// FIXME: problem here with effective types?
			argType = SemanticType.Tuple(type, SemanticType.Int,
					SemanticType.Int);
			break;
		default:
			internalFailure("unknown ternary opcode encountered (" + e + ")",
					filename, e);
			return null;
		}
		Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] { first,
				second, third });
		SemanticType.Function funType = SemanticType.Function(argType, type,
				type);
		return Code.FunCall(funType, argument, nid,
				e.attribute(Attribute.Source.class));
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
		case SET:
			opcode = Code.Op.SET;
			break;
//		case MAP:
//			opcode = Code.Op.MAP;
//			break;
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
			opcode = Code.Op.SET;
			break;
		}
		default:
			internalFailure("unknown unary opcode encountered (" + e + ")",
					filename, e);
			return null;
		}
		return Code.Nary(type, opcode, operands,
				e.attribute(Attribute.Source.class));
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
					e.attribute(Attribute.Source.class));
		} else {				
			return Code.Quantifier(type, Code.Op.EXISTS, operand, types,
					e.attribute(Attribute.Source.class));
		}
	}
	
	// FIXME: The following is a bit of a hack really. The purpose is to ensure
	// every quantified variable is unique through an entire expression. This is
	// necessary because the rewrite rules for quantifiers don't proper handle
	// name clashes between quantified varibles.
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
								tl.attribute(Attribute.Source.class)));
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
		SemanticType.Function type = (SemanticType.Function) e
				.attribute(TypeAttribute.class).type;
		Code operand = generate(e.operand, environment, context);
		try {
			Pair<NameID, SemanticType.Function> p = builder
					.resolveAsFunctionType(e.name, context);			
			return Code.FunCall(type, operand, p.first(), 
					e.attribute(Attribute.Source.class));
		} catch (ResolveError re) {
			// should be unreachable if type propagation is already succeeded.
			syntaxError("cannot resolve as function or definition call",
					filename, e, re);
			return null;
		}
	}
	
	protected Code generate(Expr.IndexOf e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType operand_type = (SemanticType) e.attribute(TypeAttribute.class).type;
		Code source = generate(e.operand, environment, context);

		if(operand_type instanceof SemanticType.EffectiveTuple) {
			SemanticType.EffectiveTuple tt = (SemanticType.EffectiveTuple) operand_type;
			Value.Integer idx = (Value.Integer) ((Expr.Constant) e.index).value;
			return Code.Load(tt.tupleType(), source, idx.value.intValue(),
					e.attribute(Attribute.Source.class));
		} else {
			// FIXME: handle effective set here
			SemanticType.Set type = (SemanticType.Set) operand_type;
			SemanticType.EffectiveTuple element = (SemanticType.EffectiveTuple) type.element();
			SemanticType.Tuple argType = SemanticType.Tuple(type,
					element.tupleElement(0));
			SemanticType.Function funType = SemanticType.Function(argType,
					element.tupleElement(1),element.tupleElement(0),element.tupleElement(1));			
			Code index = generate(e.index, environment, context);
			NameID nid = new NameID(WYCS_CORE_MAP, "IndexOf");
			Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] {
					source, index });
			return Code.FunCall(funType, argument, nid,
					e.attribute(Attribute.Source.class));
		}
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
	protected SemanticType[] bindGenerics(NameID nid, SemanticType argumentType,
			SyntacticElement elem) {
		try {
			WycsFile module = builder.getModule(nid.module());
			// module should not be null if TypePropagation has already passed.
			Object d = module.declaration(nid.name());
			SemanticType[] generics;
			SemanticType parameterType;
			if(d instanceof WycsFile.Function) {
				WycsFile.Function fn = (WycsFile.Function) d;
				generics = fn.type.generics();
				parameterType = fn.type.from();
			} else if(d instanceof WycsFile.Macro) {
				WycsFile.Macro fn = (WycsFile.Macro) d;
				generics = fn.type.generics();
				parameterType = fn.type.from();
			} else {
				internalFailure("cannot resolve as function or macro call",
						filename, elem);
				return null; // dead-code
			}
			HashMap<String,SemanticType> binding = new HashMap<String,SemanticType>();
			if (!SemanticType.bind(parameterType, argumentType, binding)) {
				internalFailure("cannot bind function or macro call", filename,
						elem);
			}
			SemanticType[] result = new SemanticType[generics.length];
			for(int i=0;i!=result.length;++i) {
				SemanticType.Var v = (SemanticType.Var) generics[i];
				SemanticType type = binding.get(v.name());
				if(type == null) {
					internalFailure("cannot bind function or macro call",
							filename, elem);
				}
				result[i] = type;
			}
			return result;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, elem, ex);
			return null; // dead-code
		}
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
	
	
	private static final Trie WYCS_CORE_SET = Trie.ROOT.append("wycs")
			.append("core").append("Set");
	private static final Trie WYCS_CORE_MAP = Trie.ROOT.append("wycs")
			.append("core").append("Map");
	private static final Trie WYCS_CORE_LIST = Trie.ROOT.append("wycs")
			.append("core").append("List");
}
