package wyll.core;

import java.math.BigInteger;
import java.util.List;

import wycc.util.Pair;

public class LowLevel {

	public static interface Visitor<D, S, E> {
		// ===================================================
		// Declarations
		// ===================================================

		public D visitStaticVariable(String name, Type type, E initialiser);

		public D visitType(String name, Type definition);

		public D visitMethod(String name, List<Pair<Type, String>> parameters, Type retType, List<S> body);

		// ===================================================
		// Statements
		// ===================================================

		public S visitAssert(E condition);

		public S visitAssign(Type type, E lhs, E rhs);

		public S visitBlock(List<S> body);

		public S visitBreak();

		public S visitContinue();

		public S visitDoWhile(E condition, List<S> body);

		public S visitFor(S declaration, E condition, S increment, List<S> body);

		public S visitIfElse(List<Pair<E, List<S>>> branches);

		public S visitReturn(E rval);

		public S visitSwitch(E condition, List<Pair<Integer, List<S>>> branches);

		public S visitVariableDeclaration(Type type, String name, E initialiser);

		public S visitWhile(E condition, List<S> body);

		// ===================================================
		// General Expressions
		// ===================================================

		public E visitNullInitialiser();

		public E visitVariableAccess(Type type, String name);

		public E visitLambdaAccess(Type.Method type, String name);

		public E visitLambda(Type.Method type, List<String> parameters, E body);

		public E visitStaticVariableAccess(Type type, String name);

		public E visitDirectInvocation(Type.Method type, String name, List<E> arguments);

		public E visitIndirectInvocation(Type.Method type, E receiver, List<E> arguments);

		public E visitEqual(Type lhsT, Type rhsT, E lhs, E rhs);

		public E visitNotEqual(Type lhsT, Type rhsT, E lhs, E rhs);

		public E visitClone(Type type, E expr);

		// ===================================================
		// Logical Expressions
		// ===================================================

		public E visitLogicalInitialiser(boolean value);

		public E visitLogicalAnd(E lhs, E rhs);

		public E visitLogicalOr(E lhs, E rhs);

		public E visitLogicalNot(E expr);

		public E visitLogicalEqual(E lhs, E rhs);

		public E visitLogicalNotEqual(E lhs, E rhs);

		// ===================================================
		// Integer Expressions
		// ===================================================

		public E visitIntegerInitialiser(Type.Int type, BigInteger value);

		public E visitIntegerEqual(Type.Int type, E lhs, E rhs);

		public E visitIntegerNotEqual(Type.Int type, E lhs, E rhs);

		public E visitIntegerLessThan(Type.Int type, E lhs, E rhs);

		public E visitIntegerLessThanOrEqual(Type.Int type, E lhs, E rhs);

		public E visitIntegerGreaterThan(Type.Int type, E lhs, E rhs);

		public E visitIntegerGreaterThanOrEqual(Type.Int type, E lhs, E rhs);

		public E visitIntegerAdd(Type.Int type, E lhs, E rhs);

		public E visitIntegerSubtract(Type.Int type, E lhs, E rhs);

		public E visitIntegerMultiply(Type.Int type, E lhs, E rhs);

		public E visitIntegerDivide(Type.Int type, E lhs, E rhs);

		public E visitIntegerRemainder(Type.Int type, E lhs, E rhs);

		public E visitIntegerNegate(Type.Int type, E expr);

		public E visitIntegerCoercion(Type.Int target, Type.Int actual, E expr);

		// ===================================================
		// Bitwise Expressions
		// ===================================================

		public E visitBitwiseAnd(Type.Int target, E lhs, E rhs);

		public E visitBitwiseOr(Type.Int target, E lhs, E rhs);

		public E visitBitwiseXor(Type.Int target, E lhs, E rhs);

		public E visitBitwiseShl(Type.Int target, E lhs, E rhs);

		public E visitBitwiseShr(Type.Int target, E lhs, E rhs);

		public E visitBitwiseNot(Type.Int target, E expr);

		public E visitBitwiseEqual(Type.Int target, E lhs, E rhs);

		public E visitBitwiseNotEqual(Type.Int target, E lhs, E rhs);

		// ===================================================
		// Array Expressions
		// ===================================================

		public E visitArrayInitialiser(Type.Array type, List<E> operands);

		public E visitArrayInitialiser(Type.Array type, E length);

		public E visitArrayGenerator(Type.Array type, E value, E length);

		public E visitArrayLength(Type.Array type, E source);

		public E visitArrayAccess(List<Type.Array> type, E source, E index);

		// ===================================================
		// Record Expressions
		// ===================================================

		public E visitRecordInitialiser(Type.Record type, List<E> operands);

		public E visitRecordAccess(Type.Record type, E source, String field);

		public E visitRecordCoercion(Type.Int target, Type.Int actual, E expr);

		// ===================================================
		// Reference Expressions
		// ===================================================

		public E visitReferenceInitialiser(Type.Reference type, E operand);

		public E visitReferenceAccess(Type.Reference type, E operand);

		// ===================================================
		// Union Expressions
		// ===================================================

		public E visitUnionEnter(Type.Union type, int tag, E expr);

		public E visitUnionLeave(Type.Union type, int tag, E expr);

		public E visitUnionAccess(Type.Union type, E expr);

		public E visitUnionCoercion(Type.Union target, Type.Union actual, E expr);

		// ===================================================
		// Types
		// ===================================================

		public Type.Void visitTypeVoid();

		public Type.Bool visitTypeBool();

		public Type.Null visitTypeNull();

		/**
		 * Construct an integer type of a given width. Negative widths indicate unbound
		 * integers.
		 *
		 * @param width
		 * @return
		 */
		public Type.Int visitTypeInt(int width);

		public Type.Array visitTypeArray(Type element);

		public Type.Reference visitTypeReference(Type element);

		public Type.Recursive visitTypeRecursive(String name);

		public Type.Record visitTypeRecord(List<Pair<Type,String>> fields);

		public Type.Method visitTypeMethod(List<Type> parameters, Type ret);

		public Type.Union visitTypeUnion(List<Type> elements);
	}

	public static interface Type {

		/**
		 * Return the width of the given type in bits. Every type has a fixed width.
		 *
		 * @return
		 */
		public int getWidth();

		public interface Primitive extends Type {

		}

		public interface Void extends Primitive {

		}

		public interface Null extends Primitive {

		}

		public interface Bool extends Primitive {

		}

		public interface Int extends Primitive {

		}

		public interface Array extends Type {
			public Type getElement();
		}

		public interface Record extends Type {
			/**
			 * Get the number of fields in this record.
			 *
			 * @return
			 */
			public int size();

			/**
			 * Get a specific field within this record.
			 *
			 * @param i
			 * @return
			 */
			public Pair<? extends Type, String> getField(int i);
		}

		public interface Reference extends Type {
			/**
			 * Get the type referred to by this reference.
			 *
			 * @return
			 */
			public Type getElement();
		}

		public interface Recursive extends Type {
			/**
			 * Get the name of the recursive type to which this corresponds.
			 *
			 * @return
			 */
			public String getName();
		}

		public interface Method extends Type {
			public int numberOfParameters();

			public Type getParameterType(int param);

			public boolean hasReturnType();

			public Type getReturnType();
		}

		public interface Union extends Type {
			public int size();

			public Type getElement(int i);
		}


	}
}
