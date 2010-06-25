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

import wyjc.*;
import wyjc.ast.UnresolvedWhileyFile;
import wyjc.ast.UnresolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.types.Types;

public class ExpandConstraints {
	public void expand(UnresolvedWhileyFile uwf) {
		for(Decl d : uwf.declarations()) {
			if(d instanceof FunDecl) {
				expand((FunDecl)d);
			}
		}
	}		
	
	public void expand(FunDecl fd) {
		
		Condition preCondition = fd.preCondition();
		for (FunDecl.Parameter p : fd.parameters()) {			
			ConstraintAttr ca = p.attribute(ConstraintAttr.class);
			if (ca != null && ca.constraint() != null) {
				Condition c = ca.constraint();				
				HashMap<String, Expr> binding = new HashMap<String, Expr>();
				binding.put("$", new Variable(p.name(), p
						.attribute(TypeAttr.class)));
				c = c.substitute(binding);
				if (preCondition == null) {
					preCondition = c;
				} else {
					preCondition = new And(preCondition, c, new TypeAttr(
							Types.T_BOOL));
				}
			}
		}
		
		FunDecl.Return r = fd.returnType();
		Condition postCondition = fd.postCondition();
		ConstraintAttr ca = r.attribute(ConstraintAttr.class);
		if(ca != null && ca.constraint() != null) {			
			if (postCondition != null) {
				postCondition = new And(preCondition, ca.constraint(),
						new TypeAttr(Types.T_BOOL));
			} else {
				postCondition = ca.constraint();
			}
		}
		
		fd.setPreCondition(preCondition);
		fd.setPostCondition(postCondition);
	}
}
