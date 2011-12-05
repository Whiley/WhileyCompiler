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
	 * An LVal is a special form of expression which may appear on the left-hand
	 * side of an assignment.
	 * 
	 * @author djp
	 * 
	 */
	public interface LVal extends Expr {}
	
	public static class UnknownVariable extends SyntacticElement.Impl implements Expr, LVal {
		public final String var;

		public UnknownVariable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}

		public String toString() {
			return var;
		}
	}
	
	public static class LocalVariable extends SyntacticElement.Impl implements
			Expr, LVal {
		public final String var;

		public LocalVariable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}

		public LocalVariable(String var, Collection<Attribute> attributes) {
			super(attributes);
			this.var = var;
		}
		
		public String toString() {
			return var;
		}
	}
		
	public static class ExternalAccess extends SyntacticElement.Impl implements Expr {
		public final NameID nid;

		public ExternalAccess(NameID mid, Attribute... attributes) {
			super(attributes);
			this.nid = mid;
		}
		
		public ExternalAccess(NameID mid, Collection<Attribute> attributes) {
			super(attributes);
			this.nid = mid;
		}
		
		public String toString() {
			return nid.toString();
		}
	}
	
	public static class ModuleAccess extends SyntacticElement.Impl implements Expr {
		public final ModuleID mid;

		public ModuleAccess(ModuleID mid, Attribute... attributes) {
			super(attributes);
			this.mid = mid;
		}
		
		public ModuleAccess(ModuleID mid, Collection<Attribute> attributes) {
			super(attributes);
			this.mid = mid;
		}
		
		public String toString() {
			return mid.toString();
		}
	}
	
	public static class PackageAccess extends SyntacticElement.Impl implements Expr {
		public PkgID pid;

		public PackageAccess(PkgID mid, Attribute... attributes) {
			super(attributes);
			this.pid = mid;
		}
		
		public PackageAccess(PkgID mid, Collection<Attribute> attributes) {
			super(attributes);
			this.pid = mid;
		}
		
		public String toString() {
			return pid.toString();
		}
	}
	
	public static class Constant extends SyntacticElement.Impl implements Expr {
		public final Value value;

		public Constant(Value val, Attribute... attributes) {
			super(attributes);
			this.value = val;
		}
		
		public String toString() {
			return value.toString();
		}
	}

	public static class Convert extends SyntacticElement.Impl implements Expr {
		public final UnresolvedType type;
		public Expr expr;

		public Convert(UnresolvedType type, Expr expr, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.expr = expr;
		}
		
		public String toString() {
			return "(" + type.toString() + ") " + expr;
		}
	}
	
	public static class TypeConst extends SyntacticElement.Impl implements Expr {
		public final UnresolvedType type;

		public TypeConst(UnresolvedType val, Attribute... attributes) {
			super(attributes);
			this.type = val;
		}
	}
	
	public static class FunConst extends SyntacticElement.Impl implements Expr {

		public String name;
		public final List<UnresolvedType> paramTypes;

		public FunConst(String name, List<UnresolvedType> paramTypes, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.paramTypes = paramTypes;
		}
	}
	
	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public final BOp op;
		public Expr lhs;
		public Expr rhs;
		
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
		
		public String toString() {
			return "(" + op + " " + lhs + " " + rhs + ")";
		}
	}

	// A list access is very similar to a BinOp, except that it can be assiged.
	public static class ListAccess extends SyntacticElement.Impl implements
			Expr, LVal {		
		public Expr src;
		public Expr index;
		
		public ListAccess(Expr src, Expr index, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
		
		public ListAccess(Expr src, Expr index, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
					
		public String toString() {
			return src + "[" + index + "]";
		}
	}



	public enum UOp {
		NOT,
		NEG,
		INVERT,
		LENGTHOF,		
		PROCESSACCESS,
		PROCESSSPAWN
	}
	
	public static class UnOp extends SyntacticElement.Impl implements Expr {
		public final UOp op;
		public Expr mhs;		
		
		public UnOp(UOp op, Expr mhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;			
		}
		
		public String toString() {
			return op + mhs.toString();
		}
	}
	
	public static class NaryOp extends SyntacticElement.Impl implements Expr {
		public final NOp nop;
		public final ArrayList<Expr> arguments;
		public NaryOp(NOp nop, Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.nop = nop;
			this.arguments = new ArrayList<Expr>(arguments);
		}
		public NaryOp(NOp nop, Attribute attribute, Expr... arguments) {
			super(attribute);
			this.nop = nop;
			this.arguments = new ArrayList<Expr>();
			for(Expr a : arguments) {
				this.arguments.add(a);
			}
		}
	}
	
	public enum NOp {
		SETGEN,
		LISTGEN,
		SUBLIST					
	}
	
	public static class Comprehension extends SyntacticElement.Impl implements Expr {
		public final COp cop;
		public Expr value;
		public final ArrayList<Pair<String,Expr>> sources;
		public Expr condition;
		
		public Comprehension(COp cop, Expr value,
				Collection<Pair<String, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.value = value;
			this.condition = condition;
			this.sources = new ArrayList<Pair<String, Expr>>(sources);
		}
	}
	
	public enum COp {
		SETCOMP,
		LISTCOMP,
		NONE, // implies value == null					
		SOME, // implies value == null
	}
	
	public static class RecordAccess extends SyntacticElement.Impl implements
			LVal {
		public Expr lhs;
		public final String name;

		public RecordAccess(Expr lhs, String name, Attribute... attributes) {
			super(attributes);
			this.lhs = lhs;
			this.name = name;
		}
		
		public String toString() {
			return lhs + "." + name;
		}
	}		

	public static class DictionaryGen extends SyntacticElement.Impl implements Expr {
		public final ArrayList<Pair<Expr,Expr>> pairs;		
		
		public DictionaryGen(Collection<Pair<Expr,Expr>> pairs, Attribute... attributes) {
			super(attributes);
			this.pairs = new ArrayList<Pair<Expr,Expr>>(pairs);
		}
	}
	
	public static class RecordGen extends SyntacticElement.Impl implements Expr {
		public final HashMap<String,Expr> fields;		
		
		public RecordGen(Map<String, Expr> fields, Attribute... attributes) {
			super(attributes);
			this.fields = new HashMap<String, Expr>(fields);
		}
	}
	
	public static class TupleGen extends SyntacticElement.Impl implements LVal {
		public final ArrayList<Expr> fields;		
		
		public TupleGen(Collection<Expr> fields, Attribute... attributes) {
			super(attributes);
			this.fields = new ArrayList<Expr>(fields);
		}
	}
	
	public static class Invoke extends SyntacticElement.Impl implements Expr,
			Stmt {
		public final String name;
		public Expr receiver;
		public final List<Expr> arguments;
		public final boolean synchronous;

		public Invoke(String name, Expr receiver, List<Expr> arguments,
				boolean synchronous, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.receiver = receiver;
			this.arguments = arguments;
			this.synchronous = synchronous;
		}
	}
	
	public static class Spawn extends UnOp implements Stmt {		
		public Spawn(Expr mhs, Attribute... attributes) {
			super(UOp.PROCESSSPAWN,mhs,attributes);							
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
