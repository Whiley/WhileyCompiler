package wyil.util;

import java.util.List;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wyil.lang.Bytecode;
import wyil.lang.SyntaxTree;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.lang.SyntaxTree.Expr;

public abstract class AbstractSyntaxTree extends SyntacticElement.Impl {

	public AbstractSyntaxTree(List<Attribute> attributes) {
		super(attributes);
	}

	public AbstractSyntaxTree(Attribute... attributes) {
		super(attributes);
	}

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
	public static abstract class Expr extends AbstractSyntaxTree implements SyntaxTree.Expr {
		protected final WyilFile.Declaration parent;
		protected final Type type;

		public Expr(Type type, WyilFile.Declaration parent, List<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.parent = parent;
		}

		public Expr(Type type, WyilFile.Declaration parent, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.parent = parent;
		}

		@Override
		public WyilFile.Declaration getEnclosingDeclaration() {
			return parent;
		}

		/**
		 * Get the index of this expression in the enclosing declaration. Every
		 * distinct expression has a unique index.
		 * 
		 * @return
		 */
		public int getIndex() {
			return parent.getExpressionIndex(this);
		}

		/**
		 * Get the declared type of this location.
		 * 
		 * @return
		 */
		public Type getType() {
			return type;
		}
	}
	
	public static class Operator<T extends Bytecode.Expr> extends AbstractSyntaxTree.Expr
			implements SyntaxTree.Operator<T> {
		private final T bytecode;

		public Operator(Type type, T bytecode, WyilFile.Declaration parent, List<Attribute> attributes) {
			super(type, parent, attributes);
			this.bytecode = bytecode;
		}

		public Operator(Type type, T bytecode, WyilFile.Declaration parent, Attribute... attributes) {
			super(type, parent, attributes);
			this.bytecode = bytecode;
		}

		/**
		 * Get the bytecode associated with this expression
		 * 
		 * @return
		 */
		public T getBytecode() {
			return bytecode;
		}

		/**
		 * Get the underlying opcode for this expression
		 * 
		 * @return
		 */
		public int getOpcode() {
			return bytecode.getOpcode();
		}

		/**
		 * Return the ith operand associated with this expression.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr getOperand(int i) {
			int operand = bytecode.getOperand(i);
			return parent.getExpression(operand);
		}

		/**
		 * Get the number of operand groups in this expression.
		 * 
		 * @return
		 */
		public int numberOfOperandGroups() {
			return bytecode.numberOfOperandGroups();
		}

		/**
		 * Get the ith operand group in this expression.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperandGroup(int i) {
			return parent.getExpressions(bytecode.getOperandGroup(i));
		}

		/**
		 * Return the ith operand associated with this expression.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperands() {
			int[] operands = bytecode.getOperands();
			return parent.getExpressions(operands);
		}

		public String toString() {
			return type + " " + bytecode;
		}
	}

	public static class PositionalOperator<T extends Bytecode.Expr> extends Operator<T> implements SyntaxTree.PositionalOperator<T> {
		private int position;

		public PositionalOperator(Type type, T bytecode, int position, WyilFile.Declaration parent,
				List<Attribute> attributes) {
			super(type, bytecode, parent, attributes);
			this.position = position;
		}

		public PositionalOperator(Type type, T bytecode, int position, WyilFile.Declaration parent,
				Attribute... attributes) {
			super(type, bytecode, parent, attributes);
			this.position = position;
		}
		
		public int getPosition() {
			return position;
		}
	}
	
	/**
	 * Represents a declared variable in the syntax tree.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Variable extends Expr implements SyntaxTree.Variable {
		private final String name;

		public Variable(Type type, String name, WyilFile.Declaration parent, List<Attribute> attributes) {
			super(type, parent, attributes);
			this.name = name;
		}

		public Variable(Type type, String name, WyilFile.Declaration parent, Attribute... attributes) {
			super(type, parent, attributes);
			this.name = name;
		}

		public String name() {
			return name;
		}

		public String toString() {
			return type + " " + name;
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
	public static class Stmt<T extends Bytecode.Stmt> extends AbstractSyntaxTree implements SyntaxTree.Stmt<T> {
		private final SyntaxTree.Block parent;
		private final T bytecode;

		public Stmt(T code, SyntaxTree.Block parent, List<Attribute> attributes) {
			super(attributes);
			this.bytecode = code;
			this.parent = parent;
		}

		public Stmt(T code, SyntaxTree.Block parent, Attribute... attributes) {
			super(attributes);
			this.bytecode = code;
			this.parent = parent;
		}

		/**
		 * Get the actual bytecode associated with this statement.
		 * 
		 * @return
		 */
		public T getBytecode() {
			return bytecode;
		}

		/**
		 * Get the underlying opcode for this statement
		 * 
		 * @return
		 */
		public int getOpcode() {
			return bytecode.getOpcode();
		}

		/**
		 * Get the number of blocks contained in this statement. This includes
		 * only those which are immediate children of this statement, but not
		 * those which are transitively contained.
		 * 
		 * @return
		 */
		public int numberOfBlocks() {
			return bytecode.numberOfBlocks();
		}
		
		/**
		 * Get the ith block contained in this statement.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Block getBlock(int i) {
			return parent.getEnclosingDeclaration().getBlock(bytecode.getBlock(i));
		}

		/**
		 * Return the ith operand associated with this statement.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr getOperand(int i) {
			int operand = bytecode.getOperand(i);
			return parent.getEnclosingDeclaration().getExpression(operand);
		}

		/**
		 * Return the ith operand associated with this statement.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperands() {
			int[] operands = bytecode.getOperands();
			return parent.getEnclosingDeclaration().getExpressions(operands);
		}

		/**
		 * Determine the number of operand groups in this bytecode.
		 * 
		 * @return
		 */
		public int numberOfOperandGroups() {
			return bytecode.numberOfOperandGroups();
		}
		
		/**
		 * Get the ith operand group in this bytecode.
		 * 
		 * @param i
		 * @return
		 */
		public SyntaxTree.Expr[] getOperandGroup(int i) {
			return parent.getEnclosingDeclaration().getExpressions(bytecode.getOperandGroup(i));
		}
		
		
		@Override
		public WyilFile.Declaration getEnclosingDeclaration() {
			return parent.getEnclosingDeclaration();
		}

		/**
		 * Get the enclosing block of this bytecode entry.
		 * 
		 * @return
		 */
		public SyntaxTree.Block getEnclosingBlock() {
			return parent;
		}
	}
	
	public static class StmtExpr<T extends Bytecode.Stmt & Bytecode.Expr> extends Stmt<T> implements SyntaxTree.Operator<T> {
		public StmtExpr(T code, SyntaxTree.Block parent, List<Attribute> attributes) {
			super(code, parent, attributes);
		}

		public StmtExpr(T code, SyntaxTree.Block parent, Attribute... attributes) {
			super(code, parent, attributes);
		}

		@Override
		public int getIndex() {
			throw new IllegalArgumentException();
		}

		@Override
		public Type getType() {
			throw new IllegalArgumentException();
		}
	}
}
