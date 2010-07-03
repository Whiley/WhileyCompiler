// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.ast;

import java.util.*;


import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.ast.types.unresolved.UnresolvedType;
import wyjc.util.ModuleID;

/**
 * An unresolved whiley file represents a source file which has not yet been
 * resolved. In particule, this means that it contains:
 * <ol>
 * <li>Constant definitions which have not been reduced to values.</li>
 * <li>Type definitions which have unresolved types</li>
 * <li>Function declarations which have unresolved types</li>
 * </ol>
 * 
 * @author djp
 * 
 */

public class UnresolvedWhileyFile {
	private ModuleID identifier;
	private String filename;	
	private ArrayList<Decl> decls;

	public UnresolvedWhileyFile(ModuleID id, String filename, Collection<Decl> declarations) {
		this.identifier = id;
		this.filename = filename;
		this.decls = new ArrayList<Decl>(declarations);
	}
	
	public ModuleID id() {
		return identifier;
	}
	
	public String filename() {
		return filename;
	}
	
	public List<Decl> declarations() {
		return decls;
	}
	
	public SkeletonInfo skeletonInfo() {		
		return new SkeletonInfo(identifier) {
			public boolean hasName(String name) {
				// FIXME: improve performance!
				for(Decl d : decls) {
					if(d.name().equals(name)) {
						return true;
					}
				}
				return false;
			}
			
			public UnresolvedType unresolvedType(String name) {
				// FIXME: improve performance!
				for(Decl d : decls) {
					if(d.name().equals(name) && d instanceof TypeDecl) {
						TypeDecl td = (TypeDecl) d;
						return td.type();
					}
				}
				return null;
			}
		};
	}
	
	public static interface Decl extends TemplatedWhileyFile.Decl<UnresolvedType> {}
	
	public static class ImportDecl extends SyntacticElementImpl implements Decl {
		public ArrayList<String> pkg;
		
		public ImportDecl(List<String> pkg, Attribute... attributes) {
			super(attributes);
			this.pkg = new ArrayList<String>(pkg);
		}
		
		public ImportDecl(List<String> pkg, Collection<Attribute> attributes) {
			super(attributes);
			this.pkg = new ArrayList<String>(pkg);
		}
		
		public String name() {
			return "";
		}
		
		public List<String> pkg() {
			return Collections.unmodifiableList(pkg);
		}
	}
	
	public static class ConstDecl extends TemplatedWhileyFile.ConstDecl<UnresolvedType, Expr>  implements Decl{
		public ConstDecl(List<Modifier> modifiers, Expr value, String name, Attribute... attributes) { 
			super(modifiers,value,name,attributes);			
		}
		public ConstDecl(List<Modifier> modifiers, Expr value, String name, List<Attribute> attributes) { 
			super(modifiers,value,name,attributes);			
		}
		public void setConstant(Expr value) {
			this.value = value;  
		}
	}
	
	public static class TypeDecl extends TemplatedWhileyFile.TypeDecl<UnresolvedType> implements Decl {
		public TypeDecl(List<Modifier> modifiers, UnresolvedType type, Condition constraint, String name, Attribute... attributes) { 
			super(modifiers, type, constraint, name, attributes);			
		}
		public TypeDecl(List<Modifier> modifiers, UnresolvedType type, Condition constraint, String name, List<Attribute> attributes) { 
			super(modifiers,type, constraint, name, attributes);			
		}
	}
	
	public static class FunDecl
			extends
				TemplatedWhileyFile.FunDecl<UnresolvedType, FunDecl.Receiver, FunDecl.Parameter, FunDecl.Return>
			implements
				Decl {
		public FunDecl(List<Modifier> modifiers, String name, Receiver receiver, Return returnType,
				List<Parameter> parameters, Condition pre, Condition post,
				List<Stmt> statements, Attribute... attributes) {
			super(modifiers,name, receiver, returnType, parameters, pre, post,
					statements, attributes);
		}

		public void setPreCondition(Condition pre) {
			this.precondition = pre;
		}

		public void setPostCondition(Condition post) {
			this.postcondition = post;
		}

		public static class Return
				extends
					TemplatedWhileyFile.Return<UnresolvedType> {
			public Return(UnresolvedType t, Attribute... attributes) {
				super(t, attributes);
			}
		}

		public static class Receiver
		extends
		TemplatedWhileyFile.Receiver<UnresolvedType> {
			public Receiver(UnresolvedType t, Attribute... attributes) {
				super(t, attributes);
			}
		}
		
		public static class Parameter
				extends
					TemplatedWhileyFile.Parameter<UnresolvedType> {
			public Parameter(UnresolvedType t, String name,
					Attribute... attributes) {
				super(t, name, attributes);
			}
		}
	}		
}
