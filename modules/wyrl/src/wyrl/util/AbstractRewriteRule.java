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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import wyautl.core.*;
import wyautl.rw.Activation;
import wyautl.rw.RewriteRule;
import wyrl.core.Pattern;

public abstract class AbstractRewriteRule implements RewriteRule {

	/**
	 * The pattern that this rewrite rule will match against.
	 */
	private final Pattern pattern;

	/**
	 * The schema describes the automata that this rule will operate over.
	 */
	private final Schema schema;
	
	private final int nPatternDeclarations; 

	/**
	 * Temporary state used during acceptance
	 */
	private int count;
	
	public AbstractRewriteRule(Pattern pattern, Schema schema) {
		this.schema = schema;
		this.pattern = pattern;
		// NOTE: +1 to include the root variable.
		nPatternDeclarations = pattern.declarations().size() + 1;
	}

	public final List<Activation> probe(Automaton automaton, int root) {
		count = 0;
		Object[] initialState = new Object[nPatternDeclarations];
		initialState[count++] = root;
		
		ArrayList states = new ArrayList();		
		states.add(initialState);
		
		
		// First, we check whether or not this pattern accepts the given
		// automaton state. In the case that it does, we build an appropriate
		// activation record.
		if (accepts(pattern, automaton, root, states)) {
			for(int i=0;i!=states.size();++i) {
				Object[] state = (Object[]) states.get(i);
				states.set(i,new Activation(this,null,state));
			}		
		} else {
			states = null;
		}
		
		return states;
	}

	private final boolean accepts(Pattern p, Automaton automaton, int root,
			ArrayList<Object[]> states) {
		if (p instanceof Pattern.Leaf) {
			Pattern.Leaf leaf = (Pattern.Leaf) p;
			return Runtime.accepts(leaf.type, automaton, automaton.get(root),
					schema);			
		} else if (p instanceof Pattern.Term) {
			return accepts((Pattern.Term) p, automaton, root, states);
		} else if (p instanceof Pattern.Set) {
			return accepts((Pattern.Set) p, automaton, root, states);
		} else if (p instanceof Pattern.Bag) {
			return accepts((Pattern.Bag) p, automaton, root, states);
		} else {
			return accepts((Pattern.List) p, automaton, root, states);
		}
	}

	private final boolean accepts(Pattern.Term p, Automaton automaton, int root,
			ArrayList<Object[]> states) {
		Automaton.State state = automaton.get(root);
		if (state instanceof Automaton.Term) {
			Automaton.Term t = (Automaton.Term) state;
			String actualName = schema.get(t.kind).name;
			// Check term names match
			if (!p.name.equals(actualName)) {
				return false;
			}
			// Check contents matches
			if (p.data == null) {
				return t.contents == Automaton.K_VOID;
			} else if (accepts(p.data, automaton, t.contents, states)) {
				if(p.variable != null) {
					// At this point, we need to store the root of the match as this
					// will feed into the activation record.
					assign(count++,t.contents,states);
				}
				return true;
			}
		}
		return false;
	}

	private final boolean accepts(Pattern.BagOrSet p, Automaton automaton,
			int root, ArrayList<Object[]> states) {
		int startCount = count;
		Automaton.State state = automaton.get(root);
		
		if (p instanceof Pattern.Set && !(state instanceof Automaton.Set)) {
			return false;
		} else if (p instanceof Pattern.Bag && !(state instanceof Automaton.Bag)) {
			return false;
		} 
		
		Automaton.Collection c = (Automaton.Collection) state;
		Pair<Pattern,String>[] p_elements = p.elements;
		int minSize = p.unbounded ? p_elements.length - 1 : p_elements.length;
		
		// First, check size of collection		
		if(!p.unbounded && c.size() != minSize || c.size() < minSize) {
			return false;
		}
		
		// FIXME: is there a bug here because of the ordering I go through the
		// loop means we don't try all combinations?
		BitSet matched = new BitSet();
		
		// Second, we need to try and match the fixed elements (but not the
		// unbound elements yet).		
		for (int i = 0; i != minSize; ++i) {
			Pair<Pattern,String> pItem = p_elements[i];
			Pattern pItem_first = pItem.first();
			String pItem_second = pItem.second();
			boolean found = false;
			for (int j = 0; j != c.size(); ++j) {
				if (matched.get(j)) {
					continue;
				}
				int aItem = c.get(j);
				if (accepts(pItem_first, automaton, aItem, states)) {
					matched.set(j, true);
					found = true;
					if(pItem_second != null) {
						assign(count++,aItem,states);
					}
					break;
				}
			}
			if (!found) {
				count = startCount; // reset
				return false;
			} 
		}
		
		// Third, in the case of an unbounded match we check the remainder.
		if (p.unbounded) {
			Pair<Pattern, String> pItem = p_elements[minSize];
			Pattern pItem_first = pItem.first();
			String pItem_second = pItem.second();
			for (int j = 0; j != c.size(); ++j) {
				if (matched.get(j)) { continue; }
				int aItem = c.get(j);
				if (!accepts(pItem_first, automaton, aItem, states)) {
					count = startCount; // reset
					return false;
				}
			}
			if (pItem_second != null) {					
				int[] children = new int[c.size() - minSize];
				for (int i = 0,j = 0; i != c.size(); ++i) {
					if (matched.get(i)) {
						continue;
					}
					children[j++] = c.get(i);
				}
				if(state instanceof Automaton.Set) {
					assign(count++,new Automaton.Set(children),states);
				} else {
					assign(count++,new Automaton.Bag(children),states);
				}
			}
		}
		
		// If we get here, then we're done.
		
		return true;
	}

	private final boolean nonDeterministicAccept(Automaton.Collection c,
			Pair<Pattern, String>[] elements, int elementIndex, BitSet matched,
			Automaton automaton, ArrayList<Object[]> states) {
		if(elementIndex == elements.length) {
			// matching complete.  
			return true;
		} else {
			boolean found = false;
			Pair<Pattern,String> pItem = elements[elementIndex];
			Pattern pItem_first = pItem.first();
			String pItem_second = pItem.second();
			
			for (int i = 0; i != c.size(); ++i) {
				if(matched.get(i)) { continue; }
				int aItem = c.get(i);
				if (accepts(pItem_first, automaton, aItem, states)) {
					matched.set(i, true);
					if(nonDeterministicAccept(c,elements,elementIndex+1,matched,automaton,states)) {
						found = true;
						if(pItem_second != null) {
							
							// FIXME: broken here
							got here
							
							assign(count++,aItem,states);
						}
					}	
					matched.set(i,false);
				}		
			}
			
			return found;
		}
	}
	
	private final boolean accepts(Pattern.List p, Automaton automaton,
			int root, ArrayList<Object[]> states) {
		int startCount = count;
		Automaton.Collection c = (Automaton.Collection) automaton.get(root);
		Pair<Pattern, String>[] p_elements = p.elements;
		int minSize = p.unbounded ? p_elements.length - 1 : p_elements.length;

		// First, check size of collection
		if (!p.unbounded && c.size() != minSize || c.size() < minSize) {
			return false;
		}

		// Second, we need to try and match the elements.
		for (int i = 0; i != minSize; ++i) {
			Pair<Pattern, String> pItem = p_elements[i];
			Pattern pItem_first = pItem.first();
			String pItem_second = pItem.second();
			int aItem = c.get(i);
			if (!accepts(pItem_first, automaton, aItem, states)) {
				count = startCount; // reset
				return false;
			} else if (pItem_second != null) {
				assign(count++, aItem, states);
			}
		}

		// Third, in the case of an unbounded match we check the remainder.
		if (p.unbounded) {
			Pair<Pattern, String> pItem = p_elements[minSize];
			Pattern pItem_first = pItem.first();
			String pItem_second = pItem.second();
			for (int i = minSize; i != c.size(); ++i) {
				int aItem = c.get(i);
				if (!accepts(pItem_first, automaton, aItem, states)) {
					count = startCount; // reset
					return false;
				}
			}
			if (pItem_second != null) {
				int[] children = new int[c.size() - minSize];
				for (int i = minSize; i != c.size(); ++i) {
					children[i] = c.get(i);
				}
				assign(count++, new Automaton.List(children), states);
			}
		}

		return true;
	}
	
	public static final void assign(int state, Object value, ArrayList<Object[]> states) {
		
	}
}
