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

import wyil.lang.*;
import wyil.util.Pair;
import wyil.util.SyntacticElement;

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
	 * Get the type that this expression will evaluate to. This type may contain
	 * nominal information that could be further expanded. This means one should
	 * not use this type for subtype testing; rather it should only be used for
	 * reporting information to the user (e.g. type errors, etc).
	 * 
	 * @return
	 */
	public Type nominalType();

	/**
	 * Get the raw type that this expression will evaluate to. This type may
	 * only contain nominal information that cannot be further expanded (e.g.
	 * because it's declared private). This type can safely be used for subtype
	 * testing.
	 * 
	 * @return
	 */
	public Type rawType();
	
	/**
	 * An LVal is a special form of expression which may appear on the left-hand
	 * side of an assignment.
	 * 
	 * @author djp
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
		
		public Type nominalType() {
			return null;
		}
		
		public Type rawType() {
			return null;
		}
		
		public String toString() {
			return var;
		}
	}

	public static class LocalVariable extends AbstractVariable {		
		public Type nominalType;
		public Type rawType;

		public LocalVariable(String var, Attribute... attributes) {
			super(var, attributes);			
		}

		public LocalVariable(String var, Collection<Attribute> attributes) {
			super(var, attributes);			
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type rawType() {
			return rawType;
		}
		
		public String toString() {
			return var;
		}
	}
		
	public static class Constant extends SyntacticElement.Impl implements Expr {
		public final Value value;

		public Constant(Value val, Attribute... attributes) {
			super(attributes);
			this.value = val;
		}
		
		public Type nominalType() {
			return value.type();
		}
		
		public Type rawType() {
			return value.type();
		}
		
		public String toString() {
			return value.toString();
		}
	}

	public static class Convert extends SyntacticElement.Impl implements Expr {
		public final UnresolvedType unresolvedType;
		public Type nominalType;
		public Type rawType;				
		public Expr expr;	
		
		public Convert(UnresolvedType type, Expr expr, Attribute... attributes) {
			super(attributes);
			this.unresolvedType = type;
			this.expr = expr;
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type rawType() {
			return rawType;
		}
		
		public String toString() {
			return "(" + unresolvedType.toString() + ") " + expr;
		}
	}
	
	public static class TypeVal extends SyntacticElement.Impl implements Expr {
		public final UnresolvedType unresolvedType;
		public Type nominalType;
		public Type rawType;
		
		public TypeVal(UnresolvedType val, Attribute... attributes) {
			super(attributes);
			this.unresolvedType = val;
		}
		

		public Type nominalType() {
			return nominalType;
		}
		
		public Type rawType() {
			return rawType;
		}
	}
	
	public static class Function extends SyntacticElement.Impl implements Expr {
		public final String name;
		public final ArrayList<UnresolvedType> paramTypes;
		public Type nominalType;
		public Type.Function rawType;
		
		public Function(String name, Collection<UnresolvedType> paramTypes, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.paramTypes = new ArrayList<UnresolvedType>(paramTypes);
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type.Function rawType() {
			return rawType;
		}
	}
	
	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public BOp op;
		public Expr lhs;
		public Expr rhs;
		public Type nominalType;
		public Type rawType;
		
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
		
		public Type nominalType() {
			switch(op) {
			case EQ:
			case NEQ:
			case LT:	
			case LTEQ:
			case GT:	
			case GTEQ:
			case SUBSET:	
			case SUBSETEQ:
			case TYPEEQ:				
				return Type.T_BOOL;
			default:
				return nominalType;
			}			
		}
		
		public Type rawType() {
			switch(op) {
			case EQ:
			case NEQ:
			case LT:	
			case LTEQ:
			case GT:	
			case GTEQ:
			case SUBSET:	
			case SUBSETEQ:
			case TYPEEQ:				
				return Type.T_BOOL;
			default:
				return rawType;
			}			
		}
		
		public String toString() {
			return "(" + op + " " + lhs + " " + rhs + ")";
		}
	}

	// A list access is very similar to a BinOp, except that it can be assiged.
	public static class AbstractIndexAccess extends SyntacticElement.Impl implements
			Expr, LVal {
		public Type nominalElementType;
		public Expr src;
		public Expr index;
	
		public AbstractIndexAccess(Expr src, Expr index, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
		
		public AbstractIndexAccess(Expr src, Expr index, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
				
		public Type nominalType() {
			return nominalElementType;
		}
		
		public Type rawType() {
			return null;
		}
		
		public String toString() {
			return src + "[" + index + "]";
		}
	}

	public static class ListAccess extends AbstractIndexAccess {					
		public Type.List rawSrcType;

		public ListAccess(Expr src, Expr index, Attribute... attributes) {
			super(src,index,attributes);
		}

		public ListAccess(Expr src, Expr index, Collection<Attribute> attributes) {
			super(src,index,attributes);			
		}

		public Type rawType() {
			return rawSrcType.element();			
		}
	}

	public static class DictionaryAccess extends AbstractIndexAccess {				
		public Type.Dictionary rawSrcType;

		public DictionaryAccess(Expr src, Expr index, Attribute... attributes) {
			super(src,index,attributes);
		}

		public DictionaryAccess(Expr src, Expr index, Collection<Attribute> attributes) {
			super(src,index,attributes);			
		}
		
		public Type rawType() {
			return rawSrcType.value();			
		}
	}
	
	public static class StringAccess extends AbstractIndexAccess {				
		public StringAccess(Expr src, Expr index, Attribute... attributes) {
			super(src,index,attributes);			
		}

		public StringAccess(Expr src, Expr index, Collection<Attribute> attributes) {
			super(src,index,attributes);			
		}

		public Type nominalType() {
			return Type.T_CHAR;
		}

		public Type rawType() {
			return Type.T_CHAR;			
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
		public Type nominalType;
		public Type rawType;
		
		public UnOp(UOp op, Expr mhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;			
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type rawType() {
			return rawType;
		}
		
		public String toString() {
			return op + mhs.toString();
		}
	}
	
	public static class Set extends SyntacticElement.Impl implements Expr {		
		public final ArrayList<Expr> arguments;
		public Type nominalType;
		public Type.Set rawType;
		
		public Set(Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.arguments = new ArrayList<Expr>(arguments);
		}
		
		public Set(Attribute attribute, Expr... arguments) {
			super(attribute);
			this.arguments = new ArrayList<Expr>();
			for(Expr a : arguments) {
				this.arguments.add(a);
			}
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type.Set rawType() {
			return rawType;
		}
	}
	
	public static class List extends SyntacticElement.Impl implements Expr {		
		public final ArrayList<Expr> arguments;
		public Type nominalType;
		public Type.List rawType;
		
		public List(Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.arguments = new ArrayList<Expr>(arguments);
		}
		
		public List(Attribute attribute, Expr... arguments) {
			super(attribute);
			this.arguments = new ArrayList<Expr>();
			for(Expr a : arguments) {
				this.arguments.add(a);
			}
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type.List rawType() {
			return rawType;
		}
	}
	
	public static class SubList extends SyntacticElement.Impl implements Expr {		
		public Expr src;
		public Expr start;
		public Expr end;
		public Type nominalType;
		public Type.List rawType;
		
		public SubList(Expr src, Expr start, Expr end, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.start = start;
			this.end = end;			
		}
		
		public SubList(Expr src, Expr start, Expr end, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.start = start;
			this.end = end;
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type.List rawType() {
			return rawType;
		}
	}
	
	public static class Comprehension extends SyntacticElement.Impl implements Expr {
		public final COp cop;
		public Expr value;
		public final ArrayList<Pair<String,Expr>> sources;
		public Expr condition;
		public Type nominalType;
		public Type rawType;
		
		public Comprehension(COp cop, Expr value,
				Collection<Pair<String, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.value = value;
			this.condition = condition;
			this.sources = new ArrayList<Pair<String, Expr>>(sources);
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type rawType() {
			return rawType;
		}
	}
	
	public enum COp {
		SETCOMP,
		LISTCOMP,
		NONE, // implies value == null					
		SOME, // implies value == null
	}
	
	public static class AbstractDotAccess extends SyntacticElement.Impl
			implements
				LVal {
		public Expr src;
		public final String name;

		public AbstractDotAccess(Expr lhs, String name, Attribute... attributes) {
			super(attributes);
			this.src = lhs;
			this.name = name;
		}

		public AbstractDotAccess(Expr lhs, String name,
				Collection<Attribute> attributes) {
			super(attributes);
			this.src = lhs;
			this.name = name;
		}
		
		public Type nominalType() {
			return null;
		}

		public Type rawType() {
			return null;
		}

		public String toString() {
			return src + "." + name;
		}
	}
	
	public static class RecordAccess extends AbstractDotAccess {
		public Type nominalFieldType;
		public Type.Record rawSrcType;

		public RecordAccess(Expr lhs, String name, Attribute... attributes) {
			super(lhs,name,attributes);			
		}
		
		public RecordAccess(Expr lhs, String name, Collection<Attribute> attributes) {
			super(lhs,name,attributes);			
		}
		
		public Type nominalType() {
			return nominalFieldType;
		}
		
		public Type rawType() {
			return rawSrcType.fields().get(name);
		}		
	}		
	
	// should extend abstract dot access?
	public static class ConstantAccess extends AbstractDotAccess {
		public final NameID nid;
		public Value value;

		public ConstantAccess(ModuleAccess src, String name, NameID nid,
				Attribute... attributes) {
			super(src, name, attributes);
			this.nid = nid;
		}

		public ConstantAccess(ModuleAccess src, String name, NameID nid,
				Collection<Attribute> attributes) {
			super(src, name, attributes);
			this.nid = nid;
		}
				
		public Type nominalType() {
			return Type.Nominal(nid);
		}
		
		public Type rawType() {
			return value.type();
		}
		
		public String toString() {
			if(src == null) {
				// root
				return name;
			} else {
				return src + "." + name;
			}
		}
	}		
	
	// should extend abstract dot access?
	public static class ModuleAccess extends AbstractDotAccess {
		public final ModuleID mid;

		public ModuleAccess(PackageAccess src, String name, ModuleID mid, Attribute... attributes) {
			super(src, name, attributes);
			this.mid = mid;
		}
		
		public ModuleAccess(PackageAccess src, String name, ModuleID mid, Collection<Attribute> attributes) {
			super(src, name, attributes);
			this.mid = mid;
		}
		
		public Type nominalType() {
			return null;
		}
		
		public Type rawType() {
			return null;
		}
		
		public String toString() {
			if(src == null) {
				// root
				return name;
			} else {
				return src + "." + name;
			}
		}
	}

	public static class PackageAccess extends AbstractDotAccess {
		public PkgID pid;

		public PackageAccess(PackageAccess src, String name, PkgID pid, Attribute... attributes) {
			super(src, name, attributes);
			this.pid = pid;
		}
		
		public PackageAccess(PackageAccess src, String name, PkgID pid, Collection<Attribute> attributes) {
			super(src, name, attributes);
			this.pid = pid;
		}			

		public Type nominalType() {
			return null;
		}
		
		public Type rawType() {
			return null;
		}
		
		public String toString() {
			if(src == null) {
				// package root
				return name;
			} else {
				return src + "." + name;
			}
		}
	}
	
	public static class ProcessAccess extends SyntacticElement.Impl implements Expr {
		public Expr src;	
		public Type nominalElementType;
		public Type.Process rawSrcType;
		
		public ProcessAccess(Expr src, Attribute... attributes) {
			super(attributes);
			this.src = src;			
		}
		
		public Type nominalType() {
			return nominalElementType;
		}
		
		public Type rawType() {
			return rawSrcType.element();
		}
				
		public String toString() {
			return "*" + src.toString();
		}
	}
		
	public static class Dictionary extends SyntacticElement.Impl implements Expr {
		public final ArrayList<Pair<Expr,Expr>> pairs;		
		public Type nominalType;
		public Type.Dictionary rawType;
		
		public Dictionary(Collection<Pair<Expr,Expr>> pairs, Attribute... attributes) {
			super(attributes);
			this.pairs = new ArrayList<Pair<Expr,Expr>>(pairs);
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type.Dictionary rawType() {
			return rawType;
		}
	}
	
	public static class Record extends SyntacticElement.Impl implements
			Expr {
		public final HashMap<String, Expr> fields;
		public Type nominalType;
		public Type.Record rawType;

		public Record(Map<String, Expr> fields,
				Attribute... attributes) {
			super(attributes);
			this.fields = new HashMap<String, Expr>(fields);
		}

		public Type nominalType() {
			return nominalType;
		}

		public Type.Record rawType() {
			return rawType;
		}
	}
	
	public static class Tuple extends SyntacticElement.Impl implements
			LVal {
		public final ArrayList<Expr> fields;
		public Type nominalType;
		public Type.Tuple rawType;
		
		public Tuple(Collection<Expr> fields, Attribute... attributes) {
			super(attributes);
			this.fields = new ArrayList<Expr>(fields);
		}

		public Type nominalType() {
			return nominalType;
		}

		public Type.Tuple rawType() {
			return rawType;
		}
	}
	
	public static class Invoke extends SyntacticElement.Impl implements Expr,
			Stmt {
		public final String name;
		public Expr receiver;
		public final ArrayList<Expr> arguments;
		public final boolean synchronous;
		public Type nominalReturnType;
		public Type.Function rawType;

		public Invoke(String name, Expr receiver, Collection<Expr> arguments,
				boolean synchronous, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.receiver = receiver;
			this.arguments = new ArrayList<Expr>(arguments);
			this.synchronous = synchronous;
		}
		
		public Type nominalType() {
			return nominalReturnType;
		}
		
		public Type rawType() {
			return rawType.ret();
		}
	}
	
	
	public static class AbstractLength extends SyntacticElement.Impl implements Expr {
		public Expr src;	
		
		public AbstractLength(Expr mhs, Attribute... attributes) {
			super(attributes);
			this.src = mhs;			
		}
		
		public AbstractLength(Expr mhs, Collection<Attribute> attributes) {
			super(attributes);
			this.src = mhs;			
		}
		
		public Type nominalType() {
			return Type.T_INT;
		}		
		
		public Type rawType() {
			return Type.T_INT;
		}
		
		public String toString() {
			return "|" + src.toString() + "|";
		}
	}
	
	public static class SetLength extends AbstractLength {
		public Type.Set rawSrcType;
		
		public SetLength(Expr src, Attribute... attributes) {
			super(src,attributes);
		}
		
		public SetLength(Expr src, Collection<Attribute> attributes) {
			super(src,attributes);
		}		
	}		
	
	public static class ListLength extends AbstractLength {		
		public Type.List rawSrcType;
		
		public ListLength(Expr src, Attribute... attributes) {
			super(src,attributes);
		}
		
		public ListLength(Expr src, Collection<Attribute> attributes) {
			super(src,attributes);
		}		
	}	
	
	public static class StringLength extends AbstractLength {		
		public StringLength(Expr src, Attribute... attributes) {
			super(src,attributes);
		}
		
		public StringLength(Expr src, Collection<Attribute> attributes) {
			super(src,attributes);
		}
	}	
	
	public static class DictionaryLength extends AbstractLength {		
		public Type.Dictionary rawSrcType;
		
		public DictionaryLength(Expr src, Attribute... attributes) {
			super(src,attributes);
		}
		
		public DictionaryLength(Expr src, Collection<Attribute> attributes) {
			super(src,attributes);
		}		
	}	
	
	public static class Spawn  extends SyntacticElement.Impl implements Expr,Stmt {
		public Expr expr;
		public Type nominalType;
		public Type.Process rawType;		

		public Spawn(Expr expr, Attribute... attributes) {
			this.expr = expr;						
		}
		
		public Type nominalType() {
			return nominalType;
		}
		
		public Type.Process rawType() {
			return rawType;
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
		LISTAPPEND{
			public String toString() { return "+"; }
		},
		STRINGAPPEND{
			public String toString() { return "+"; }
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
		SUBSET{
			public String toString() { return "<"; }
		},
		SUBSETEQ{
			public String toString() { return "<="; }
		},
		ELEMENTOF{
			public String toString() { return "in"; }
		},		
		RANGE{
			public String toString() { return ".."; }
		},
		TYPEEQ{
			public String toString() { return "~=="; }
		},
		TYPEIMPLIES {
			public String toString() { return "~=>"; }
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
