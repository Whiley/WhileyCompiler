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

import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wyc.builder.Nominal;
import wyil.lang.*;
import wyil.util.Pair;

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
		public final Value value;

		public Constant(Value val, Attribute... attributes) {
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

	public static class Convert extends SyntacticElement.Impl implements Expr {
		public final UnresolvedType unresolvedType;
		public Nominal type;					
		public Expr expr;	
		
		public Convert(UnresolvedType type, Expr expr, Attribute... attributes) {
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
		public final UnresolvedType unresolvedType;
		public Nominal type;		
		
		public TypeVal(UnresolvedType val, Attribute... attributes) {
			super(attributes);
			this.unresolvedType = val;
		}
		
		public Nominal result() {
			return Nominal.T_META;
		}		
	}
	
	public static class AbstractFunctionOrMethodOrMessage extends SyntacticElement.Impl implements Expr {
		public final String name;
		public final ArrayList<UnresolvedType> paramTypes;
		public Nominal.FunctionOrMethodOrMessage type;		
				
		public AbstractFunctionOrMethodOrMessage(String name, Collection<UnresolvedType> paramTypes, Attribute... attributes) {
			super(attributes);
			this.name = name;			
			if(paramTypes != null) {
				this.paramTypes = new ArrayList<UnresolvedType>(paramTypes);
			} else {
				this.paramTypes = null;
			}
		}
		
		public AbstractFunctionOrMethodOrMessage(String name,
				Collection<UnresolvedType> paramTypes,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			if(paramTypes != null) {
				this.paramTypes = new ArrayList<UnresolvedType>(paramTypes);
			} else {
				this.paramTypes = null;
			}
		}
		
		public Nominal.FunctionOrMethodOrMessage result() {
			return type;
		}		
	}
	
	public static class FunctionOrMethodOrMessage extends AbstractFunctionOrMethodOrMessage {
		public final NameID nid;					
		
		public FunctionOrMethodOrMessage(NameID nid, Collection<UnresolvedType> paramTypes,
				Attribute... attributes) {
			super(nid.name(), paramTypes, attributes);
			this.nid = nid;
		}
		
		public FunctionOrMethodOrMessage(NameID nid, Collection<UnresolvedType> paramTypes,
				Collection<Attribute> attributes) {
			super(nid.name(), paramTypes, attributes);
			this.nid = nid;
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
			case ELEMENTOF:
			case SUBSET:	
			case SUBSETEQ:
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
		public Nominal.EffectiveMap srcType;
	
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
	
	public static class Set extends SyntacticElement.Impl implements Expr {		
		public final ArrayList<Expr> arguments;
		public Nominal.Set type;		
		
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
		
		public Nominal.Set result() {
			return type;
		}		
	}
	
	public static class List extends SyntacticElement.Impl implements Expr {		
		public final ArrayList<Expr> arguments;
		public Nominal.List type;		
		
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
		
		public Nominal.List result() {
			return type;
		}		
	}
	
	public static class SubList extends SyntacticElement.Impl implements Expr {		
		public Expr src;
		public Expr start;
		public Expr end;	
		public Nominal.EffectiveList type;
		
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
		
		public Nominal result() {
			return (Nominal) type;
		}		
	}
	
	public static class SubString extends SyntacticElement.Impl implements Expr {		
		public Expr src;
		public Expr start;
		public Expr end;	
		
		public SubString(Expr src, Expr start, Expr end, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.start = start;
			this.end = end;			
		}
		
		public SubString(Expr src, Expr start, Expr end, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.start = start;
			this.end = end;
		}
		
		public Nominal result() {
			return Nominal.T_STRING;
		}		
	}
	
	public static class Comprehension extends SyntacticElement.Impl implements Expr {
		public final COp cop;
		public Expr value;
		public final ArrayList<Pair<String,Expr>> sources;
		public Expr condition;
		public Nominal type;
		
		public Comprehension(COp cop, Expr value,
				Collection<Pair<String, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.value = value;
			this.condition = condition;
			this.sources = new ArrayList<Pair<String, Expr>>(sources);
		}
		
		public Nominal result() {
			return type;
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
		
		public Nominal result() {
			return null;
		}

		public String toString() {
			return src + "." + name;
		}
	}
	
	public static class RecordAccess extends AbstractDotAccess {		
		public Nominal.EffectiveRecord srcType;

		public RecordAccess(Expr lhs, String name, Attribute... attributes) {
			super(lhs,name,attributes);			
		}
		
		public RecordAccess(Expr lhs, String name, Collection<Attribute> attributes) {
			super(lhs,name,attributes);			
		}
		
		public Nominal result() {
			return srcType.field(name);
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
				
		public Nominal result() {
			// FIXME: loss of nominal information here, since the type of the
			// constant in question is always fully expanded.
			return Nominal.construct(value.type(), value.type());
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
	
	public static class ModuleAccess extends AbstractDotAccess {
		public final Path.ID mid;

		public ModuleAccess(PackageAccess src, String name, Path.ID mid, Attribute... attributes) {
			super(src, name, attributes);
			this.mid = mid;
		}
		
		public ModuleAccess(PackageAccess src, String name, Path.ID mid, Collection<Attribute> attributes) {
			super(src, name, attributes);
			this.mid = mid;
		}
		
		public Nominal result() {
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
		public Path.ID pid;

		public PackageAccess(PackageAccess src, String name, Path.ID pid, Attribute... attributes) {
			super(src, name, attributes);
			this.pid = pid;
		}
		
		public PackageAccess(PackageAccess src, String name, Path.ID pid, Collection<Attribute> attributes) {
			super(src, name, attributes);
			this.pid = pid;
		}			

		public Nominal result() {
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
		
	public static class Dictionary extends SyntacticElement.Impl implements Expr {
		public final ArrayList<Pair<Expr,Expr>> pairs;		
		public Nominal.Dictionary type;		
		
		public Dictionary(Collection<Pair<Expr,Expr>> pairs, Attribute... attributes) {
			super(attributes);
			this.pairs = new ArrayList<Pair<Expr,Expr>>(pairs);
		}
		
		public Nominal.Dictionary result() {
			return type;
		}		
	}
	
	public static class Record extends SyntacticElement.Impl implements
			Expr {
		public final HashMap<String, Expr> fields;
		public Nominal.Record type;		

		public Record(Map<String, Expr> fields,
				Attribute... attributes) {
			super(attributes);
			this.fields = new HashMap<String, Expr>(fields);
		}

		public Nominal.Record result() {
			return type;
		}
	}
	
	public static class Tuple extends SyntacticElement.Impl implements
			LVal {
		public final ArrayList<Expr> fields;
		public Nominal.Tuple type;		
		
		public Tuple(Collection<Expr> fields, Attribute... attributes) {
			super(attributes);
			this.fields = new ArrayList<Expr>(fields);
		}

		public Nominal.Tuple result() {
			return type;
		}
	}
	
	public static class AbstractInvoke<R extends Expr> extends SyntacticElement.Impl implements Expr,
			Stmt {
		public final String name;
		public R qualification;
		public final ArrayList<Expr> arguments;		
		public final boolean synchronous;		
		
		public AbstractInvoke(String name, R receiver,
				Collection<Expr> arguments, boolean synchronous,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.qualification = receiver;
			this.arguments = new ArrayList<Expr>(arguments);	
			this.synchronous = synchronous;
		}
		
		public AbstractInvoke(String name, R receiver,
				Collection<Expr> arguments, boolean synchronous,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.qualification = receiver;
			this.arguments = new ArrayList<Expr>(arguments);
			this.synchronous = synchronous;
		}
		
		public Nominal result() {
			return null;
		}
	}
	
	public static class MessageSend extends AbstractInvoke<Expr> {		
		public final NameID nid;
		public Nominal.Message messageType;
		
		public MessageSend(NameID nid, Expr receiver,
				Collection<Expr> arguments, boolean synchronous,
				Attribute... attributes) {
			super(nid.name(), receiver, arguments, synchronous, attributes);
			this.nid = nid;			
		}

		public MessageSend(NameID nid, Expr receiver,
				Collection<Expr> arguments, boolean synchronous,
				Collection<Attribute> attributes) {
			super(nid.name(), receiver, arguments, synchronous, attributes);
			this.nid = nid;
		}
		
		public NameID nid() {
			return nid;
		}		
		
		public Nominal result() {
			return messageType.ret();
		}
	}
	
	public static class MethodCall extends AbstractInvoke<ModuleAccess> {		
		public final NameID nid;
		public Nominal.Method methodType;
		
		public MethodCall(NameID nid, ModuleAccess qualification, Collection<Expr> arguments,
				Attribute... attributes) {
			super(nid.name(),qualification,arguments,false,attributes);
			this.nid = nid;
		}
		
		public MethodCall(NameID nid, ModuleAccess qualification, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(nid.name(),qualification,arguments,false,attributes);
			this.nid = nid;			
		}
		
		public NameID nid() {
			return nid;
		}
		
		public Nominal result() {
			return methodType.ret();
		}
	}
	
	public static class FunctionCall extends AbstractInvoke<ModuleAccess> {		
		public final NameID nid;
		public Nominal.Function functionType;
		
		public FunctionCall(NameID nid, ModuleAccess qualification, Collection<Expr> arguments,
				Attribute... attributes) {
			super(nid.name(),qualification,arguments,false,attributes);
			this.nid = nid;
		}
		
		public FunctionCall(NameID nid, ModuleAccess qualification, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(nid.name(),qualification,arguments,false,attributes);
			this.nid = nid;			
		}
		
		public NameID nid() {
			return nid;
		}
		
		public Nominal result() {
			return functionType.ret();
		}
	}
	
	public static class AbstractIndirectInvoke extends SyntacticElement.Impl implements Expr,
	Stmt {
		public Expr src; 
		public final ArrayList<Expr> arguments;		
		public final boolean synchronous;				

		public AbstractIndirectInvoke(Expr src,
				Collection<Expr> arguments, boolean synchronous,
				Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.arguments = new ArrayList<Expr>(arguments);	
			this.synchronous = synchronous;
		}

		public AbstractIndirectInvoke(Expr src,
				Collection<Expr> arguments, boolean synchronous,
				Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.arguments = new ArrayList<Expr>(arguments);
			this.synchronous = synchronous;
		}

		public Nominal result() {
			return null;
		}				
	}
	
	public static class IndirectMethodCall extends AbstractIndirectInvoke {				
		public Nominal.Method methodType;
		
		public IndirectMethodCall(Expr src, Collection<Expr> arguments,
				Attribute... attributes) {
			super(src,arguments,true,attributes);
		}
		
		public IndirectMethodCall(Expr src, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(src,arguments,true,attributes);
		}
		
		public Nominal result() {
			return methodType.ret();
		}
	}
	
	public static class IndirectFunctionCall extends AbstractIndirectInvoke {				
		public Nominal.Function functionType;
		
		public IndirectFunctionCall(Expr src, Collection<Expr> arguments,
				Attribute... attributes) {
			super(src,arguments,true,attributes);
		}
		
		public IndirectFunctionCall(Expr src, Collection<Expr> arguments,
				Collection<Attribute> attributes) {
			super(src,arguments,true,attributes);
		}
		
		public Nominal result() {
			return functionType.ret();
		}
	}	
	
	public static class IndirectMessageSend extends AbstractIndirectInvoke {				
		public final Expr receiver;
		public Nominal.Message messageType;		
		
		public IndirectMessageSend(Expr src, Expr receiver,
				Collection<Expr> arguments, boolean synchronous,
				Attribute... attributes) {
			super(src, arguments, synchronous, attributes);
			this.receiver = receiver;
		}
		
		public IndirectMessageSend(Expr src, Expr receiver,
				Collection<Expr> arguments, boolean synchronous,
				Collection<Attribute> attributes) {
			super(src, arguments, synchronous, attributes);
			this.receiver = receiver;
		}
		
		public Nominal result() {
			return messageType.ret();
		}
	}
	
	public static class LengthOf extends SyntacticElement.Impl implements Expr {
		public Expr src;
		public Nominal.EffectiveCollection srcType;
		
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
