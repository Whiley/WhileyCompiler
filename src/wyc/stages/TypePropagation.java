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

package wyc.stages;

import static wyil.util.SyntaxError.*;
import static wyil.util.ErrorMessages.*;

import java.util.*;

import wyc.TypeExpander;
import wyc.lang.*;
import wyc.lang.WhileyFile.MethDecl;
import wyc.lang.WhileyFile.*;
import wyil.ModuleLoader;
import wyil.lang.Import;
import wyil.lang.PkgID;
import wyil.lang.Type;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import static wyil.util.SyntaxError.*;

/**
 * Propagates type information in a flow-sensitive fashion from declared
 * parameter and return types through assigned expressions, to determine types
 * for all intermediate expressions and variables. For example:
 * 
 * <pre>
 * int sum([int] data):
 *     r = 0          // infers int type for r, based on type of constant
 *     for v in data: // infers int type for v, based on type of data
 *         r = r + v  // infers int type for r, based on type of operands 
 *     return r       // infers int type for r, based on type of r after loop
 * </pre>
 * 
 * The flash points here are the variables <code>r</code> and <code>v</code> as
 * <i>they do not have declared types</i>. Type propagation is responsible for
 * determing their type.
 * 
 * Loops present an interesting challenge for type propagation. Consider this
 * example:
 * 
 * <pre>
 * real loopy(int max):
 *     i = 0
 *     while i < max:
 *         i = i + 0.5
 *     return i
 * </pre>
 * 
 * On the first pass through the loop, variable <code>i</code> is inferred to
 * have type <code>int</code> (based on the type of the constant <code>0</code>
 * ). However, the add expression is inferred to have type <code>real</code>
 * (based on the type of the rhs) and, hence, the resulting type inferred for
 * <code>i</code> is <code>real</code>. At this point, the loop must be
 * reconsidered taking into account this updated type for <code>i</code>.
 * 
 * In some cases, this process must update the underlying expressions to reflect
 * the correct operator. For example:
 * 
 * <pre>
 * {int} f({int} x, {int} y):
 *    return x+y
 * </pre>
 * 
 * Initially, the expression <code>x+y</code> is assumed to be arithmetic
 * addition. During type propagation, however, it becomes apparent that its
 * operands are both sets. Therefore, the underlying AST node is updated to
 * represent a set union.
 * 
 * <h3>References</h3>
 * <ul>
 * <li>
 * <p>
 * David J. Pearce and James Noble. Structural and Flow-Sensitive Types for
 * Whiley. Technical Report, Victoria University of Wellington, 2010.
 * </p>
 * </li>
 * </ul>
 * 
 * @author David J. Pearce
 * 
 */
public final class TypePropagation {
	private final ModuleLoader loader;
	private final TypeExpander expander;
	private String filename;
	
	public TypePropagation(ModuleLoader loader, TypeExpander expander) {
		this.loader = loader;
		this.expander = expander;
	}
	
	public void propagate(WhileyFile wf) {
		this.filename = wf.filename;
		
		for(WhileyFile.Decl decl : wf.declarations) {
			if(decl instanceof FunDecl) {
				propagate((FunDecl)decl);
			} else if(decl instanceof TypeDecl) {
				propagate((TypeDecl)decl);					
			} else if(decl instanceof ConstDecl) {
				propagate((ConstDecl)decl);					
			}			
		}
	}
	
	public void propagate(ConstDecl cd) {
		
	}
	
	public void propagate(TypeDecl td) {
		Attributes.Type attr = td.type.attribute(Attributes.Type.class);
		try {
			Type expanded = expander.expand(attr.type);
		} catch(ResolveError e) {
			// FIXME: this will report an error that is not very specific.			
			syntaxError(e.getMessage(),filename,td.type,e);
		}
	}

	public void propagate(FunDecl fd) {
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		for (WhileyFile.Parameter p : fd.parameters) {			
			Attributes.Type attr = p.type.attribute(Attributes.Type.class);
			environment.put(p.name,attr.type);
		}
		
		if(fd instanceof MethDecl) {
			MethDecl md = (MethDecl) fd;
			Attributes.Type attr = md.receiver.attribute(Attributes.Type.class);
			environment.put("this",attr.type);
		}
		
		propagate(fd.statements,environment);
	}
	
	private void propagate(ArrayList<Stmt> body,
			HashMap<String, Type> environment) {

	}
	
	private <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError(errorMessage(SUBTYPE_ERROR, clazz.getName(), t),
					filename, elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	private void checkIsSubtype(Type t1, Type t2, SyntacticElement elem) {
		if (!Type.isImplicitCoerciveSubtype(t1, t2)) {			
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2), filename, elem);
		}
	}	
}
