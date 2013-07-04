package wyrl.util;

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

	/**
	 * Temporary state used during acceptance
	 */
	private int count;

	/**
	 * Temporary state used during acceptance.
	 */
	private final Object[] state;

	public AbstractRewriteRule(Pattern pattern, Schema schema) {
		this.schema = schema;
		this.pattern = pattern;
		this.state = new Object[pattern.declarations().size()+1];
	}

	public final Activation probe(Automaton automaton, int root) {
		Activation result;

		state[count++] = root;
		
		// First, we check whether or not this pattern accepts the given
		// automaton state. In the case that it does, we build an appropriate
		// activation record.
		if (accepts(pattern, automaton, root)) {
			result = new Activation(this, null, Arrays.copyOf(state,
					state.length));
		} else {
			result = null;
		}

		// Clear the state array. Strictly speaking, this is not necessary.
		// But, it potentially helps reduce memory usage.
		while (count > 0) { state[--count] = null; }

		return result;
	}

	private final boolean accepts(Pattern p, Automaton automaton, int root) {
		if (p instanceof Pattern.Leaf) {
			Pattern.Leaf leaf = (Pattern.Leaf) p;
			return Runtime.accepts(leaf.type, automaton, automaton.get(root),
					schema);			
		} else if (p instanceof Pattern.Term) {
			return accepts((Pattern.Term) p, automaton, root);
		} else if (p instanceof Pattern.Set) {
			return accepts((Pattern.Set) p, automaton, root);
		} else if (p instanceof Pattern.Bag) {
			return accepts((Pattern.Bag) p, automaton, root);
		} else {
			return accepts((Pattern.List) p, automaton, root);
		}
	}

	private final boolean accepts(Pattern.Term p, Automaton automaton, int root) {
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
			} else if (accepts(p.data, automaton, t.contents)) {
				if(p.variable != null) {
					// At this point, we need to store the root of the match as this
					// will feed into the activation record.
					this.state[count++] = root;
				}
				return true;
			}
		}
		return false;
	}

	private final boolean accepts(Pattern.BagOrSet p, Automaton automaton,
			int root) {
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
				if (accepts(pItem_first, automaton, aItem)) {
					matched.set(j, true);
					found = true;
					if(pItem_second != null) {
						this.state[count++] = aItem;
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
				if (!accepts(pItem_first, automaton, aItem)) {
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
					this.state[count++] = new Automaton.Set(children);
				} else {
					this.state[count++] = new Automaton.Bag(children);
				}
			}
		}
		
		// If we get here, then we're done.
		
		return true;
	}

	private final boolean accepts(Pattern.List p, Automaton automaton, int root) {
		int startCount = count;
		Automaton.Collection c = (Automaton.Collection) automaton.get(root);
		Pair<Pattern,String>[] p_elements = p.elements;
		int minSize = p.unbounded ? p_elements.length - 1 : p_elements.length;
		
		// First, check size of collection		
		if(!p.unbounded && c.size() != minSize || c.size() < minSize) {
			return false;
		}
		
		// Second, we need to try and match the elements.
		for (int i = 0; i != minSize; ++i) {
			Pair<Pattern,String> pItem = p_elements[i];
			Pattern pItem_first = pItem.first();
			String pItem_second = pItem.second();
			int aItem = c.get(i);
			if (!accepts(pItem_first, automaton, aItem)) {
				count = startCount; // reset
				return false;
			} else if (pItem_second != null) {
				this.state[count++] = aItem;
			}			
		}
		
		// Third, in the case of an unbounded match we check the remainder.
		if (p.unbounded) {
			Pair<Pattern, String> pItem = p_elements[minSize];
			Pattern pItem_first = pItem.first();
			String pItem_second = pItem.second();
			for (int i = minSize; i != c.size(); ++i) {				
				int aItem = c.get(i);
				if (!accepts(pItem_first, automaton, aItem)) {
					count = startCount; // reset
					return false;
				}
			}
			if (pItem_second != null) {		
				int[] children = new int[c.size() - minSize];
				for (int i = minSize; i != c.size(); ++i) {					
					children[i] = c.get(i);
				}
				this.state[count++] = new Automaton.List(children);
			}
		}
		
		return true;
	}
}
