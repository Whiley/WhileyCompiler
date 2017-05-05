package wyil.util;

import java.util.List;

import wybs.lang.Build;
import wyil.lang.Bytecode;
import wyil.lang.SyntaxTree;
import wyil.lang.SyntaxTree.Location;
import wyil.lang.WyilFile;

/**
 * <p>
 * Responsible for determining when a value of a dynamically sized data type can
 * be "moved" or must be "copied". Moving is preferable preferable (when
 * permitted) because the original reference can be used without copying the
 * underlying data. The following provides a useful example:
 * </p>
 *
 * <pre>
 * function g(int[] xs, int i) -> (int[] ys):
 *     xs[j] = f(xs)
 *     return xs
 * </pre>
 *
 * <p>
 * Here, in the invocation <code>f(xs)</code> the array <code>xs</code> cannot
 * be moved since it is live afterwards. Instead, we must clone <code>xs</code>
 * at this point. However, the subsequent use of <code>xs</code> in the
 * <code>return</code> statement does not require a clone and can be moved
 * (since <code>xs</code> is no longer live).
 * </p>
 *
 * <p>
 * The following illustrates another situation where temporary moves or
 * "borrows" are permitted:
 * </p>
 *
 * <pre>
 * function get(int[] xs, int i, int j) -> (int r):
 *     int sum = xs[i]
 *     sum = sum + xs[j]
 *     return sum
 * </pre>
 *
 * Clearly, there is not need to clone <code>xs</code> when it is used in the
 * initialiser for <code>sum</code>. This is because the use is temporary and
 * does not modify <code>xs</code>. We say that <code>xs</code> is not
 * <em>consumed</em>.
 *
 * @author David J. Pearce
 *
 */
public class MoveAnalysis implements Build.Stage<WyilFile> {
	public MoveAnalysis(Build.Task builder) {

	}


	@Override
	public void apply(WyilFile module) {

		for(WyilFile.Type type : module.types()) {
			check(type);
		}
		for(WyilFile.FunctionOrMethod method : module.functionOrMethods()) {
			check(method);
		}
	}

	private void check(WyilFile.Type t) {
		for(Location<Bytecode.Expr> e : t.getInvariant()) {
			check(false,e);
		}
	}
	private void check(WyilFile.FunctionOrMethod fm) {
		SyntaxTree tree = fm.getTree();
		// Examine all entries in this block looking for a conversion bytecode
		List<SyntaxTree.Location<?>> expressions = tree.getLocations();
		for (int i = 0; i != expressions.size(); ++i) {
			SyntaxTree.Location<?> l = expressions.get(i);
			if (l.getBytecode() instanceof Bytecode.Stmt) {
				check((Location<Bytecode.Stmt>) l);
			}
		}
	}

	private  void check(Location<Bytecode.Stmt> stmt) {
		switch(stmt.getOpcode()) {

		case Bytecode.OPCODE_assert:
		case Bytecode.OPCODE_assume: {
			check(false,(Location<Bytecode.Expr>) stmt.getOperand(0));
			break;
		}
		case Bytecode.OPCODE_vardeclinit: {
			check(true, (Location<Bytecode.Expr>) stmt.getOperand(0));
			break;
		}
		case Bytecode.OPCODE_return: {
			for(int i=0;i!=stmt.numberOfOperands();++i) {
				check(false,(Location<Bytecode.Expr>) stmt.getOperand(i));
			}
			break;
		}
		case Bytecode.OPCODE_if:
		case Bytecode.OPCODE_ifelse:
		case Bytecode.OPCODE_switch:
		case Bytecode.OPCODE_while:
		case Bytecode.OPCODE_dowhile: {
			check(false,(Location<Bytecode.Expr>) stmt.getOperand(0));
			break;
		}
		case Bytecode.OPCODE_debug: {
			check(false,(Location<Bytecode.Expr>) stmt.getOperand(0));
			break;
		}
		case Bytecode.OPCODE_invoke:
		case Bytecode.OPCODE_indirectinvoke: {
			check(false,(Location) stmt);
			break;
		}
		case Bytecode.OPCODE_assign: {
			Location<?>[] lhs = stmt.getOperandGroup(SyntaxTree.LEFTHANDSIDE);
			Location<?>[] rhs = stmt.getOperandGroup(SyntaxTree.RIGHTHANDSIDE);
			for(int i=0;i!=lhs.length;++i) {
				check(false,(Location<Bytecode.Expr>) lhs[i]);
			}
			for(int i=0;i!=rhs.length;++i) {
				check(true,(Location<Bytecode.Expr>) rhs[i]);
			}
			break;
		}
		case Bytecode.OPCODE_aliasdecl:
		case Bytecode.OPCODE_vardecl:
		case Bytecode.OPCODE_fail:
		case Bytecode.OPCODE_block:
		case Bytecode.OPCODE_namedblock:
		case Bytecode.OPCODE_break:
		case Bytecode.OPCODE_continue:
		case Bytecode.OPCODE_skip:
			// do nothing here
			break;
		default:
			throw new IllegalArgumentException(
					"Unknown bytecode encountered: " + stmt.getBytecode().getClass().getName());
		}
	}

	private void check(boolean consumed, Location<Bytecode.Expr> expr) {
		switch(expr.getOpcode()) {
		case Bytecode.OPCODE_lambda:
		case Bytecode.OPCODE_is:
			check(false,(Location<Bytecode.Expr>) expr.getOperand(0));
			break;
		case Bytecode.OPCODE_all:
		case Bytecode.OPCODE_some: {
			for(int i=0;i!=expr.numberOfOperands();++i) {
				check(false,(Location<Bytecode.Expr>) expr.getOperand(i));
			}
			for (int i = 0; i != expr.numberOfOperandGroups(); ++i) {
				Location<?>[] range = expr.getOperandGroup(i);
				check(false,(Location<Bytecode.Expr>) range[SyntaxTree.START]);
				check(false,(Location<Bytecode.Expr>) range[SyntaxTree.END]);
			}
			break;
		}
		case Bytecode.OPCODE_convert: {
			check(consumed,(Location<Bytecode.Expr>) expr.getOperand(0));
			break;
		}
		case Bytecode.OPCODE_fieldload:
		case Bytecode.OPCODE_neg:
		case Bytecode.OPCODE_arraylength:
		case Bytecode.OPCODE_dereference:
		case Bytecode.OPCODE_add:
		case Bytecode.OPCODE_sub:
		case Bytecode.OPCODE_mul:
		case Bytecode.OPCODE_div:
		case Bytecode.OPCODE_rem:
		case Bytecode.OPCODE_eq:
		case Bytecode.OPCODE_ne:
		case Bytecode.OPCODE_lt:
		case Bytecode.OPCODE_le:
		case Bytecode.OPCODE_gt:
		case Bytecode.OPCODE_ge:
		case Bytecode.OPCODE_logicalnot:
		case Bytecode.OPCODE_logicaland:
		case Bytecode.OPCODE_logicalor:
		case Bytecode.OPCODE_bitwiseinvert:
		case Bytecode.OPCODE_bitwiseor:
		case Bytecode.OPCODE_bitwisexor:
		case Bytecode.OPCODE_bitwiseand:
		case Bytecode.OPCODE_shl:
		case Bytecode.OPCODE_shr:
		case Bytecode.OPCODE_arrayindex:
		case Bytecode.OPCODE_arraygen: {
			for(int i=0;i!=expr.numberOfOperands();++i) {
				check(false,(Location<Bytecode.Expr>) expr.getOperand(i));
			}
			break;
		}
		case Bytecode.OPCODE_array:
		case Bytecode.OPCODE_record: {
			for(int i=0;i!=expr.numberOfOperands();++i) {
				check(consumed,(Location<Bytecode.Expr>) expr.getOperand(i));
			}
			break;
		}
		case Bytecode.OPCODE_invoke:
		case Bytecode.OPCODE_indirectinvoke:
		case Bytecode.OPCODE_newobject: {
			for (int i = 0; i != expr.numberOfOperands(); ++i) {
				check(true, (Location<Bytecode.Expr>) expr.getOperand(i));
			}
			break;
		}
		case Bytecode.OPCODE_const:
			break;
		case Bytecode.OPCODE_varmove:
		case Bytecode.OPCODE_varcopy:
			if (!consumed) {
				// In this case, we have identified a variable access which is
				// not consumed and therefore can be implemented as a move.
				Bytecode.VariableAccess bytecode = new Bytecode.VariableAccess(false, expr.getOperand(0).getIndex());
				SyntaxTree.Location<?> move = new SyntaxTree.Location<>(expr.getEnclosingTree(), expr.getType(), bytecode,
						expr.attributes());
				expr.getEnclosingTree().getLocations().set(expr.getIndex(), move);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown bytecode encountered: " + expr.getBytecode().getClass().getName());
		}
	}
}
