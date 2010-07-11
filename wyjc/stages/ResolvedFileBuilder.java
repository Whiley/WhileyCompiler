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

package wyjc.stages;

import java.util.*;

import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.UnresolvedWhileyFile;
import wyjc.ast.attrs.TypeAttr;
import wyjc.ast.exprs.*;
import wyjc.ast.types.*;

public class ResolvedFileBuilder {

	public ResolvedWhileyFile build(UnresolvedWhileyFile uwf) {
		ArrayList<ResolvedWhileyFile.Decl> resolvedDecls = new ArrayList();
		for(UnresolvedWhileyFile.Decl d : uwf.declarations()) {
			ResolvedWhileyFile.Decl nd = build(d);
			if(nd != null) {
				resolvedDecls.add(nd);
			}
		}
		return new ResolvedWhileyFile(uwf.id(),uwf.filename(),resolvedDecls);
	}
	
	protected ResolvedWhileyFile.Decl build(UnresolvedWhileyFile.Decl d) {
		if(d instanceof UnresolvedWhileyFile.ImportDecl) {
			return null;
		} else if(d instanceof UnresolvedWhileyFile.ConstDecl) {
			return build((UnresolvedWhileyFile.ConstDecl)d);
		} else if(d instanceof UnresolvedWhileyFile.TypeDecl) {
			return build((UnresolvedWhileyFile.TypeDecl)d);
		} else {			
			return build((UnresolvedWhileyFile.FunDecl)d);
		}
	}
	
	protected ResolvedWhileyFile.ConstDecl build(UnresolvedWhileyFile.ConstDecl cd) {
		Value v = (Value) cd.constant();
		return new ResolvedWhileyFile.ConstDecl(cd.modifiers(), v, cd.name(),
				cd.attributes());
	}
	
	protected ResolvedWhileyFile.TypeDecl build(UnresolvedWhileyFile.TypeDecl cd) {
		Type t = (Type) cd.attribute(TypeAttr.class).type();		
		return new ResolvedWhileyFile.TypeDecl(cd.modifiers(), t, cd.name(),
				cd.attributes());
	}
	
	protected ResolvedWhileyFile.FunDecl build(UnresolvedWhileyFile.FunDecl cd) {
		ArrayList<ResolvedWhileyFile.FunDecl.Parameter> params = new ArrayList();				
		
		for (UnresolvedWhileyFile.FunDecl.Parameter p : cd.parameters()) {			
			params.add(new ResolvedWhileyFile.FunDecl.Parameter((Type) p
					.attribute(TypeAttr.class).type(), p.name(), p
					.attributes()));
		}
		
		ResolvedWhileyFile.FunDecl.Return ret = new ResolvedWhileyFile.FunDecl.Return(
				(Type) cd.returnType().attribute(TypeAttr.class).type(), cd.returnType().attributes());
		
		ResolvedWhileyFile.FunDecl.Receiver rec = null;
		
		if(cd.receiver() != null) {
			rec = new ResolvedWhileyFile.FunDecl.Receiver(cd
					.receiver().attribute(TypeAttr.class).type(), cd
					.returnType().attributes());
		}
		
		return new ResolvedWhileyFile.FunDecl(cd.modifiers(),cd.name(), rec, ret, params, cd
				.preCondition(), cd.postCondition(), cd.statements(), cd
				.attributes());
	}	
}
