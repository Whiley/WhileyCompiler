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
import wyjc.ast.exprs.Condition;
import wyjc.ast.exprs.Value;
import wyjc.ast.stmts.Stmt;
import wyjc.ast.types.*;
import wyjc.util.ModuleID;

public class ResolvedWhileyFile {
	private ModuleID identifier;
	private String filename;
	private ArrayList<Decl> decls;

	public ResolvedWhileyFile(ModuleID id, String filename,
			Collection<Decl> declarations) {
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

	public ModuleInfo moduleInfo() {
		HashMap<String,ModuleInfo.TypeDef> types = new HashMap<String,ModuleInfo.TypeDef>();
		HashMap<String,ModuleInfo.Const> constants = new HashMap<String,ModuleInfo.Const>();
		HashMap<String,List<ModuleInfo.Method>> methods = new HashMap<String,List<ModuleInfo.Method>>();
		
		for(Decl d : decls) {
			if(d instanceof TypeDecl) {
				TypeDecl td = (TypeDecl) d;
				types.put(td.name(), new ModuleInfo.TypeDef(td.name(),td.type()));
			} else if(d instanceof ConstDecl) {
				ConstDecl cd = (ConstDecl) d;
				constants.put(cd.name(), new ModuleInfo.Const(cd.name(),cd.constant()));
			} else if(d instanceof FunDecl) {
				FunDecl fd = (FunDecl) d;
				FunDecl.Receiver rec = fd.receiver();
				Type recType = rec == null ? null : rec.type(); 
				ModuleInfo.Method method = new ModuleInfo.Method(recType, fd
						.name(), fd.type(), fd.parameterNames(), fd
						.preCondition(), fd.postCondition());
				List<ModuleInfo.Method> ms = methods.get(fd.name());
				if(ms == null) {
					ms = new ArrayList<ModuleInfo.Method>();
					methods.put(fd.name(), ms);
				}
				ms.add(method);
			}
		}
		
		return new ModuleInfo(identifier,methods,types,constants);
	}
	
	public static interface Decl extends TemplatedWhileyFile.Decl<Type> {
	}

	public static class ConstDecl
			extends
				TemplatedWhileyFile.ConstDecl<Type, Value> implements Decl {
		public ConstDecl(List<Modifier> modifiers, Value value, String name, Attribute... attributes) {
			super(modifiers, value, name, attributes);
		}
		public ConstDecl(List<Modifier> modifiers, Value value, String name, Collection<Attribute> attributes) {
			super(modifiers, value, name, attributes);
		}
	}

	public static class TypeDecl extends TemplatedWhileyFile.TypeDecl<Type>
			implements
				Decl {
		public TypeDecl(List<Modifier> modifiers, Type type, String name,
				Attribute... attributes) {
			super(modifiers,type, name, attributes);
		}
		public TypeDecl(List<Modifier> modifiers, Type type, String name,
				Collection<Attribute> attributes) {
			super(modifiers,type, name, attributes);
		}		
	}

	public static class FunDecl
			extends
				TemplatedWhileyFile.FunDecl<Type, FunDecl.Receiver, FunDecl.Parameter, FunDecl.Return>
			implements
				Decl {
		
		public FunDecl(List<Modifier> modifiers, String name, Receiver receiver, Return returnType,
				List<Parameter> parameters, Condition pre, Condition post,
				List<Stmt> statements, Attribute... attributes) {
			super(modifiers,name, receiver, returnType, parameters, pre, post, statements,
					attributes);
		}

		public FunDecl(List<Modifier> modifiers, String name, Receiver receiver, Return returnType,
				List<Parameter> parameters, Condition pre, Condition post,
				List<Stmt> statements, Collection<Attribute> attributes) {
			super(modifiers,name, receiver, returnType, parameters, pre, post, statements,
					attributes);
		}
				
		public FunType type() {
			ArrayList<Type> ps = new ArrayList<Type>();
			for (Parameter p : parameters()) {
				ps.add(p.type());
			}
			// FIXME: this will need to be fixed at some point.
			return new FunType(returnType().type(), ps, null);
		}
		
		public static class Return extends TemplatedWhileyFile.Return<Type> {
			public Return(Type t, Attribute... attributes) {
				super(t, attributes);
			}
			public Return(Type t, Collection<Attribute> attributes) {
				super(t, attributes);
			}
		}	
		
		public static class Receiver extends TemplatedWhileyFile.Receiver<Type> {
			public Receiver(Type t, Attribute... attributes) {
				super(t, attributes);
			}
			public Receiver(Type t, Collection<Attribute> attributes) {
				super(t, attributes);
			}						
		}	
		
		public static class Parameter
				extends
					TemplatedWhileyFile.Parameter<Type> {
			
			public Parameter(Type t, String name, Attribute... attributes) {
				super(t, name, attributes);				
			}
			
			public Parameter(Type t, String name, Collection<Attribute> attributes) {
				super(t, name, attributes);				
			}			
		}
	}
}
