package wycs.builders;

import java.math.BigInteger;
import java.util.*;
import static wybs.lang.SyntaxError.*;
import wybs.lang.Attribute;
import wybs.lang.NameID;
import wybs.util.Pair;
import wybs.util.Trie;
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
		// TODO: implement this
		return null;
	}
	
	protected WycsFile.Declaration generate(WyalFile.Function d) {
		// TODO: implement this
		return null;		
	}
	
	protected WycsFile.Declaration generate(WyalFile.Assert d) {
		Code condition = generate(d.expr, new HashMap<String,Integer>());
		return new WycsFile.Assert(d.message, condition, d.attribute(Attribute.Source.class));
	}
	
	protected Code generate(Expr e, HashMap<String,Integer> environment) {
		if (e instanceof Expr.Variable) {
			return generate((Expr.Variable) e, environment);
		} else if (e instanceof Expr.Constant) {
			return generate((Expr.Constant) e, environment);
		} else if (e instanceof Expr.Unary) {
			return generate((Expr.Unary) e, environment);
		} else if (e instanceof Expr.Binary) {
			return generate((Expr.Binary) e, environment);
		} else if (e instanceof Expr.Nary) {
			return generate((Expr.Nary) e, environment);
		} else if (e instanceof Expr.Quantifier) {
			return generate((Expr.Quantifier) e, environment);
		} else if (e instanceof Expr.FunCall) {
			return generate((Expr.FunCall) e, environment);
		} else if (e instanceof Expr.Load) {
			return generate((Expr.Load) e, environment);
		} else if (e instanceof Expr.IndexOf) {
			return generate((Expr.IndexOf) e, environment);
		} else {
			internalFailure("unknown expression encountered (" + e + ")",
					filename, e);
			return null;
		}
	}
	
	protected Code generate(Expr.Variable e, HashMap<String,Integer> environment) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		int index = environment.get(e.name);
		return Code.Variable(type, new Code[0], index);
	}
	
	protected Code generate(Expr.Constant v, HashMap<String,Integer> environment) {
		return Code.Constant(v.value);
	}
	
	protected Code generate(Expr.Unary e, HashMap<String,Integer> environment) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code operand = generate(e.operand,environment);
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
		return Code.Unary(type, opcode, operand);
	}
	
	protected Code generate(Expr.Binary e, HashMap<String,Integer> environment) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code lhs = generate(e.leftOperand,environment);
		Code rhs = generate(e.rightOperand,environment);
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
			lhs = Code.Unary(type, Code.Unary.Op.NOT,lhs);
			return Code.Nary(type, Code.Op.OR, new Code[]{lhs,rhs});
		case IFF:
			Code nLhs = Code.Unary(type, Code.Unary.Op.NOT,lhs);
			Code nRhs = Code.Unary(type, Code.Unary.Op.NOT,rhs);
			lhs = Code.Nary(type, Code.Op.AND, new Code[]{lhs,rhs});
			rhs = Code.Nary(type, Code.Op.AND, new Code[]{nLhs,nRhs});
			return Code.Nary(type, Code.Op.OR, new Code[]{lhs,rhs});
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
//		case SETUNION:
//			opcode = Code.Op.NEG;
//			break;
//		case SETINTERSECTION:
//			opcode = Code.Op.NEG;
//			break;
//		case SETDIFFERENCE:
//			opcode = Code.Op.NEG;
//			break;
//		case LISTAPPEND:
//			opcode = Code.Op.NEG;
//			break;
		default:
			internalFailure("unknown unary opcode encountered (" + e + ")",
					filename, e);
			return null;
		}
		return Code.Binary(type, opcode, lhs, rhs);
	}
	
	protected Code generate(Expr.Nary e, HashMap<String,Integer> environment) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Code[] operands = new Code[e.operands.length];
		for(int i=0;i!=operands.length;++i) {
			operands[i] = generate(e.operands[i],environment); 
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
		return Code.Nary(type, opcode, operands);
	}
	private static int variableIndex;

	protected Code generate(Expr.Quantifier e,
			HashMap<String, Integer> environment) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		Pair<SemanticType, Integer>[] types = new Pair[e.variables.length];
		for (int i = 0; i != e.variables.length; ++i) {
			Pair<SyntacticType, Expr.Variable> p = e.variables[i];
			Expr.Variable v = p.second();
			types[i] = new Pair<SemanticType, Integer>(
					v.attribute(TypeAttribute.class).type, variableIndex);
			environment.put(p.second().name, variableIndex++);
		}
		Code operand = generate(e.operand, environment);
		Code.Op opcode = e instanceof Expr.ForAll ? Code.Op.FORALL
				: Code.Op.EXISTS;
		return Code.Quantifier(type, opcode, operand, types);
	}
	
	protected Code generate(Expr.Load e, HashMap<String, Integer> environment) {
		SemanticType.Tuple type = (SemanticType.Tuple) e
				.attribute(TypeAttribute.class).type;
		Code source = generate(e.operand, environment);
		return Code.Load(type, source, e.index);
	}
	
	protected Code generate(Expr.IndexOf e, HashMap<String, Integer> environment) {
		// FIXME: handle effective set here
		SemanticType.Set type = (SemanticType.Set) e.operand
				.attribute(TypeAttribute.class).type;
		SemanticType.Tuple element = (SemanticType.Tuple) type.element();
		SemanticType.Tuple argType = SemanticType.Tuple(type,
				element.element(0));
		SemanticType.Function funType = SemanticType.Function(argType,
				element.element(1));
		Code source = generate(e.operand, environment);
		Code index = generate(e.index, environment);
		NameID nid = new NameID(WYCS_CORE_LIST, "IndexOf");
		Code argument = Code.Nary(argType, Code.Op.TUPLE, new Code[] { source,
				index });
		return Code.FunCall(funType, argument, nid);
	}
	
	private static final Trie WYCS_CORE_LIST = Trie.ROOT.append("wycs").append("core").append("List");
}
