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

import wyc.NameResolver;
import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;

/**
 * Provides classes representing the various kinds of declaration found in a
 * Whiley source file. This includes <i>type declarations</i>, <i>method
 * declarations</i>, <i>constant declarations</i>, etc. In essence, a
 * <code>WhileyFile</code> forms the root of the Abstract Syntax Tree.
 * 
 * @author djp
 * 
 */
public final class WhileyFile {
	public final ModuleID module;
	public final String filename;
	public final ArrayList<Decl> declarations;
	
	public WhileyFile(ModuleID module, String filename, List<Decl> decls) {
		this.module = module;
		this.filename = filename;
		this.declarations = new ArrayList<Decl>(decls);
	}
	
	public boolean hasName(String name) {
		return declaration(name) != null;
	}			

	public Decl declaration(String name) {
		for(Decl d : declarations) {
			if(d.name().equals(name)) {
				return d;
			}
		}
		return null;
	}
	
	public <T> List<T> declarations(Class<T> c) {
		ArrayList<T> r = new ArrayList<T>();
		for(Decl d : declarations) {
			if(c.isInstance(d)) {
				r.add((T)d);
			}			
		}
		return r;
	}
	
	public <T> List<T> declarations(Class<T> c, String name) {
		ArrayList<T> r = new ArrayList<T>();
		for(Decl d : declarations) {
			if (d.name().equals(name) && c.isInstance(d)) {
				r.add((T) d);
			}		
		}
		return r;
	}
	
	public TypeDecl typeDecl(String name) {
		for (Decl d : declarations) {
			if (d instanceof TypeDecl && d.name().equals(name)) {
				return (TypeDecl) d;
			}
		}
		return null;
	}
	
	public interface Decl extends SyntacticElement {
		public String name();
	}

	/**
	 * Represents an import declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * import Console from whiley.lang.System
	 * </pre>
	 * 
	 * Here, the package is <code>whiley.lang</code>, the module is
	 * <code>System</code> and the name is <code>Console</code>.
	 * 
	 * @author djp
	 * 
	 */
	public static class ImportDecl extends SyntacticElement.Impl implements Decl {
		public ArrayList<String> pkg;
		public String module;
		public String name;
		
		public ImportDecl(List<String> pkg, String module, String name, Attribute... attributes) {
			super(attributes);
			this.pkg = new ArrayList<String>(pkg);
			this.module = module;
			this.name = name;
		}
	
		public String name() {
			return "";
		}		
	}

	/**
	 * Represents a constant declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * define PI as 3.14159
	 * </pre>
	 * 
	 * Constant declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * 
	 * @author djp
	 * 
	 */
	public static class ConstDecl extends
				SyntacticElement.Impl implements Decl {
		
		public final List<Modifier> modifiers;
		public final Expr constant;
		public final String name;
		public Value value;

		public ConstDecl(List<Modifier> modifiers, Expr constant, String name,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.constant = constant;
			this.name = name;
		}

		public String name() {
			return name;
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String toString() {
			return "define " + constant + " as " + name;
		}
	}

	/**
	 * Represents a type declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * define nat as int where $ >= 0
	 * </pre>
	 * 
	 * Here, the newly defined type is <code>nat</code> whose values are all
	 * non-negative integers. Type declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * 
	 * @author djp
	 * 
	 */	
	public static class TypeDecl extends SyntacticElement.Impl implements Decl {
		public final List<Modifier> modifiers;
		public final UnresolvedType unresolvedType;		
		public final Expr constraint;
		public final String name;
		public Type nominalType;
		public Type rawType;

		public TypeDecl(List<Modifier> modifiers, UnresolvedType type, String name,
				Expr constraint, Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.unresolvedType = type;
			this.name = name;			
			this.constraint = constraint;			
		}		
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String name() { return name; }		
		
		public String toString() {
			if(constraint != null) {
				return "define " + unresolvedType + " as " + name + " where " + constraint;
			} else {
				return "define " + unresolvedType + " as " + name;
			}
		}
	}

	/**
	 * Represents a function declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * int f(int x) requires x > 0, ensures $ < 0:
	 *    return -x
	 * </pre>
	 * 
	 * <p>
	 * Here, a function <code>f</code> is defined which accepts only positive
	 * integers and returns only negative integers. The special variable
	 * <code>$</code> is used to refer to the return value. Functions in Whiley
	 * may not have side-effects (i.e. they are <code>pure functions</code>).
	 * </p>
	 * 
	 * <p>
	 * Function declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * </p>
	 * 
	 * @author djp
	 * 
	 */
	public static class FunDecl extends SyntacticElement.Impl implements
			Decl {
		public final ArrayList<Modifier> modifiers;
		public final String name;		
		public final UnresolvedType ret;
		public final UnresolvedType throwType;
		public final ArrayList<Parameter> parameters;
		public final Expr precondition;
		public final Expr postcondition;		
		public final ArrayList<Stmt> statements;
		public Type.Function nominalType;
		public Type.Function rawType;

		/**
		 * Construct an object representing a Whiley function.
		 * 
		 * @param name
		 *            - The name of the function.
		 * @param returnType
		 *            - The return type of this method
		 * @param paramTypes
		 *            - The list of parameter names and their types for this
		 *            method
		 * @param precondition
		 *            - The constraint which must hold true on entry and exit
		 *            (maybe null)
		 * @param statements
		 *            - The Statements making up the function body.
		 */
		public FunDecl(List<Modifier> modifiers, String name,
				UnresolvedType ret, List<Parameter> parameters,
				Expr precondition, Expr postcondition,
				UnresolvedType throwType, List<Stmt> statements,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = new ArrayList<Modifier>(modifiers);
			this.name = name;			
			this.ret = ret;
			this.parameters = new ArrayList<Parameter>(parameters);
			this.precondition = precondition;
			this.postcondition = postcondition;
			this.statements = new ArrayList<Stmt>(statements);
			this.throwType = throwType;
		}

		public boolean isPublic() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}

		public String name() {
			return name;
		}
	}

	/**
	 * Represents a method declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * int MyData::m(int x):
	 *    return this.x + x
	 * </pre>
	 * 
	 * <p>
	 * Here, a method <code>m</code> is defined for the process type
	 * <code>MyData</code>, which is referred to as the <i>receiver</i>. The
	 * special variable <code>this</code> is used to access fields within the
	 * process type. Methods are distinct from functions in Whiley as they may
	 * have side-effects. This includes reading/writing I/O and modifying the
	 * state of their receiver. Methods may also be <i>headless</i>, meaning
	 * they are not attached to any specific receiver.
	 * </p>
	 * 
	 * <p>
	 * Method declarations may also have modifiers, such as <code>public</code>
	 * and <code>private</code>.
	 * </p>
	 * 
	 * @author djp
	 * 
	 */
	public final static class MethDecl extends FunDecl implements Decl {
		public final UnresolvedType receiver;
		
		public MethDecl(List<Modifier> modifiers, String name,
				UnresolvedType receiver, UnresolvedType ret,
				List<Parameter> parameters, Expr precondition,
				Expr postcondition, UnresolvedType throwType, 
				List<Stmt> statements,
				Attribute... attributes) {
			super(modifiers,name,ret,parameters,precondition,postcondition,throwType,statements,attributes);
			this.receiver = receiver;
		}
	}

	/**
	 * Represents a parameter declaration as part of a function or method
	 * declaration. The primary purpose of this is to retain the source-code
	 * location of the parameter in case any syntax error needs to be reported
	 * on it.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Parameter extends SyntacticElement.Impl implements Decl {
		public final UnresolvedType type;
		public final String name;

		public Parameter(UnresolvedType type, String name, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}
		
		public String name() {
			return name;
		}
	}	
}
