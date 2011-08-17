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
import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;

public class WhileyFile {
	public final ModuleID module;
	public final String filename;
	public final ArrayList<Decl> declarations;
	
	public WhileyFile(ModuleID module, String filename, List<Decl> decls) {
		this.module = module;
		this.filename = filename;
		this.declarations = new ArrayList<Decl>(decls);
	}
	
	public ModuleLoader.Skeleton skeleton() {		
		return new ModuleLoader.Skeleton(module) {
			public boolean hasName(String name) {
				// FIXME: improve performance!
				for(Decl d : declarations) {
					if(d.name().equals(name)) {
						return true;
					}
				}
				return false;
			}			
		};
	}

	
	public interface Decl extends SyntacticElement {
		public String name();
	}
	
	public static class ImportDecl extends SyntacticElement.Impl implements Decl {
		public ArrayList<String> pkg;
		
		public ImportDecl(List<String> pkg, Attribute... attributes) {
			super(attributes);
			this.pkg = new ArrayList<String>(pkg);
		}
	
		public String name() {
			return "";
		}		
	}

	public static class ConstDecl extends
				SyntacticElement.Impl implements Decl {
		
		public final List<Modifier> modifiers;
		public final Expr constant;
		public final String name;

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
	
	public static class TypeDecl extends SyntacticElement.Impl implements Decl {
		public final List<Modifier> modifiers;
		public final UnresolvedType type;
		public final Expr constraint;
		public final String name;

		public TypeDecl(List<Modifier> modifiers, UnresolvedType type, String name,
				Expr constraint, Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.type = type;
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
				return "define " + type + " as " + name + " where " + constraint;
			} else {
				return "define " + type + " as " + name;
			}
		}
	}

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
