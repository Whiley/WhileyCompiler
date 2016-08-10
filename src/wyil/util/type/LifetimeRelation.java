package wyil.util.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * This relation tracks the partial order among lifetimes.
 */
public class LifetimeRelation {

	/**
	 * These are all lifetime parameters to the current method. There is no
	 * order between lifetimes in this set.
	 */
	private final Set<String> parameters;

	/**
	 * These are lifetimes declared by named blocks. These lifetimes are totally
	 * ordered according to their position in the stack. Each lifetime in
	 * {@link #parameters} outlives all block lifetimes.
	 */
	private final Stack<String> blocks;

	/**
	 * Create a new and empty lifetime relation.
	 */
	public LifetimeRelation() {
		this.parameters = new HashSet<String>();
		this.blocks = new Stack<String>();
	}

	/**
	 * Create an independent copy of the given lifetime relation.
	 *
	 * @param lifetimeRelation
	 */
	public LifetimeRelation(LifetimeRelation lifetimeRelation) {
		this.parameters = new HashSet<String>(lifetimeRelation.parameters);
		this.blocks = new Stack<String>();
		this.blocks.addAll(lifetimeRelation.blocks);
	}

	/**
	 * Check whether the first (outer) lifetime outlives the second (inner)
	 * lifetime.
	 *
	 * @param outerLifetime
	 * @param innerLifetime
	 * @return
	 */
	public boolean outlives(String outerLifetime, String innerLifetime) {
		if (outerLifetime.equals("*")) {
			// * outlives everything
			return true;
		}

		if (outerLifetime.equals(innerLifetime)) {
			return true;
		}

		// Check whether the inner lifetime is from a named block.
		int innerIndex = this.blocks.indexOf(innerLifetime);
		if (innerIndex != -1) {
			// All parameters are ordered before blocks.
			if (this.parameters.contains(outerLifetime)) {
				return true;
			}

			// Maybe another block at lower stack position?
			int outerIndex = this.blocks.indexOf(outerLifetime);
			if (outerIndex != -1 && outerIndex < innerIndex) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Add a method's lifetime parameters to this relation.
	 *
	 * @param lifetimeParameters
	 */
	public void addParameters(Collection<String> lifetimeParameters) {
		this.parameters.addAll(lifetimeParameters);
	}

	/**
	 * Enter a named block to this relation.
	 *
	 * @param lifetime
	 */
	public void startNamedBlock(String lifetime) {
		this.blocks.push(lifetime);
	}

	/**
	 * Remove the named block with the given name from this relation. It also
	 * removes inner blocks in case that the given block is not the latest one.
	 *
	 * @param lifetime
	 */
	public void endNamedBlock(String lifetime) {
		int i = this.blocks.lastIndexOf(lifetime);
		if (i != -1) {
			this.blocks.subList(i, this.blocks.size()).clear();
		}
	}

	/**
	 * Replace this lifetime relation with the merge result of the given two
	 * relations.
	 *
	 * @param first
	 * @param second
	 */
	public void replaceWithMerge(LifetimeRelation first, LifetimeRelation second) {
		this.parameters.clear();
		this.blocks.clear();

		this.parameters.addAll(first.parameters);
		this.parameters.retainAll(second.parameters);
		Iterator<String> it1 = first.blocks.iterator();
		Iterator<String> it2 = second.blocks.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			String b1 = it1.next();
			String b2 = it2.next();
			if (b1.equals(b2)) {
				this.blocks.push(b1);
			} else {
				break;
			}
		}
	}

	public final static LifetimeRelation EMPTY = new LifetimeRelation();
}
