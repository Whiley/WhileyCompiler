package wyil.lang;

import java.util.ArrayList;
import java.util.List;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wyil.util.AbstractSyntaxTree;

/**
 * A SyntaxTree representation of the Whiley Intermediate Language (WyIL).
 * Specifically, bytecodes in WyIL are "flat" rather than being nested trees,
 * etc. This makes them easier to work with for performing optimisation, amongst
 * other things. This also means they are close to their byte-level
 * representation on disk. However, the tree-like nature of a typically abstract
 * syntax tree is convenient in many ways, and this class provides a "wrapper"
 * for bytecodes which makes them appear as a tree-like structure.
 * 
 * @author David J. Pearce
 *
 */
public interface SyntaxTree extends SyntacticElement {
	
	// ============================================================
	// Types
	// ============================================================
	
	/**
	 * Get the enclosing declaration of this syntax tree element.
	 * 
	 * @return
	 */
	public abstract WyilFile.Declaration getEnclosingDeclaration();

	/**
	 * In an abstract sense, a location represents a single unit of storage
	 * required for the execution of a given syntax tree. Every location has a
	 * single type which determines the necessary storage required for its
	 * value.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static interface Expr extends SyntaxTree {
		
		/**
		 * Get the index of this expression in the enclosing declaration. Every
		 * distinct expression has a unique index.
		 * 
		 * @return
		 */
		public int getIndex();

		/**
		 * Get the declared type of this location.
		 * 
		 * @return
		 */
		public Type getType();
	}

	/**
	 * Represents the result of an intermediate computation which is assigned to
	 * an anonymous location.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface Operator<T extends Bytecode.Expr> extends Expr {
		

		/**
		 * Get the bytecode associated with this expression
		 * 
		 * @return
		 */
		public T getBytecode();

		/**
		 * Get the underlying opcode for this expression
		 * 
		 * @return
		 */
		public int getOpcode();

		/**
		 * Return the ith operand associated with this expression.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr getOperand(int i);

		/**
		 * Get the number of operand groups in this expression.
		 * 
		 * @return
		 */
		public int numberOfOperandGroups();
		
		/**
		 * Get the ith operand group in this expression.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperandGroup(int i);
		
		/**
		 * Return the ith operand associated with this expression.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperands();
	}
	
	/**
	 * A positional operator represents a partial result from evaluating a given
	 * operator. Specifically, positional operators are used for operators which
	 * returns multiple values. Currently, the only operators which do this are
	 * for method/function invocations.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface PositionalOperator<T extends Bytecode.Expr> extends Operator<T> {
		/**
		 * Get the position of this positional operator.
		 * @return
		 */
		public int getPosition();
	}
	
	/**
	 * Represents a declared variable in the syntax tree.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Variable extends Expr {		
		/**
		 * Get the declared name of this variable
		 * @return
		 */
		public String name();
	}

	/**
	 * Represents a sequence of bytecode statements which form a block of some
	 * kind. Retains a reference to the enclosing declaration.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Block extends ArrayList<SyntaxTree.Stmt<?>> {
		private final WyilFile.Declaration parent;

		public Block(WyilFile.Declaration parent) {
			this.parent = parent;
		}

		/**
		 * Get the enclosing declaration for this block.
		 * 
		 * @return
		 */
		public WyilFile.Declaration getEnclosingDeclaration() {
			return parent;
		}
	}

	/**
	 * Represents an entry within a code block. This is a pairing of a bytecode
	 * and a list of attributes. Entries are useful because they allow us to
	 * separate a bytecode from the source-level information we carry about it.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface Stmt<T extends Bytecode.Stmt> extends SyntaxTree {
		
		/**
		 * Get the actual bytecode associated with this statement.
		 * 
		 * @return
		 */
		public T getBytecode();
		
		/**
		 * Get the underlying opcode for this statement
		 * 
		 * @return
		 */
		public int getOpcode();

		/**
		 * Get the number of blocks contained in this statement. This includes
		 * only those which are immediate children of this statement, but not
		 * those which are transitively contained.
		 * 
		 * @return
		 */
		public int numberOfBlocks();
		
		/**
		 * Get the ith block contained in this statement.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Block getBlock(int i);

		/**
		 * Return the ith operand associated with this statement.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr getOperand(int i);

		/**
		 * Return the ith operand associated with this statement.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperands();

		/**
		 * Determine the number of operand groups in this bytecode.
		 * 
		 * @return
		 */
		public int numberOfOperandGroups();
		
		/**
		 * Get the ith operand group in this bytecode.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperandGroup(int i);
		
		/**
		 * Get the enclosing block of this bytecode entry.
		 * 
		 * @return
		 */
		public SyntaxTree.Block getEnclosingBlock();
	}
	
	/**
	 * Some helpful context to make reading the code using syntax trees simpler.
	 */
	public static final int CONDITION = 0;
	public static final int BODY = 0;
	public static final int VARIABLE = 0;
	public static final int TRUEBRANCH = 0;
	public static final int PARAMETERS = 0;
	public static final int ARGUMENTS = 0;
	public static final int LEFTHANDSIDE = 0;
	//
	public static final int START = 1;
	public static final int FALSEBRANCH = 1;
	public static final int RIGHTHANDSIDE = 1;
	public static final int ENVIRONMENT = 0;
	//
	public static final int END = 2;
	
}
