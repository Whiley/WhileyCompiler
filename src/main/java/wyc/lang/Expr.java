// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.lang;

import java.util.*;

import wybs.lang.Attribute;
import wybs.lang.NameID;
import wybs.lang.SyntacticElement;
import wyc.builder.FlowTypeChecker;
import wyc.io.WhileyFileLexer;
import wycc.util.Pair;
import wycc.util.Triple;
import wyfs.lang.Path;
import wyil.lang.*;

/**
 * Provides classes for representing expressions in Whiley's source language.
 * Examples include <i>binary operators</i>, <i>integer constants</i>, <i>field
 * accesses</i>, etc. Each class is an instance of <code>SyntacticElement</code>
 * and, hence, can be adorned with certain information (such as source location,
 * etc).
 *
 * @author David J. Pearce
 *
 */
public interface Expr extends SyntacticElement {

	/**
	 * Get the type that this expression will evaluate to. This type splits into
	 * a nominal and raw component. The nominal component retains name
	 * information and, as such, is incomplete. This means one should not use
	 * the nominal component for subtype testing; rather it should only be used
	 * for reporting information to the user (e.g. type errors, etc). The raw
	 * component represents the fully expanded type, and can safely be used for
	 * type testing. However, it can be rather long and cumbersome to read so
	 * should not be reported to the user.
	 *
	 * @return
	 */
	public Type result();

	/**
	 * An LVal is a special form of expression which may appear on the left-hand
	 * side of an assignment.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface LVal extends Expr {}

	/**
	 * A Multi expression is one which returns multiple values. Certain
	 * expression forms are permitted to return multiple values and these
	 * implement Multi.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Multi extends SyntacticElement{
		/**
		 * Get all the return types this expression can produce.
		 *
		 * @return
		 */
		public List<Type> returns();
	}

	public static class AbstractVariable extends SyntacticElement.Impl implements Expr, LVal {
		public final String var;

		public AbstractVariable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}

		public AbstractVariable(String var, Collection<Attribute> attributes) {
			super(attributes);
			this.var = var;
		}

		@Override
		public Type result() {
			return null;
		}

		@Override
		public String toString() {
			return var;
		}
	}

	public static class LocalVariable extends AbstractVariable {
		public Type type;

		public LocalVariable(String var, Attribute... attributes) {
			super(var, attributes);
		}

		public LocalVariable(String var, Collection<Attribute> attributes) {
			super(var, attributes);
		}

		@Override
		public Type result() {
			return type;
		}

		@Override
		public String toString() {
			return var;
		}
	}

	public static class AssignedVariable extends LocalVariable {
		public Type afterType;

		public AssignedVariable(String var, Attribute... attributes) {
			super(var, attributes);
		}

		public AssignedVariable(String var, Collection<Attribute> attributes) {
			super(var, attributes);
		}
	}

	public static class Constant extends SyntacticElement.Impl implements Expr {
		public final wyil.lang.Constant value;

		public Constant(wyil.lang.Constant val, Attribute... attributes) {
			super(attributes);
			this.value = val;
		}

		@Override
		public Type result() {
			return value.type();
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	/**
	 * Represents a cast expression, which has the form:
	 *
	 * <pre>
	 * Term ::= ...
	 *      | '(' Type ')' Expression
	 * </pre>
	 *
	 * @param start
	 * @return
	 */
	public static class Cast extends SyntacticElement.Impl implements Expr {
		public final SyntacticType unresolvedType;
		public Type type;
		public Expr expr;

		public Cast(SyntacticType type, Expr expr, Attribute... attributes) {
			super(attributes);
			this.unresolvedType = type;
			this.expr = expr;
		}

		@Override
		public Type result() {
			return type;
		}

		@Override
		public String toString() {
			return "(" + unresolvedType.toString() + ") " + expr;
		}
	}

	public static class TypeVal extends SyntacticElement.Impl implements Expr {
		public final SyntacticType unresolvedType;
		public Type type;

		public TypeVal(SyntacticType val, Attribute... attributes) {
			super(attributes);
			this.unresolvedType = val;
		}

		@Override
		public Type result() {
			return Type.T_META;
		}
	}

	public static class AbstractFunctionOrMethod extends SyntacticElement.Impl implements Expr {
		public final String name;
		public final ArrayList<SyntacticType> paramTypes;
		public final ArrayList<String> lifetimeParameters;
		public Type.FunctionOrMethod type;

		public AbstractFunctionOrMethod(String name, Collection<SyntacticType> paramTypes,
				Collection<String> lifetimeParameters, Attribute... attributes) {
			super(attributes);
			this.name = name;
			if(paramTypes != null) {
				this.paramTypes = new ArrayList<>(paramTypes);
			} else {
				this.paramTypes = null;
			}
			if(lifetimeParameters != null) {
				this.lifetimeParameters = new ArrayList<>(lifetimeParameters);
			} else {
				this.lifetimeParameters = null;
			}
		}

		public AbstractFunctionOrMethod(String name,
				Collection<SyntacticType> paramTypes,
				Collection<String> lifetimeParameters,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			if(paramTypes != null) {
				this.paramTypes = new ArrayList<>(paramTypes);
			} else {
				this.paramTypes = null;
			}
			if(lifetimeParameters != null) {
				this.lifetimeParameters = new ArrayList<>(lifetimeParameters);
			} else {
				this.lifetimeParameters = null;
			}
		}

		@Override
		public Type.FunctionOrMethod result() {
			return type;
		}
	}

	public static class FunctionOrMethod extends AbstractFunctionOrMethod {
		public final NameID nid;

		public FunctionOrMethod(NameID nid, Collection<SyntacticType> paramTypes,
				Collection<String> lifetimeParameters, Attribute... attributes) {
			super(nid.name(), paramTypes, lifetimeParameters, attributes);
			this.nid = nid;
		}

		public FunctionOrMethod(NameID nid, Collection<SyntacticType> paramTypes,
				Collection<String> lifetimeParameters, Collection<Attribute> attributes) {
			super(nid.name(), paramTypes, lifetimeParameters, attributes);
			this.nid = nid;
		}
	}

	public static class Lambda extends SyntacticElement.Impl implements Expr {
		public final ArrayList<WhileyFile.Parameter> parameters;
		public final HashSet<String> contextLifetimes;
		public final ArrayList<String> lifetimeParameters;
		public Expr body;
		public Type.FunctionOrMethod type;

		public Lambda(Collection<WhileyFile.Parameter> parameters, Collection<String> contextLifetimes,
				Collection<String> lifetimeParameters, Expr body, Attribute... attributes) {
			super(attributes);
			this.parameters = new ArrayList<>(parameters);
			this.contextLifetimes = new HashSet<>(contextLifetimes);
			this.lifetimeParameters = new ArrayList<>(lifetimeParameters);
			this.body = body;
		}

		public Lambda(Collection<WhileyFile.Parameter> parameters, Collection<String> contextLifetimes,
				Collection<String> lifetimeParameters, Expr body, Collection<Attribute> attributes) {
			super(attributes);
			this.parameters = new ArrayList<>(parameters);
			this.contextLifetimes = new HashSet<>(contextLifetimes);
			this.lifetimeParameters = new ArrayList<>(lifetimeParameters);
			this.body = body;
		}

		@Override
		public Type.FunctionOrMethod result() {
			return type;
		}
	}

	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public BOp op;
		public Expr lhs;
		public Expr rhs;
		public Type srcType;

		public BinOp(BOp op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public BinOp(BOp op, Expr lhs, Expr rhs, Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		@Override
		public Type result() {
			switch(op) {
			case EQ:
			case NEQ:
			case LT:
			case LTEQ:
			case GT:
			case GTEQ:
			case IS:
				return Type.T_BOOL;
			default:
				return srcType;
			}
		}

		public Type srcType() {
			return srcType;
		}

		@Override
		public String toString() {
			return "(" + op + " " + lhs + " " + rhs + ")";
		}
	}

	// A list access is very similar to a BinOp, except that it can be assiged.
	public static class IndexOf extends SyntacticElement.Impl implements
			Expr, LVal {
		public Expr src;
		public Expr index;
		public Type.EffectiveArray srcType;

		public IndexOf(Expr src, Expr index, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}

		public IndexOf(Expr src, Expr index, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}

		@Override
		public Type result() {
			return srcType.getReadableElementType();
		}

		@Override
		public String toString() {
			return src + "[" + index + "]";
		}
	}

	public enum UOp {
		NOT,
		NEG,
		INVERT,
		ARRAYLENGTH
	}

	public static class UnOp extends SyntacticElement.Impl implements Expr {
		public final UOp op;
		public Expr mhs;
		public Type type;

		public UnOp(UOp op, Expr mhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;
		}

		@Override
		public Type result() {
			if(op == UOp.ARRAYLENGTH) {
				return Type.T_INT;
			} else {
				return type;
			}
		}

		@Override
		public String toString() {
			return op + mhs.toString();
		}
	}

	/**
	 * Represents an array initialiser expression, which is of the form:
	 *
	 * <pre>
	 * ArrayInitialiser ::= '[' [ Expression (',' Expression)+ ] ']'
	 * </pre>
	 *
	 * @return
	 */
	public static class ArrayInitialiser extends SyntacticElement.Impl implements Expr {
		public final ArrayList<Expr> arguments;
		public Type.Array type;

		public ArrayInitialiser(Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.arguments = new ArrayList<>(arguments);
		}

		public ArrayInitialiser(Attribute attribute, Expr... arguments) {
			super(attribute);
			this.arguments = new ArrayList<>();
			for(Expr a : arguments) {
				this.arguments.add(a);
			}
		}

		@Override
		public Type.Array result() {
			return type;
		}
	}

	/**
	 * Represents an array generator expression, which is of the form:
	 *
	 * <pre>
	 * ArrayGenerator ::= '[' Expression ';' Expression ']'
	 * </pre>
	 *
	 * @return
	 */
	public static class ArrayGenerator extends SyntacticElement.Impl implements Expr {
		public Expr element;
		public Expr count;
		public Type.Array type;

		public ArrayGenerator(Expr element, Expr count, Attribute... attributes) {
			super(attributes);
			this.element = element;
			this.count = count;
		}

		@Override
		public Type.Array result() {
			return type;
		}
	}

	public static class Quantifier extends SyntacticElement.Impl implements Expr {
		public final QOp cop;
		public final ArrayList<Triple<String,Expr,Expr>> sources;
		public Expr condition;
		public Type type;

		public Quantifier(QOp cop,
				Collection<Triple<String, Expr, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.condition = condition;
			this.sources = new ArrayList<>(sources);
		}

		@Override
		public Type result() {
			return type;
		}
	}

	public enum QOp {
		SOME, ALL,
	}

	public static class FieldAccess extends SyntacticElement.Impl implements LVal {
		public Expr src;
		public final String name;
		public Type.EffectiveRecord srcType;

		public FieldAccess(Expr lhs, String name, Attribute... attributes) {
			super(attributes);
			this.src = lhs;
			this.name = name;
		}

		public FieldAccess(Expr lhs, String name,
				Collection<Attribute> attributes) {
			super(attributes);
			this.src = lhs;
			this.name = name;
		}

		@Override
		public Type result() {
			return srcType.getReadableFieldType(name);
		}

		@Override
		public String toString() {
			return src + "." + name;
		}
	}

	public static class ConstantAccess extends SyntacticElement.Impl implements Expr {
		public final String name;
		public Path.ID qualification;
		public wyil.lang.Constant value;
		public Type type;

		public ConstantAccess(String name, Path.ID qualification,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.qualification = qualification;
		}

		public ConstantAccess(String name, Path.ID qualification,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.qualification = qualification;
		}

		@Override
		public Type result() {
			// Note: must return our type here, rather than value.type(). This
			// is because value.type() does not distinguish nominal and raw
			// types.  See #544.
			return type;
		}

		@Override
		public String toString() {
			if(qualification == null) {
				// root
				return name;
			} else {
				return qualification + "." + name;
			}
		}
	}

	public static class Dereference extends SyntacticElement.Impl implements LVal {
		public Expr src;
		public Type.Reference srcType;

		public Dereference(Expr src, Attribute... attributes) {
			super(attributes);
			this.src = src;
		}

		@Override
		public Type result() {
			return srcType.element();
		}

		@Override
		public String toString() {
			return "*" + src.toString();
		}
	}

	public static class Record extends SyntacticElement.Impl implements
			Expr {
		public final String name;
		public final HashMap<String, Expr> fields;
		public Type type;

		public Record(String name, java.util.Map<String, Expr> fields,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.fields = new HashMap<>(fields);
		}

		@Override
		public Type result() {
			return type;
		}
	}

	public static class AbstractInvoke extends SyntacticElement.Impl implements Expr,
			Stmt {
		public final String name;
		public Path.ID qualification;
		public final ArrayList<Expr> arguments;
		public final ArrayList<String> lifetimeArguments;

		public AbstractInvoke(String name, Path.ID receiver,
				Collection<Expr> arguments, Collection<String> lifetimeArguments,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.qualification = receiver;
			this.arguments = new ArrayList<>(arguments);
			if (lifetimeArguments != null) {
				this.lifetimeArguments = new ArrayList<>(lifetimeArguments);
			} else {
				this.lifetimeArguments = null;
			}
		}

		public AbstractInvoke(String name, Path.ID receiver,
				Collection<Expr> arguments, Collection<String> lifetimeArguments,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.qualification = receiver;
			this.arguments = new ArrayList<>(arguments);
			if (lifetimeArguments != null) {
				this.lifetimeArguments = new ArrayList<>(lifetimeArguments);
			} else {
				this.lifetimeArguments = null;
			}
		}

		@Override
		public Type result() {
			return null;
		}
	}

	public static abstract class FunctionOrMethodCall extends AbstractInvoke implements Multi {
		public final NameID nid;

		public FunctionOrMethodCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Attribute... attributes) {
			super(nid.name(),qualification,arguments,lifetimeArguments,attributes);
			this.nid = nid;
		}

		public FunctionOrMethodCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Collection<Attribute> attributes) {
			super(nid.name(),qualification,arguments,lifetimeArguments,attributes);
			this.nid = nid;
		}

		public NameID nid() {
			return nid;
		}

		public abstract Type.FunctionOrMethod type();

		@Override
		public List<Type> returns() {
			return Arrays.asList(type().returns());
		}
	}

	public static class MethodCall extends FunctionOrMethodCall {
		public Type.Method methodType;

		public MethodCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Attribute... attributes) {
			super(nid,qualification,arguments,lifetimeArguments,attributes);
		}

		public MethodCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Collection<Attribute> attributes) {
			super(nid,qualification,arguments,lifetimeArguments,attributes);
		}

		@Override
		public Type.Method type() {
			return methodType;
		}

		@Override
		public Type result() {
			if (methodType.returns().length == 1) {
				Type returnType = methodType.returns()[0];
				if (this.lifetimeArguments == null || this.lifetimeArguments.isEmpty()) {
					return returnType;
				}
				List<String> lifetimeParams = Arrays.asList(methodType.lifetimeParams());
				return FlowTypeChecker.applySubstitution(lifetimeParams, this.lifetimeArguments, returnType);
			} else {
				throw new IllegalArgumentException("incorrect number of returns for function call");
			}
		}
	}

	/**
	 * Parse a function invocation expression, which has the form:
	 *
	 * <pre>
	 * NameIdentifier '(' [ Expression ( ',' Expression )* ] ')'
	 * </pre>
	 *
	 * @return
	 */
	public static class FunctionCall extends FunctionOrMethodCall {

		public Type.Function functionType;

		public FunctionCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Attribute... attributes) {
			super(nid,qualification,arguments,Collections.<String>emptyList(),attributes);
		}

		public FunctionCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(nid,qualification,arguments,Collections.<String>emptyList(),attributes);
		}

		@Override
		public Type.Function type() {
			return functionType;
		}

		@Override
		public Type result() {
			if(functionType.returns().length == 1) {
				return functionType.returns()[0];
			} else {
				throw new IllegalArgumentException("incorrect number of returns for function call");
			}
		}
	}

	public static class PropertyCall extends FunctionOrMethodCall {

		public Type.Property propertyType;

		public PropertyCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Attribute... attributes) {
			super(nid,qualification,arguments,Collections.<String>emptyList(),attributes);
		}

		public PropertyCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(nid,qualification,arguments,Collections.<String>emptyList(),attributes);
		}

		@Override
		public Type.Property type() {
			return propertyType;
		}

		@Override
		public Type result() {
			return Type.T_BOOL;
		}
	}

	public static class AbstractIndirectInvoke extends SyntacticElement.Impl implements Expr,
	Stmt {
		public Expr src;
		public final ArrayList<Expr> arguments;
		public final ArrayList<String> lifetimeArguments;

		public AbstractIndirectInvoke(Expr src,
				Collection<Expr> arguments,
				Collection<String> lifetimeArguments,
				Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.arguments = new ArrayList<>(arguments);
			this.lifetimeArguments = lifetimeArguments == null ? null : new ArrayList<>(lifetimeArguments);
		}

		public AbstractIndirectInvoke(Expr src,
				Collection<Expr> arguments,
				Collection<String> lifetimeArguments,
				Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.arguments = new ArrayList<>(arguments);
			this.lifetimeArguments = lifetimeArguments == null ? null : new ArrayList<>(lifetimeArguments);
		}

		@Override
		public Type result() {
			return null;
		}
	}

	public static abstract class IndirectFunctionOrMethodCall extends AbstractIndirectInvoke implements Multi {
		public IndirectFunctionOrMethodCall(Expr src, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Attribute... attributes) {
			super(src,arguments,lifetimeArguments,attributes);
		}

		public IndirectFunctionOrMethodCall(Expr src, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Collection<Attribute> attributes) {
			super(src,arguments,lifetimeArguments,attributes);
		}

		public abstract Type.FunctionOrMethod type();

		@Override
		public List<Type> returns() {
			return Arrays.asList(type().returns());
		}
	}

	public static class IndirectMethodCall extends IndirectFunctionOrMethodCall {
		public Type.Method methodType;

		public IndirectMethodCall(Expr src, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Attribute... attributes) {
			super(src,arguments,lifetimeArguments,attributes);
		}

		public IndirectMethodCall(Expr src, Collection<Expr> arguments,
				Collection<String> lifetimeArguments, Collection<Attribute> attributes) {
			super(src,arguments,lifetimeArguments,attributes);
		}

		@Override
		public Type result() {
			if (methodType.returns().length == 1) {
				Type returnType = methodType.returns()[0];
				if (this.lifetimeArguments == null || this.lifetimeArguments.isEmpty()) {
					return returnType;
				}
				List<String> lifetimeParams = Arrays.asList(methodType.lifetimeParams());
				return FlowTypeChecker.applySubstitution(lifetimeParams, this.lifetimeArguments, returnType);
			} else {
				throw new IllegalArgumentException("incorrect number of returns for indirect method call");
			}
		}

		@Override
		public Type.FunctionOrMethod type() {
			return methodType;
		}
	}

	public static class IndirectFunctionCall extends IndirectFunctionOrMethodCall {
		public Type.Function functionType;

		public IndirectFunctionCall(Expr src, Collection<Expr> arguments,
				Attribute... attributes) {
			super(src,arguments,Collections.<String>emptyList(),attributes);
		}

		public IndirectFunctionCall(Expr src, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(src,arguments,Collections.<String>emptyList(),attributes);
		}

		@Override
		public Type result() {
			if(functionType.returns().length == 1) {
				return functionType.returns()[0];
			} else {
				throw new IllegalArgumentException("incorrect number of returns for indirect function call");
			}
		}

		@Override
		public Type.FunctionOrMethod type() {
			return functionType;
		}
	}

	public static class New extends SyntacticElement.Impl implements Expr,Stmt {
		public Expr expr;
		public Type.Reference type;
		public String lifetime;

		public New(Expr expr, String lifetime, Attribute... attributes) {
			super(attributes);
			this.lifetime = lifetime;
			this.expr = expr;
		}

		@Override
		public Type.Reference result() {
			return type;
		}
	}

	public enum BOp {
		AND {
			@Override
			public String toString() { return "&&"; }
		},
		OR{
			@Override
			public String toString() { return "||"; }
		},
		XOR {
			@Override
			public String toString() { return "^^"; }
		},
		ADD{
			@Override
			public String toString() { return "+"; }
		},
		SUB{
			@Override
			public String toString() { return "-"; }
		},
		MUL{
			@Override
			public String toString() { return "*"; }
		},
		DIV{
			@Override
			public String toString() { return "/"; }
		},
		REM{
			@Override
			public String toString() { return "%"; }
		},
		UNION{
			@Override
			public String toString() { return "+"; }
		},
		INTERSECTION{
			@Override
			public String toString() { return "&"; }
		},
		DIFFERENCE{
			@Override
			public String toString() { return "-"; }
		},
		EQ{
			@Override
			public String toString() { return "=="; }
		},
		NEQ{
			@Override
			public String toString() { return "!="; }
		},
		LT{
			@Override
			public String toString() { return "<"; }
		},
		LTEQ{
			@Override
			public String toString() { return "<="; }
		},
		GT{
			@Override
			public String toString() { return ">"; }
		},
		GTEQ{
			@Override
			public String toString() { return ">="; }
		},
		RANGE{
			@Override
			public String toString() { return ".."; }
		},
		IS{
			@Override
			public String toString() { return "is"; }
		},
		BITWISEAND {
			@Override
			public String toString() { return "&"; }
		},
		BITWISEOR{
			@Override
			public String toString() { return "|"; }
		},
		BITWISEXOR {
			@Override
			public String toString() { return "^"; }
		},
		LEFTSHIFT {
			@Override
			public String toString() { return "<<"; }
		},
		RIGHTSHIFT {
			@Override
			public String toString() { return ">>"; }
		},
	};
}
