package wyautl.lang;

import static wyautl.lang.Automaton.State;
import wyautl.util.BinaryMatrix;

/**
 * <p>
 * This default interpretation of <i>subsumption</i> provides a useful starting
 * point for many interpretations. It follows a uniform <i>covariant</i>
 * protocol, as follows:
 * </p>
 * <ul>
 * <li>A state <code>s1</code> subsumes another state <code>s2</code> if they
 * have the same (sequential) kind and every child of <code>s1</code> at
 * position <code>i</code> subsumes the child of <code>s2</code> at that
 * position.</li>
 * <li>A state <code>s1</code> subsumes another state <code>s2</code> if they
 * have the same (non-sequential) kind and every child of <code>s2</code> is
 * subsumed by a child of <code>s1</code>.
 * </ul>
 * 
 * 
 * @author David J. Pearce
 * 
 */
public class DefaultSubsumption implements Relation {
	private final BinaryMatrix subsumes;
	private final Automaton from;
	private final Automaton to;
	
	public DefaultSubsumption(Automaton from, Automaton to) {
		this.from = from;
		this.to = to;
		this.subsumes = new BinaryMatrix(from.size(),to.size(),true);
	}
	
	public final Automaton from() {
		return from;
	}
	
	public final Automaton to() {
		return to;
	}
	
	public boolean update(int from, int to) {
		boolean ov = subsumes.get(from, to);
		boolean nv = isRelated(from, to);
		if(ov != nv) {
			subsumes.set(from,to,nv);
			return true; // changed
		} else {
			return false; // no change
		}
	}
	
	public boolean isRelated(int fromIndex, int toIndex) {
		State s1 = from.states[fromIndex];
		State s2 = to.states[toIndex];
		
		if(s1.kind == s2.kind && s1.deterministic == s2.deterministic) {
			boolean deterministic = s1.deterministic;
			
			if(deterministic) {
				int[] s1children = s1.children;
				int[] s2children = s2.children;
				
				if(s1children.length != s2children.length) {
					return false;
				}
				
				int length = s1children.length;
				
				for (int k = 0; k != length; ++k) {
					int s1child = s1children[k];
					int s2child = s2children[k];
					if (!subsumes.get(s1child, s2child)) {
						return false;
					}
				}
				
				return true;				
			} else {
				// non-deterministic (i.e. more expensive) case
				int[] s1children = s1.children;
				int[] s2children = s2.children;				
				int s1length = s1children.length;
				int s2length = s2children.length;
				
				// Check every node in s2 is subsumed by a node in s1
				for(int k=0;k!=s2length;++k) {
					int s2child = s2children[k];
					boolean matched = false;
					for (int l = 0; l != s1length; ++l) {
						int s1child = s1children[l];
						if (subsumes.get(s1child, s2child)) {
							matched = true;
							break;
						}
					}
					if(!matched) {
						return false;
					}
				}				
				return true;
			}
		}
		
		return false;
	}	
}
