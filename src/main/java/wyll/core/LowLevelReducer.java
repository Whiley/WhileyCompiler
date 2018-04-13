package wyll.core;

import static wyc.lang.WhileyFile.TYPE_array;
import static wyc.lang.WhileyFile.TYPE_bool;
import static wyc.lang.WhileyFile.TYPE_byte;
import static wyc.lang.WhileyFile.TYPE_function;
import static wyc.lang.WhileyFile.TYPE_int;
import static wyc.lang.WhileyFile.TYPE_method;
import static wyc.lang.WhileyFile.TYPE_nominal;
import static wyc.lang.WhileyFile.TYPE_null;
import static wyc.lang.WhileyFile.TYPE_property;
import static wyc.lang.WhileyFile.TYPE_record;
import static wyc.lang.WhileyFile.TYPE_reference;
import static wyc.lang.WhileyFile.TYPE_staticreference;
import static wyc.lang.WhileyFile.TYPE_union;
import static wyc.lang.WhileyFile.TYPE_void;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wybs.lang.NameResolver;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.Type;
import wyc.lang.WhileyFile.Type.Array;
import wyc.lang.WhileyFile.Type.Byte;
import wyc.lang.WhileyFile.Type.Callable;
import wyc.lang.WhileyFile.Type.Function;
import wyc.lang.WhileyFile.Type.Int;
import wyc.lang.WhileyFile.Type.Method;
import wyc.lang.WhileyFile.Type.Property;
import wyc.lang.WhileyFile.Type.Record;
import wyc.lang.WhileyFile.Type.Reference;
import wyc.util.AbstractVisitor;
import wycc.util.Pair;
import wycc.util.Triple;
import wyll.task.TypeMangler;
import wyll.util.StdTypeMangler;

public class LowLevelReducer<D,S,E extends S> implements LowLevelTranslator.Reducer<D,S,E> {
	protected final NameResolver resolver;
	private final LowLevel.Visitor<D, S, E> visitor;
	private final TypeMangler mangler;
	/**
	 * The auxillaries are the list of additional declarations created as
	 * intermediates during the translation process.
	 */
	private final ArrayList<D> auxillaries = new ArrayList<>();

	public LowLevelReducer(NameResolver resolver, LowLevel.Visitor<D, S, E> visitor) {
		this.resolver = resolver;
		this.visitor = visitor;
		this.mangler = new StdTypeMangler();
	}

	@Override
	public D visitStaticVariable(Identifier name, Type type, E initialiser) {
		LowLevel.Type llType = convertType(type);
		// FIXME: should mangle the name?
		return visitor.visitStaticVariable(name.get(), llType, initialiser);
	}

	@Override
	public D visitType(Identifier name, Type definition, Identifier parameter, List<E> invariant) {
		// FIXME: for now
		return null;
	}

	@Override
	public D visitProperty(Identifier name, Property type, Tuple<Identifier> parameters, Tuple<Identifier> returns,
			List<E> invariant) {
		Tuple<Type> parameterTypes = type.getParameters();
		String mangle = getMangledName(name.toString(), type);
		// Construct parameters
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> nParameters = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Identifier parameter = parameters.get(i);
			LowLevel.Type parameterType = convertType(parameterTypes.get(i));
			nParameters.add(new wycc.util.Pair<>(parameterType, parameter.get()));
		}
		// Determine appropriate return type which properly encodes multiple returns
		// when present. If no return value is given then this is null.
		LowLevel.Type retType = convertType(getMultipleReturnType(type.getReturns()));
		// Now we have to translate each of the invariant clauses and combine them
		// together.
		ArrayList<S> body = new ArrayList<>();
		// FIXME: should do better here!
		String var = createTemporaryVariable();
		LowLevel.Type varT = convertBool(Type.Bool);
		body.add(visitor.visitVariableDeclaration(varT, var, visitor.visitLogicalInitialiser(true)));
		for (int i = 0; i != invariant.size(); ++i) {
			E lhs = visitor.visitVariableAccess(varT, var);
			E rhs = invariant.get(i);
			rhs = visitor.visitLogicalAnd(lhs, rhs);
			body.add(visitor.visitAssign(varT, lhs, rhs));
		}
		body.add(visitor.visitReturn(visitor.visitVariableAccess(varT, var)));
		return visitor.visitMethod(mangle, nParameters, retType, body);
	}

	@Override
	public D visitFunction(Identifier name, Function type, Tuple<Identifier> parameters, Tuple<Identifier> returns,
			List<E> requires, List<E> ensures, List<S> body) {
		Tuple<Type> parameterTypes = type.getParameters();
		String mangle = getMangledName(name.toString(), type);
		// Construct parameters
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> nParameters = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Identifier parameter = parameters.get(i);
			LowLevel.Type parameterType = convertType(parameterTypes.get(i));
			nParameters.add(new wycc.util.Pair<>(parameterType, parameter.get()));
		}
		// Determine appropriate return type which properly encodes multiple returns
		// when present. If no return value is given then this is null.
		LowLevel.Type retType = convertType(getMultipleReturnType(type.getReturns()));
		// Done
		return visitor.visitMethod(mangle, nParameters, retType, body);
	}

	@Override
	public D visitMethod(Identifier name, Method type, Tuple<Identifier> parameters, Tuple<Identifier> returns,
			List<E> requires, List<E> ensures, List<S> body) {
		Tuple<Type> parameterTypes = type.getParameters();
		String mangle = getMangledName(name.toString(), type);
		// Construct parameters
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> nParameters = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Identifier parameter = parameters.get(i);
			LowLevel.Type parameterType = convertType(parameterTypes.get(i));
			nParameters.add(new wycc.util.Pair<>(parameterType, parameter.get()));
		}
		// Determine appropriate return type which properly encodes multiple returns
		// when present. If no return value is given then this is null.
		LowLevel.Type retType = convertType(getMultipleReturnType(type.getReturns()));
		// Done
		return visitor.visitMethod(mangle, nParameters, retType, body);
	}

	@Override
	public S visitAssert(E condition) {
		return visitor.visitAssert(condition);
	}

	@Override
	public S visitAssume(E condition) {
		return visitor.visitAssert(condition);
	}

	@Override
	public S visitAssign(List<Triple<Tuple<Type>, List<E>, E>> assignments) {
		List<S> stmts = new ArrayList<>();
		// Translate assignments into individual statements
		for (int i = 0; i != assignments.size(); ++i) {
			Triple<Tuple<Type>, List<E>, E> assignment = assignments.get(i);
			Tuple<Type> types = assignment.first();
			List<E> lhs = assignment.second();
			E rhs = assignment.third();
			//
			if (lhs.size() == 1) {
				// Simple case of single assignment
				LowLevel.Type llType = convertType(types.get(0));
				stmts.add(visitor.visitAssign(llType, lhs.get(0), rhs));
			} else {
				// Complex case of multiple assignment
				// Create variable declaration
				String var = createTemporaryVariable();
				Type temporaryType = getMultipleReturnType(types);
				LowLevel.Type llTemporaryType = convertType(temporaryType);
				stmts.add(visitor.visitVariableDeclaration(llTemporaryType, var, rhs));
				// Assign to lhs variables
				LowLevel.Type.Record llRecT = (LowLevel.Type.Record) llTemporaryType;
				for (int k = 0; k != types.size(); ++k) {
					E lval = lhs.get(k);
					E rval = visitor.visitVariableAccess(llTemporaryType, var);
					rval = visitor.visitRecordAccess(llRecT, rval, "f" + k);
					// Apply any coercions required of the assignment.
					stmts.add(visitor.visitAssign(llRecT.getField(k).first(), lval, rval));
				}
			}
		}
		// If only one statement, then easy enough
		if (stmts.size() == 1) {
			return stmts.get(0);
		} else {
			// NOTE: should be atomic!
			return visitor.visitBlock(stmts);
		}
	}

	@Override
	public S visitBreak() {
		return visitor.visitBreak();
	}

	@Override
	public S visitContinue() {
		return visitor.visitContinue();
	}

	@Override
	public S visitDebug(E operand) {
		throw new IllegalArgumentException("GOT HERE");
	}

	@Override
	public S visitDoWhile(E condition, List<E> invariants, List<S> body) {
		// FIXME: what to do with invariants?
		return visitor.visitDoWhile(condition, body);
	}

	@Override
	public S visitFail() {
		return visitor.visitAssert(visitor.visitLogicalInitialiser(false));
	}

	@Override
	public S visitIfElse(List<Pair<E, List<S>>> branches) {
		return visitor.visitIfElse(branches);
	}

	@Override
	public S visitNamedBlock(Identifier name, S body) {
		throw new RuntimeException("implement me!");
	}

	@Override
	public S visitReturn(List<Pair<Type, E>> operands) {
		if(operands.size() == 1) {
			// Simple case of a single return value
			Pair<Type,E> p = operands.get(0);
			return visitor.visitReturn(p.second());
		} else {
			// TODO Auto-generated method stub
			throw new RuntimeException("implement me!");
		}
	}

	@Override
	public S visitSkip() {
		// FIXME: does this make sense?
		throw new RuntimeException("implement me!");
	}

	@Override
	public S visitSwitch(Type type, E condition, List<Pair<List<E>, S>> branches) {
		throw new RuntimeException("implement me");
		//return visitor.visitSwitch(condition, branches);
	}

	@Override
	public S visitVariableDeclaration(Type type, Identifier name, E initialiser) {
		LowLevel.Type llType = convertType(type);
		return visitor.visitVariableDeclaration(llType, name.get(), initialiser);
	}

	@Override
	public S visitWhile(E condition, List<E> invariants, List<S> body) {
		// FIXME: what to do with invariants?
		return visitor.visitWhile(condition, body);
	}

	@Override
	public E visitNullInitialiser() {
		return visitor.visitNullInitialiser();
	}

	@Override
	public E visitVariableAccess(Type type, Identifier name) {
		LowLevel.Type llType = convertType(type);
		return visitor.visitVariableAccess(llType, name.get());
	}

	@Override
	public E visitLambdaAccess(Type.Callable type, Name name) {
		String mangle = getMangledName(name.toString(), type);
		LowLevel.Type.Method llType = convertCallable(type);
		return visitor.visitLambdaAccess(llType, mangle);
	}

	@Override
	public E visitLambda(Type.Callable type, Tuple<Identifier> parameters, E body) {
		LowLevel.Type.Method llType = convertCallable(type);
		ArrayList<String> ps = new ArrayList<>();
		for (Identifier i : parameters) {
			ps.add(i.get());
		}
		return visitor.visitLambda(llType, ps, body);
	}

	@Override
	public E visitStaticVariableAccess(Type type, Name name) {
		String mangle = getMangledName(name.toString(), type);
		LowLevel.Type llType = convertType(type);
		return visitor.visitStaticVariableAccess(llType, mangle);
	}

	@Override
	public E visitInvoke(Callable type, Name name, List<E> arguments) {
		String mangle = getMangledName(name.toString(), type);
		LowLevel.Type.Method llType = convertCallable(type);
		return visitor.visitDirectInvocation(llType, mangle, arguments);
	}

	@Override
	public E visitIndirectInvoke(Callable type, E receiver, List<E> arguments) {
		LowLevel.Type.Method llType = convertCallable(type);
		return visitor.visitIndirectInvocation(llType, receiver, arguments);
	}

	@Override
	public E visitIs(Type type, E operand, Type test) {
		// TODO Auto-generated method stub
		throw new RuntimeException("implement me!");
	}

	@Override
	public E visitEqual(Type lhsT, Type rhsT, E lhs, E rhs) {
		LowLevel.Type llLhsT = convertType(lhsT);
		LowLevel.Type llRhsT = convertType(rhsT);
		return visitor.visitEqual(llLhsT,llRhsT,lhs,rhs);
	}

	@Override
	public E visitNotEqual(Type lhsT, Type rhsT, E lhs, E rhs) {
		LowLevel.Type llLhsT = convertType(lhsT);
		LowLevel.Type llRhsT = convertType(rhsT);
		return visitor.visitNotEqual(llLhsT,llRhsT,lhs,rhs);
	}

	@Override
	public E visitCast(Type type, E expr, Type cast) {
		// TODO Auto-generated method stub
		throw new RuntimeException("implement me!");
	}

	@Override
	public E visitLogicalInitialiser(boolean value) {
		return visitor.visitLogicalInitialiser(value);
	}

	@Override
	public E visitLogicalAnd(List<E> operands) {
		E r = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			r = visitor.visitLogicalAnd(r, operands.get(i));
		}
		return r;
	}

	@Override
	public E visitLogicalOr(List<E> operands) {
		E r = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			r = visitor.visitLogicalOr(r, operands.get(i));
		}
		return r;
	}

	@Override
	public E visitLogicalNot(E expr) {
		return visitor.visitLogicalNot(expr);
	}

	@Override
	public E visitLogicalIff(E lhs, E rhs) {
		return visitor.visitLogicalEqual(lhs, rhs);
	}

	@Override
	public E visitLogicalImplication(E lhs, E rhs) {
		E tmp = visitor.visitLogicalNot(lhs);
		return visitor.visitLogicalOr(tmp, rhs);
	}

	@Override
	public E visitUniversalQuantifier(List<Triple<Identifier, E, E>> paraemters, E body) {
		// TODO Auto-generated method stub
		throw new RuntimeException("implement me!");
	}

	@Override
	public E visitExistentialQuantifier(List<Triple<Identifier, E, E>> paraemters, E body) {
		// TODO Auto-generated method stub
		throw new RuntimeException("implement me!");
	}

	public E visitQuantifier(boolean isUniversal, List<Triple<Identifier, E, E>> paraemters, E condition) {
		String name = "expr$" + tempIndex++;
		Set<WhileyFile.Decl.Variable> uses = determineUsedVariables(condition);
		List<wycc.util.Pair<LowLevel.Type, String>> parameters = constructQuantifierParameters(uses);
		// Create the method body, which contains the sequence of nested quantifier for
		// loops and the final return statement.
		List<S> body = constructQuantifierBody(condition, 0, condition, environment);
		// Determine whether getting through the loops should return true or false
		E retval = visitor.visitLogicalInitialiser(isUniversal);
		body.add(visitor.visitReturn(retval));
		// Create the method body from the loops and the additional return statement
		auxillaries.add(visitor.visitMethod(name, parameters, visitor.visitTypeBool(), body));
		// Declare the method somehow?
		// Create an invocation to the block
		return visitor.visitDirectInvocation(constructQuantifierType(parameters), name,
				constructQuantifierArguments(uses));
	}

	/**
	 * Determine the set of used variables in a given expression. A used variable is
	 * simply one that is accessed from within the expression. Care needs to be
	 * taken for expressions which declare parameters in order to avoid capturing
	 * these.
	 *
	 * @param expr
	 * @return
	 */
	public Set<WhileyFile.Decl.Variable> determineUsedVariables(WhileyFile.Expr expr) {
		final HashSet<WhileyFile.Decl.Variable> used = new HashSet<>();
		// Create a translateor to extract all uses from the given expression.
		final AbstractVisitor translateor = new AbstractVisitor() {
			@Override
			public void visitVariableAccess(WhileyFile.Expr.VariableAccess expr) {
				used.add(expr.getVariableDeclaration());
			}

			@Override
			public void visitUniversalQuantifier(WhileyFile.Expr.UniversalQuantifier expr) {
				visitVariables(expr.getParameters());
				visitExpression(expr.getOperand());
				removeAllDeclared(expr.getParameters());
			}

			@Override
			public void visitExistentialQuantifier(WhileyFile.Expr.ExistentialQuantifier expr) {
				visitVariables(expr.getParameters());
				visitExpression(expr.getOperand());
				removeAllDeclared(expr.getParameters());
			}

			@Override
			public void visitType(WhileyFile.Type type) {
				// No need to visit types
			}

			private void removeAllDeclared(Tuple<Decl.Variable> parameters) {
				for (int i = 0; i != parameters.size(); ++i) {
					used.remove(parameters.get(i));
				}
			}
		};
		//
		translateor.visitExpression(expr);
		return used;
	}

	@Override
	public E visitIntegerInitialiser(Int type, BigInteger value) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerInitialiser(llType, value);
	}

	@Override
	public E visitIntegerLessThan(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerLessThan(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerLessThanOrEqual(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerLessThanOrEqual(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerGreaterThan(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerGreaterThan(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerGreaterThanOrEqual(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerGreaterThanOrEqual(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerAdd(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerAdd(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerSubtract(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerSubtract(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerMultiply(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerMultiply(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerDivide(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerDivide(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerRemainder(Int type, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerRemainder(llType, lhs, rhs);
	}

	@Override
	public E visitIntegerNegate(Type.Int type, E expr) {
		LowLevel.Type.Int llType = convertInt(type);
		return visitor.visitIntegerNegate(llType, expr);
	}

	@Override
	public E visitIntegerCoercion(Int target, Int actual, E expr) {
		// TODO Auto-generated method stub
		throw new RuntimeException("implement me!");
	}

	@Override
	public E visitBitwiseAnd(Type.Byte target, List<E> operands) {
		LowLevel.Type.Int llType = convertByte(target);
		E r = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			r = visitor.visitBitwiseAnd(llType, r, operands.get(i));
		}
		return r;
	}

	@Override
	public E visitBitwiseOr(Type.Byte target, List<E> operands) {
		LowLevel.Type.Int llType = convertByte(target);
		E r = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			r = visitor.visitBitwiseOr(llType, r, operands.get(i));
		}
		return r;
	}

	@Override
	public E visitBitwiseXor(Type.Byte target, List<E> operands) {
		LowLevel.Type.Int llType = convertByte(target);
		E r = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			r = visitor.visitBitwiseXor(llType, r, operands.get(i));
		}
		return r;
	}

	@Override
	public E visitBitwiseShl(Type.Byte target, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertByte(target);
		return visitor.visitBitwiseShl(llType, lhs, rhs);
	}

	@Override
	public E visitBitwiseShr(Type.Byte target, E lhs, E rhs) {
		LowLevel.Type.Int llType = convertByte(target);
		return visitor.visitBitwiseShr(llType, lhs, rhs);
	}

	@Override
	public E visitBitwiseNot(Type.Byte target, E expr) {
		LowLevel.Type.Int llType = convertByte(target);
		return visitor.visitBitwiseNot(llType, expr);
	}

	@Override
	public E visitArrayInitialiser(Type.Array type, List<E> operands) {
		LowLevel.Type.Array llType = convertArray(type);
		return visitor.visitArrayInitialiser(llType, operands);
	}

	@Override
	public E visitArrayGenerator(Type.Array type, E value, E length) {
		LowLevel.Type.Array llType = convertArray(type);
		return visitor.visitArrayGenerator(llType, value, length);
	}

	@Override
	public E visitArrayLength(Type.Array type, E source) {
		LowLevel.Type.Array llType = convertArray(type);
		return visitor.visitArrayLength(llType, source);
	}

	@Override
	public E visitArrayAccess(Type.Array type, E source, E index) {
		ArrayList<LowLevel.Type.Array> types = new ArrayList<>();
		// FIXME: this doesn't feel right.
		types.add(convertArray(type));
		return visitor.visitArrayAccess(types, source, index);
	}

	@Override
	public E visitArrayUpdate(Array type, E source, E index, E value) {
		// NOTE: will need to implement this eventually
		throw new UnsupportedOperationException();
	}

	@Override
	public E visitRecordInitialiser(Record type, List<E> operands) {
		LowLevel.Type.Record llType = convertRecord(type);
		return visitor.visitRecordInitialiser(llType, operands);
	}

	@Override
	public E visitRecordAccess(Type.Record type, E source, Identifier field) {
		LowLevel.Type.Record llType = convertRecord(type);
		return visitor.visitRecordAccess(llType, source, field.get());
	}

	@Override
	public E visitRecordCoercion(Int target, Int actual, E expr) {
		// TODO Auto-generated method stub
		throw new RuntimeException("implement me!");
	}

	@Override
	public E visitNew(Type.Reference type, E operand) {
		LowLevel.Type.Reference llType = convertReference(type);
		return visitor.visitReferenceInitialiser(llType, operand);
	}

	@Override
	public E visitDereference(Reference type, E operand) {
		LowLevel.Type.Reference llType = convertReference(type);
		return visitor.visitReferenceAccess(llType, operand);
	}

	// ==========================================================================
	// Type
	// ==========================================================================
	public List<LowLevel.Type> convertTypes(Tuple<Type> types) {
		ArrayList<LowLevel.Type> result = new ArrayList<>();
		for (int i = 0; i != types.size(); ++i) {
			result.add(convertType(types.get(i)));
		}
		return result;
	}

	public LowLevel.Type convertType(Type type) {
		switch (type.getOpcode()) {
		case TYPE_array:
			return convertArray((Type.Array) type);
		case TYPE_bool:
			return convertBool((Type.Bool) type);
		case TYPE_byte:
			return convertByte((Type.Byte) type);
		case TYPE_int:
			return convertInt((Type.Int) type);
		case TYPE_nominal:
			return convertNominal((Type.Nominal) type);
		case TYPE_null:
			return convertNull((Type.Null) type);
		case TYPE_record:
			return convertRecord((Type.Record) type);
		case TYPE_staticreference:
		case TYPE_reference:
			return convertReference((Type.Reference) type);
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			return convertCallable((Type.Callable) type);
		case TYPE_union:
			return convertUnion((Type.Union) type);
		case TYPE_void:
			return convertVoid((Type.Void) type);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public LowLevel.Type.Array convertArray(Type.Array type) {
		LowLevel.Type element = convertType(type.getElement());
		return visitor.visitTypeArray(element);
	}

	public LowLevel.Type.Void convertVoid(Type.Void type) {
		return visitor.visitTypeVoid();
	}

	public LowLevel.Type.Bool convertBool(Type.Bool type) {
		return visitor.visitTypeBool();
	}

	public LowLevel.Type.Int convertByte(Type.Byte type) {
		return visitor.visitTypeInt(8);
	}

	public LowLevel.Type.Int convertInt(Type.Int type) {
		// FIXME: currently all integer types are unbound.
		return visitor.visitTypeInt(-1);
	}

	public LowLevel.Type.Null convertNull(Type.Null type) {
		return visitor.visitTypeNull();
	}

	public LowLevel.Type.Record convertRecord(Type.Record type) {
		Tuple<Type.Field> fields = type.getFields();
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> nFields = new ArrayList<>();
		for (int i = 0; i != fields.size(); ++i) {
			Type.Field field = fields.get(i);
			LowLevel.Type fieldType = convertType(field.getType());
			nFields.add(new wycc.util.Pair<>(fieldType, field.getName().toString()));
		}
		// FIXME: what to do about open records?
		return visitor.visitTypeRecord(nFields);
	}

	public LowLevel.Type.Reference convertReference(Type.Reference type) {
		LowLevel.Type element = convertType(type.getElement());
		return visitor.visitTypeReference(element);
	}

	public LowLevel.Type.Method convertCallable(Type.Callable type) {
		List<LowLevel.Type> parameters = convertTypes(type.getParameters());
		LowLevel.Type returns = convertType(getMultipleReturnType(type.getReturns()));
		return visitor.visitTypeMethod(parameters, returns);
	}

	public LowLevel.Type.Union convertUnion(Type.Union type) {
		ArrayList<LowLevel.Type> elements = new ArrayList<>();
		for (int i = 0; i != type.size(); ++i) {
			elements.add(convertType(type.get(i)));
		}
		return visitor.visitTypeUnion(elements);
	}

	public LowLevel.Type convertNominal(Type.Nominal type) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
			if (decl.isRecursive()) {
				// FIXME: is this always the correct translation?
				return visitor.visitTypeRecursive(type.getName().toString());
			} else {
				return convertType(decl.getType());
			}
		} catch (NameResolver.ResolutionError e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Determine the appropriate return type for a given callable declaration. The
	 * key challenge here is that we assume target architectures do not support
	 * multiple return values (though, in principle, some might such as LLVM). In
	 * the case that multiple returns are employed, then a record is used to wrap
	 * them into a single return value. The following illustrates both scenarios:
	 *
	 * <pre>
	 * function id(int x) -> (int r):
	 *    return x
	 *
	 * function swap(int x, int y) -> (int a, int b):
	 *    return y,x
	 * </pre>
	 *
	 * These are effectively translated into the following equivalent forms:
	 *
	 * <pre>
	 * function id(int x) -> (int r):
	 *    return x
	 *
	 * function swap(int x, int y) -> {int a, int b}:
	 *    return {a:y, b:x}
	 * </pre>
	 *
	 * We see here that, when only one return is present, then this is used as is.
	 * However, when more than one is required, then they are just wrapped into a
	 * record.
	 *
	 * @param returns
	 * @return
	 */
	public Type getMultipleReturnType(Tuple<Type> returns) {
		if (returns.size() == 0) {
			// No single return value
			return Type.Void;
		} else if (returns.size() == 1) {
			// One return value, so use as is.
			return returns.get(0);
		} else {
			Type.Field[] fields = new Type.Field[returns.size()];
			for (int i = 0; i != fields.length; ++i) {
				Type type = returns.get(i);
				Identifier name = new Identifier("f" + i);
				fields[i] = new Type.Field(name, type);
			}
			return new Type.Record(false, new Tuple<>(fields));
		}
	}


	public String getMangledName(String name, Type.Callable type) {
		Tuple<Identifier> lifetimes;
		if (type instanceof Type.Method) {
			lifetimes = ((Type.Method) type).getLifetimeParameters();
		} else {
			lifetimes = new Tuple<>();
		}
		String mangle = mangler.getMangle(type.getParameters(), lifetimes);
		return name + mangle;
	}

	public String getMangledName(String name, Type type) {
		return name + mangler.getMangle(new Tuple<>(type), new Tuple<>());
	}


	private static int tempIndex = 0;

	public String createTemporaryVariable() {
		// FIXME: need to do better here!!!
		return "tmp$" + tempIndex++;
	}
}
