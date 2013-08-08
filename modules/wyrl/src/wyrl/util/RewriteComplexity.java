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

package wyrl.util;

import java.util.Map;

import wyrl.core.*;

/**
 * Provides various algorithms for statically computing the complexity of a
 * given rewrite rule. That is, the lower- and upper-bounds on the reduction
 * caused by a given rewrite. For example, consider this simple rewrite:
 * 
 * <pre>
 * reduce Not(Not(BExpr e)):
 *     => e
 * </pre>
 * 
 * This has a reduction complexity of 2 since it is guaranteed to eliminate
 * exactly two nodes.. Now, consider a variant:
 * 
 * <pre>
 * reduce Not(Bool b):
 *     => False, if b == True
 * </pre>
 * 
 * This has a reduction complexity of O(1) since it will eliminate one node
 * overall if it applies successfully, but this is not guaranteed because of the
 * conditional.
 * 
 * @author David J. Pearce
 * 
 */
public class RewriteComplexity {
	
	/**
	 * Determine the guaranteed minimum size of the automaton when a given
	 * pattern matches. This is useful for statically judging how a given
	 * rewrite rule will affect an automaton. Specifically, if the difference
	 * between the minimum size of the pattern and expression it is rewritten to
	 * is negative, then the automaton is guaranteed to reduce in size after a
	 * successful application.
	 * 
	 * @param pattern
	 *            The pattern being examined.
	 * @param bindings
	 *            Variables encountered in the pattern will be bound to their
	 *            specific size values during this calculation.
	 * @return
	 */
	public static Polynomial lowerBound(Pattern pattern, Map<String,Polynomial> bindings) {
		if(pattern instanceof Pattern.Leaf) {
			Pattern.Leaf leaf = (Pattern.Leaf) pattern;
			return lowerBound(leaf.type);
		} else if(pattern instanceof Pattern.Term) {
			Pattern.Term term = (Pattern.Term) pattern;
			if(term.data == null) {
				return Polynomial.ONE;
			} else {
				return lowerBound(term.data,bindings).add(Polynomial.ONE);
			}
		} else {
			Pattern.Collection collection = (Pattern.Collection) pattern;
			int minSize = collection.elements.length;
			if(collection.unbounded) {
				// In the case of an unbounded pattern, then the last element
				// represents the multi-match. When computing the minimum size
				// of a pattern we simply assume this is zero.
				minSize = minSize - 1;
			}
			Polynomial result = Polynomial.ONE;
			for(int i=0;i!=minSize;++i) {
				Pair<Pattern,String> p = collection.elements[i];
				Polynomial p_poly = lowerBound(p.first(),bindings);
				if(p.second() != null) {
					bindings.put(p.second(),p_poly);
				}
				result = result.add(p_poly);
			}
			return result;
		}
	}
	
	public static Polynomial lowerBound(Type type) {
		// TODO: this is rubbish
		return Polynomial.ZERO;
	}
}
