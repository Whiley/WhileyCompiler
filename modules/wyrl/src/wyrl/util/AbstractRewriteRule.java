package wyrl.util;

import java.util.Arrays;
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
				// At this point, we need to store the root of the match as this
				// will feed into the activation record.
				this.state[count++] = root;
				return true;
			}
		}
		return false;
	}

	private final boolean accepts(Pattern.BagOrSet p, Automaton automaton,
			int root) {
		// FIXME: need to implement this!
		return false;
	}

	private final boolean accepts(Pattern.List p, Automaton automaton, int root) {
		// FIXME: need to implement this!
		return false;
	}
}
