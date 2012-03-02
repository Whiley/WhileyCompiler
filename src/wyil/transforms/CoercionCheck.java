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

package wyil.transforms;

import java.util.*;

import static wybs.lang.SyntaxError.syntaxError;
import static wyil.util.ErrorMessages.*;
import wybs.lang.Builder;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wyil.*;
import wyil.lang.*;
import wyil.util.*;

/**
 * <p>
 * The point of the coercion check is to check that all convert bytecodes make
 * sense, and are not ambiguous. For example, consider the following code:
 * </p>
 * 
 * <pre>
 * define Rec1 as { real x, int y }
 * define Rec2 as { int x, real y }
 * define uRec1Rec2 as Rec1 | Rec2
 * 
 * int f(uRec1Rec2 r):
 *  if r is Rec1:
 *      return r.y
 *  else:
 *      return r.x
 * 
 * int g():
 *  rec = { x: 1, y: 1}
 *  return f(rec)
 * </pre>
 * 
 * <p>
 * An implicit coercion will be inserted just before the last statement in
 * <code>g()</code>. This will be:
 * </p>
 * 
 * <pre>
 * convert {int x,int y} => {real x,int y}|{int x,real y}
 * </pre>
 * <p>
 * However, this conversion is <i>ambiguous</i> because we could convert the
 * left-hand side to either of the two options in the right-hand side.
 * </p>
 * 
 * @author David J. Pearce
 */
public class CoercionCheck implements Transform {
	private String filename;

	public CoercionCheck(Builder builder) {

	}
	
	public void apply(WyilFile module) {
		filename = module.filename();
		
		for(WyilFile.Method method : module.methods()) {
			check(method);
		}
	}
		
	public void check(WyilFile.Method method) {				
		for (WyilFile.Case c : method.cases()) {
			check(c.body(), method);
		}		
	}
	
	protected void check(Block block,  WyilFile.Method method) {		
		for (int i = 0; i != block.size(); ++i) {
			Block.Entry stmt = block.get(i);
			Code code = stmt.code;
			if (code instanceof Code.Convert) {				
				Code.Convert conv = (Code.Convert) code; 				
				check(conv.from,conv.to,new HashSet<Pair<Type,Type>>(),stmt);
			} 
		}	
	}

	/**
	 * Recursively check that there is no ambiguity in coercing type from into
	 * type to. The visited set is necessary to ensure this process terminates
	 * in the presence of recursive types.
	 * 
	 * @param from
	 * @param to
	 * @param visited - the set of pairs already checked.
	 * @param elem - enclosing syntactic element.
	 */
	protected void check(Type from, Type to, HashSet<Pair<Type, Type>> visited,
			SyntacticElement elem) {
		Pair<Type,Type> p = new Pair<Type,Type>(from,to);
		if(visited.contains(p)) {
			return; // already checked this pair
		} else {
			visited.add(p);
		}
		if(from == Type.T_VOID) {
			// also no problem
		} else if(from instanceof Type.Leaf && to instanceof Type.Leaf) {
			// no problem
		} else if(from instanceof Type.Tuple && to instanceof Type.Tuple) {
			Type.Tuple t1 = (Type.Tuple) from;
			Type.Tuple t2 = (Type.Tuple) to;
			List<Type> t1_elements = t1.elements(); 
			List<Type> t2_elements = t2.elements();
			for(int i=0;i!=t2.elements().size();++i) {
				Type e1 = t1_elements.get(i);
				Type e2 = t2_elements.get(i);
				check(e1,e2,visited,elem);
			}
		} else if(from instanceof Type.Reference && to instanceof Type.Reference) {
			Type.Reference t1 = (Type.Reference) from;
			Type.Reference t2 = (Type.Reference) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Type.Set && to instanceof Type.Set) {
			Type.Set t1 = (Type.Set) from;
			Type.Set t2 = (Type.Set) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Type.Dictionary && to instanceof Type.Set) {
			Type.Dictionary t1 = (Type.Dictionary) from;
			Type.Set t2 = (Type.Set) to;
			Type tup = Type.Tuple(t1.key(),t1.value());
			check(tup,t2.element(),visited,elem);
		} else if(from instanceof Type.List && to instanceof Type.Set) {
			Type.List t1 = (Type.List) from;
			Type.Set t2 = (Type.Set) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Type.List && to instanceof Type.Dictionary) {
			Type.List t1 = (Type.List) from;
			Type.Dictionary t2 = (Type.Dictionary) to;
			check(t1.element(),t2.value(),visited,elem);
		} else if(from instanceof Type.List && to instanceof Type.List) {
			Type.List t1 = (Type.List) from;
			Type.List t2 = (Type.List) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Type.Strung && to instanceof Type.List) {			
			Type.List t2 = (Type.List) to;
			check(Type.T_CHAR,t2.element(),visited,elem);
		} else if(from instanceof Type.Record && to instanceof Type.Record) {
			Type.Record t1 = (Type.Record) from;
			Type.Record t2 = (Type.Record) to;
			HashMap<String,Type> t1_elements = t1.fields(); 
			HashMap<String,Type> t2_elements = t2.fields();
			ArrayList<String> fields = new ArrayList<String>(t2.keys());
			for(String s : fields) {
				Type e1 = t1_elements.get(s);
				Type e2 = t2_elements.get(s);
				check(e1,e2,visited,elem);
			}			
		} else if(from instanceof Type.Function && to instanceof Type.Function) {
			Type.Function t1 = (Type.Function) from;
			Type.Function t2 = (Type.Function) to;
			List<Type> t1_elements = t1.params(); 
			List<Type> t2_elements = t2.params();			
			for(int i=0;i!=t1_elements.size();++i) {
				Type e1 = t1_elements.get(i);
				Type e2 = t2_elements.get(i);
				check(e1,e2,visited,elem);
			}			
			check(t1.ret(),t2.ret(),visited,elem);
		} else if(from instanceof Type.Union) {
			Type.Union t1 = (Type.Union) from; 
			for(Type b : t1.bounds()) {
				check(b,to,visited,elem);
			}
		} else if(to instanceof Type.Union) {			
			Type.Union t2 = (Type.Union) to;			
			
			// First, check for identical type (i.e. no coercion necessary)
			
			for(Type b : t2.bounds()) {
				if(from.equals(b)) {
					// no problem
					return;
				}
			}
			
			// Second, check for single non-coercive match
			Type match = null;			
			
			for(Type b : t2.bounds()) {
				if(Type.isSubtype(b,from)) {
					if(match != null) {
						// found ambiguity						
						syntaxError(errorMessage(AMBIGUOUS_COERCION,from,to), filename, elem);
					} else {
						check(from,b,visited,elem);
						match = b;						
					}
				}
			}
			
			if(match != null) {
				// ok, we have a hit on a non-coercive subtype.
				return;
			}
			
			// Third, test for single coercive match
			
			for(Type b : t2.bounds()) {
				if(Type.isImplicitCoerciveSubtype(b,from)) {
					if(match != null) {
						// found ambiguity
						syntaxError("ambiguous coercion (" + from + " => "
								+ to, filename, elem);
					} else {
						check(from,b,visited,elem);
						match = b;						
					}
				}
			}
		}		
	}
}
