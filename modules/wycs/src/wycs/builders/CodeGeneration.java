package wycs.builders;

import java.math.BigInteger;
import java.util.*;
import static wybs.lang.SyntaxError.*;
import wybs.lang.Attribute;
import wybs.lang.NameID;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wybs.util.Triple;
import wycs.core.*;
import wycs.syntax.*;

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
		} else if(declaration instanceof WyalFile.Define) {
			return generate((WyalFile.Define)declaration);
		} else if(declaration instanceof WyalFile.Function) {
			return generate((WyalFile.Function)declaration);
		} else if(declaration instanceof WyalFile.Assert) {
			return generate((WyalFile.Assert)declaration);
		} else {
			internalFailure("unknown declaration encounterd",filename,declaration);
			return null;
		}
	}
	
	protected WycsFile.Declaration generate(WyalFile.Define d) {
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
		addNamedVariables(parameter,d.from,environment);
		Code condition = generate(d.condition, environment, d);
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
			addNamedVariables(parameter,d.from,environment);
			addNamedVariables(ret,d.to,environment);
			condition = generate(d.constraint, environment, d);
		}
		// Third, create declaration		
		return new WycsFile.Function(d.name, type, condition,
				d.attribute(Attribute.Source.class));
	}
	
	protected void addNamedVariables(Code root, TypePattern t,
			HashMap<String, Code> environment) {
		if(t instanceof TypePattern.Tuple) {
			TypePattern.Tuple tt = (TypePattern.Tuple) t;
			for (int i = 0; i != tt.patterns.length; ++i) {
				TypePattern p = tt.patterns[i];
				addNamedVariables(
						Code.Load((SemanticType.Tuple) root.type, root, i,
								t.attribute(Attribute.Source.class)), p,
						environment);
			}
		}
		
		if(t.var != null) {
			environment.put(t.var, root);
		}
	}
	
	protected Code generateConstraints(Code root, TypePattern t,
			HashMap<String, Code> environment, WyalFile.Context context) {
		Code constraint = null;
		
		if (t instanceof TypePattern.Tuple) {
			TypePattern.Tuple tt = (TypePattern.Tuple) t;
			ArrayList<Code> constraints = new ArrayList<Code>();
			for (int i = 0; i != tt.patterns.length; ++i) {
				TypePattern p = tt.patterns[i];
				Code c = generateConstraints(
						Code.Load((SemanticType.Tuple) root.type, root, i,
								t.attribute(Attribute.Source.class)), p,
						environment, context);
				if(c != null) {
					constraints.add(c);
				}
			}
			if(constraints.size() > 0) {
				constraint = and(constraints.toArray(new Code[constraints.size()]));
			}
		}
		
		if(t.constraint != null) {
			constraint = and(constraint,generate(t.constraint,environment,context));
		}
		if(t.source != null) {
			Code src = generate(t.source,environment,context);
			constraint = and(constraint,Code.Binary(src.returnType(),Code.Op.IN,root,src));
		}
		
		return constraint;
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
		} else if (e instanceof Expr.Nary) {
			return generate((Expr.Nary) e, environment, context);
		} else if (e instanceof Expr.Quantifier) {
			return generate((Expr.Quantifier) e, environment, context);
		} else if (e instanceof Expr.FunCall) {
			return generate((Expr.FunCall) e, environment, context);
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
			SemanticType.Tuple argType = SemanticType.Tuple(lhs.type,rhs.type);
			SemanticType.Function funType = SemanticType.Function(argType,
					type);	
			Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] {
					lhs,rhs });
			SemanticType[] generics = {type};
			return Code.FunCall(funType, argument, nid, generics,
					e.attribute(Attribute.Source.class));
		}
		case LISTAPPEND: {			
			NameID nid = new NameID(WYCS_CORE_LIST,"Append");
			SemanticType.Tuple argType = SemanticType.Tuple(lhs.type,rhs.type);
			SemanticType.Function funType = SemanticType.Function(argType,
					type);	
			Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] {
					lhs,rhs });
			SemanticType[] generics = {type};
			return Code.FunCall(funType, argument, nid, generics,
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
	
	protected Code generate(Expr.Nary e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code[] operands = new Code[e.operands.length];
		for(int i=0;i!=operands.length;++i) {
			operands[i] = generate(e.operands[i],environment, context); 
		}
		Code.Op opcode;
		switch(e.op) {
		case AND:
			opcode = Code.Op.AND;
			break;
		case OR:
			opcode = Code.Op.OR;
			break;
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
						operands[i].type);
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
		
		int rootIndex = environment.size();
		SemanticType rootType = e.pattern.attribute(TypeAttribute.class).type;
		Code root = Code.Variable(rootType, new Code[0], rootIndex,
				e.pattern.attribute(Attribute.Source.class));
		addNamedVariables(root, e.pattern, environment);
		Code constraints = generateConstraints(root,e.pattern,environment,context); 
				
		Pair<SemanticType, Integer>[] types = new Pair[] {
				new Pair<SemanticType,Integer>(rootType,rootIndex)
		};
				
		Code operand = generate(e.operand, environment, context);
		if(constraints != null) {
			operand = implies(constraints,operand);
		}		
		Code.Op opcode = e instanceof Expr.ForAll ? Code.Op.FORALL
				: Code.Op.EXISTS;
		return Code.Quantifier(type, opcode, operand, types,
				e.attribute(Attribute.Source.class));
	}
	
	protected Code generate(Expr.FunCall e, HashMap<String, Code> environment,
			WyalFile.Context context) {
		SemanticType.Function type = null;
		Code operand = generate(e.operand, environment, context);
		try {
			Pair<NameID, SemanticType.Function> p = builder
					.resolveAsFunctionType(e.name, context);
			SemanticType[] generics = new SemanticType[e.generics.length];
			for (int i = 0; i != generics.length; ++i) {
				System.out.println("GOT: " + e.generics[i]);
				generics[i] = e.generics[i].attribute(TypeAttribute.class).type;
			}
			return Code.FunCall(p.second(), operand, p.first(), generics,
					e.attribute(Attribute.Source.class));
		} catch (ResolveError re) {
			// should be unreachable if type propagation is already succeeded.
			syntaxError("cannot resolve as function or definition call",
					filename, e, re);
			return null;
		}
	}
	
	protected Code generate(Expr.IndexOf e, HashMap<String,Code> environment, WyalFile.Context context) {
		SemanticType operand_type = e.operand
				.attribute(TypeAttribute.class).type;
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
					element.tupleElement(1));			
			Code index = generate(e.index, environment, context);
			NameID nid = new NameID(WYCS_CORE_MAP, "IndexOf");
			Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] {
					source, index });
			SemanticType[] generics = {element.tupleElement(0),element.tupleElement(1)};
			return Code.FunCall(funType, argument, nid, generics,
					e.attribute(Attribute.Source.class));
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
