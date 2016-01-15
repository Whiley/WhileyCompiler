// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyc.lang;

import java.util.*;

import wyc.io.WhileyFileLexer;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
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
	public Nominal result();

	/**
	 * An LVal is a special form of expression which may appear on the left-hand
	 * side of an assignment.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface LVal extends Expr {}

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

		public Nominal result() {
			return null;
		}

		public String toString() {
			return var;
		}
	}

	public static class LocalVariable extends AbstractVariable {
		public Nominal type;

		public LocalVariable(String var, Attribute... attributes) {
			super(var, attributes);
		}

		public LocalVariable(String var, Collection<Attribute> attributes) {
			super(var, attributes);
		}

		public Nominal result() {
			return type;
		}

		public String toString() {
			return var;
		}
	}

	public static class AssignedVariable extends LocalVariable {
		public Nominal afterType;

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

		public Nominal result() {
			return Nominal.construct(value.type(),value.type());
		}

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
		public Nominal type;
		public Expr expr;

		public Cast(SyntacticType type, Expr expr, Attribute... attributes) {
			super(attributes);
			this.unresolvedType = type;
			this.expr = expr;
		}

		public Nominal result() {
			return type;
		}

		public String toString() {
			return "(" + unresolvedType.toString() + ") " + expr;
		}
	}

	public static class TypeVal extends SyntacticElement.Impl implements Expr {
		public final SyntacticType unresolvedType;
		public Nominal type;

		public TypeVal(SyntacticType val, Attribute... attributes) {
			super(attributes);
			this.unresolvedType = val;
		}

		public Nominal result() {
			return Nominal.T_META;
		}
	}

	public static class AbstractFunctionOrMethod extends SyntacticElement.Impl implements Expr {
		public final String name;
		public final ArrayList<SyntacticType> paramTypes;
		public Nominal.FunctionOrMethod type;

		public AbstractFunctionOrMethod(String name, Collection<SyntacticType> paramTypes, Attribute... attributes) {
			super(attributes);
			this.name = name;
			if(paramTypes != null) {
				this.paramTypes = new ArrayList<SyntacticType>(paramTypes);
			} else {
				this.paramTypes = null;
			}
		}

		public AbstractFunctionOrMethod(String name,
				Collection<SyntacticType> paramTypes,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			if(paramTypes != null) {
				this.paramTypes = new ArrayList<SyntacticType>(paramTypes);
			} else {
				this.paramTypes = null;
			}
		}

		public Nominal.FunctionOrMethod result() {
			return type;
		}
	}

	public static class FunctionOrMethod extends AbstractFunctionOrMethod {
		public final NameID nid;

		public FunctionOrMethod(NameID nid, Collection<SyntacticType> paramTypes,
				Attribute... attributes) {
			super(nid.name(), paramTypes, attributes);
			this.nid = nid;
		}

		public FunctionOrMethod(NameID nid, Collection<SyntacticType> paramTypes,
				Collection<Attribute> attributes) {
			super(nid.name(), paramTypes, attributes);
			this.nid = nid;
		}
	}

	public static class Lambda extends SyntacticElement.Impl implements Expr {
		public final ArrayList<WhileyFile.Parameter> parameters;
		public Expr body;
		public Nominal.FunctionOrMethod type;

		public Lambda(Collection<WhileyFile.Parameter> parameters, Expr body,
				Attribute... attributes) {
			super(attributes);
			this.parameters = new ArrayList<WhileyFile.Parameter>(parameters);
			this.body = body;
		}

		public Lambda(Collection<WhileyFile.Parameter> parameters, Expr body,
				Collection<Attribute> attributes) {
			super(attributes);
			this.parameters = new ArrayList<WhileyFile.Parameter>(parameters);
			this.body = body;
		}

		public Nominal.FunctionOrMethod result() {
			return type;
		}
	}

	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public BOp op;
		public Expr lhs;
		public Expr rhs;
		public Nominal srcType;

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

		public Nominal result() {
			switch(op) {
			case EQ:
			case NEQ:
			case LT:
			case LTEQ:
			case GT:
			case GTEQ:
			case IS:
				return Nominal.T_BOOL;
			default:
				return srcType;
			}
		}

		public Nominal srcType() {
			return srcType;
		}

		public String toString() {
			return "(" + op + " " + lhs + " " + rhs + ")";
		}
	}

	// A list access is very similar to a BinOp, except that it can be assiged.
	public static class IndexOf extends SyntacticElement.Impl implements
			Expr, LVal {
		public Expr src;
		public Expr index;
		public Nominal.Array srcType;

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

		public Nominal result() {
			return srcType.value();
		}

		public String toString() {
			return src + "[" + index + "]";
		}
	}

	public enum UOp {
		NOT,
		NEG,
		INVERT
	}

	public static class UnOp extends SyntacticElement.Impl implements Expr {
		public final UOp op;
		public Expr mhs;
		public Nominal type;

		public UnOp(UOp op, Expr mhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;
		}

		public Nominal result() {
			return type;
		}

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
		public Nominal.Array type;

		public ArrayInitialiser(Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.arguments = new ArrayList<Expr>(arguments);
		}

		public ArrayInitialiser(Attribute attribute, Expr... arguments) {
			super(attribute);
			this.arguments = new ArrayList<Expr>();
			for(Expr a : arguments) {
				this.arguments.add(a);
			}
		}

		public Nominal.Array result() {
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
		public Nominal.Array type;

		public ArrayGenerator(Expr element, Expr count, Attribute... attributes) {
			super(attributes);
			this.element = element;
			this.count = count;
		}

		public Nominal.Array result() {
			return type;
		}
	}
	
	public static class Quantifier extends SyntacticElement.Impl implements Expr {
		public final QOp cop;
		public final ArrayList<Triple<String,Expr,Expr>> sources;
		public Expr condition;
		public Nominal type;

		public Quantifier(QOp cop, 
				Collection<Triple<String, Expr, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.condition = condition;
			this.sources = new ArrayList<Triple<String, Expr, Expr>>(sources);
		}

		public Nominal result() {
			return type;
		}
	}

	public enum QOp {
		NONE, // implies value == null
		SOME, // implies value == null
		ALL, // implies value == null
	}

	public static class FieldAccess extends SyntacticElement.Impl
			implements
				LVal {
		public Expr src;
		public final String name;
		public Nominal.Record srcType;

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

		public Nominal result() {
			return srcType.field(name);
		}

		public String toString() {
			return src + "." + name;
		}
	}

	public static class ConstantAccess extends SyntacticElement.Impl implements Expr {
		public final String name;
		public Path.ID qualification;
		public wyil.lang.Constant value;
		public Nominal type;

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

		public Nominal result() {
			// Note: must return our type here, rather than value.type(). This
			// is because value.type() does not distinguish nominal and raw
			// types.  See #544.
			return type;
		}

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
		public Nominal.Reference srcType;

		public Dereference(Expr src, Attribute... attributes) {
			super(attributes);
			this.src = src;
		}

		public Nominal result() {
			return srcType.element();
		}

		public String toString() {
			return "*" + src.toString();
		}
	}

	public static class Record extends SyntacticElement.Impl implements
			Expr {
		public final HashMap<String, Expr> fields;
		public Nominal.Record type;

		public Record(java.util.Map<String, Expr> fields,
				Attribute... attributes) {
			super(attributes);
			this.fields = new HashMap<String, Expr>(fields);
		}

		public Nominal.Record result() {
			return type;
		}
	}

	public static class AbstractInvoke extends SyntacticElement.Impl implements Expr,
			Stmt {
		public final String name;
		public Path.ID qualification;
		public final ArrayList<Expr> arguments;

		public AbstractInvoke(String name, Path.ID receiver,
				Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.qualification = receiver;
			this.arguments = new ArrayList<Expr>(arguments);
		}

		public AbstractInvoke(String name, Path.ID receiver,
				Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.qualification = receiver;
			this.arguments = new ArrayList<Expr>(arguments);
		}

		public Nominal result() {
			return null;
		}
	}

	public static class MethodCall extends AbstractInvoke {
		public final NameID nid;
		public Nominal.Method methodType;

		public MethodCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Attribute... attributes) {
			super(nid.name(),qualification,arguments,attributes);
			this.nid = nid;
		}

		public MethodCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(nid.name(),qualification,arguments,attributes);
			this.nid = nid;
		}

		public NameID nid() {
			return nid;
		}

		public Nominal result() {
			if(methodType.returns().isEmpty()) {
				return null;
			} else {
				return methodType.returns().get(0);
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
	public static class FunctionCall extends AbstractInvoke {
		public final NameID nid;
		public Nominal.Function functionType;

		public FunctionCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Attribute... attributes) {
			super(nid.name(),qualification,arguments,attributes);
			this.nid = nid;
		}

		public FunctionCall(NameID nid, Path.ID qualification, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(nid.name(),qualification,arguments,attributes);
			this.nid = nid;
		}

		public NameID nid() {
			return nid;
		}

		public Nominal result() {
			return functionType.returns().get(0);
		}
	}

	public static class AbstractIndirectInvoke extends SyntacticElement.Impl implements Expr,
	Stmt {
		public Expr src;
		public final ArrayList<Expr> arguments;

		public AbstractIndirectInvoke(Expr src,
				Collection<Expr> arguments,
				Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.arguments = new ArrayList<Expr>(arguments);
		}

		public AbstractIndirectInvoke(Expr src,
				Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.arguments = new ArrayList<Expr>(arguments);
		}

		public Nominal result() {
			return null;
		}
	}

	public static class IndirectMethodCall extends AbstractIndirectInvoke {
		public Nominal.Method methodType;

		public IndirectMethodCall(Expr src, Collection<Expr> arguments,
				Attribute... attributes) {
			super(src,arguments,attributes);
		}

		public IndirectMethodCall(Expr src, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(src,arguments,attributes);
		}

		public Nominal result() {
			return methodType.returns().get(0);
		}
	}

	public static class IndirectFunctionCall extends AbstractIndirectInvoke {
		public Nominal.Function functionType;

		public IndirectFunctionCall(Expr src, Collection<Expr> arguments,
				Attribute... attributes) {
			super(src,arguments,attributes);
		}

		public IndirectFunctionCall(Expr src, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(src,arguments,attributes);
		}

		public Nominal result() {
			return functionType.returns().get(0);
		}
	}

	public static class LengthOf extends SyntacticElement.Impl implements Expr {
		public Expr src;
		public Nominal.Array srcType;

		public LengthOf(Expr mhs, Attribute... attributes) {
			super(attributes);
			this.src = mhs;
		}

		public LengthOf(Expr mhs, Collection<Attribute> attributes) {
			super(attributes);
			this.src = mhs;
		}

		public Nominal result() {
			return Nominal.T_INT;
		}

		public String toString() {
			return "|" + src.toString() + "|";
		}
	}

	public static class New extends SyntacticElement.Impl implements Expr,Stmt {
		public Expr expr;
		public Nominal.Reference type;

		public New(Expr expr, Attribute... attributes) {
			this.expr = expr;
		}

		public Nominal.Reference result() {
			return type;
		}
	}

	public static class RationalLVal extends SyntacticElement.Impl implements
	LVal {
		public LVal numerator;
		public LVal denominator;

		public RationalLVal(LVal num, LVal den, Attribute... attributes) {
			super(attributes);
			this.numerator = num;
			this.denominator = den;
		}

		public final Nominal result() {
			return null; // better be dead-code
		}
	}

	public enum BOp {
		AND {
			public String toString() { return "&&"; }
		},
		OR{
			public String toString() { return "||"; }
		},
		XOR {
			public String toString() { return "^^"; }
		},
		ADD{
			public String toString() { return "+"; }
		},
		SUB{
			public String toString() { return "-"; }
		},
		MUL{
			public String toString() { return "*"; }
		},
		DIV{
			public String toString() { return "/"; }
		},
		REM{
			public String toString() { return "%"; }
		},
		UNION{
			public String toString() { return "+"; }
		},
		INTERSECTION{
			public String toString() { return "&"; }
		},
		DIFFERENCE{
			public String toString() { return "-"; }
		},		
		EQ{
			public String toString() { return "=="; }
		},
		NEQ{
			public String toString() { return "!="; }
		},
		LT{
			public String toString() { return "<"; }
		},
		LTEQ{
			public String toString() { return "<="; }
		},
		GT{
			public String toString() { return ">"; }
		},
		GTEQ{
			public String toString() { return ">="; }
		},		
		RANGE{
			public String toString() { return ".."; }
		},
		IS{
			public String toString() { return "is"; }
		},
		BITWISEAND {
			public String toString() { return "&"; }
		},
		BITWISEOR{
			public String toString() { return "|"; }
		},
		BITWISEXOR {
			public String toString() { return "^"; }
		},
		LEFTSHIFT {
			public String toString() { return "<<"; }
		},
		RIGHTSHIFT {
			public String toString() { return ">>"; }
		},
	};
}
