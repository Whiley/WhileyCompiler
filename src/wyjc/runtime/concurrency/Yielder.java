package wyjc.runtime.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A helper class for the actor hierarchy that involves the yielding of thread
 * control and saving of local state.
 * 
 * @author Timothy Jones
 */
public abstract class Yielder extends Resumer {

	private final Stack<State> state = new Stack<State>();
	private State current = null;

	private boolean yielded = false;

	public Yielder(Scheduler scheduler) {
		super(scheduler);
	}

	/**
	 * @return Whether the object is currently yielding or has yielded.
	 */
	public boolean isYielded() {
		return yielded;
	}

	/**
	 * Yields control of the thread, but does not push a new local state object
	 * onto the stack. Useful for when a message is the last action of a method,
	 * where yielding makes sense but saving the local state does not.
	 */
	public void yield() {
		if (yielded) {
			throw new IllegalStateException(
			    "Attempting to cleanly yield while object is already yielded.");
		}
		yielded = true;
	}

	/**
	 * Yields control of the thread and pushes a new local state object onto the
	 * stack. Calls to <code>push</code> and <code>set</code> after calling this
	 * method will attach to the pushed object for retrieval later with
	 * <code>unyield</code>.
	 * 
	 * @param location The location of the computation in the method.
	 */
	public void yield(int location) {
		state.push(current = new State(location));
		yielded = true;
	}

	/**
	 * Pops a local state object off of the stack, moving the <code>pop</code>
	 * and <code>get</code> methods onto the next locals.
	 * 
	 * @return The location marker of the popped locals object.
	 */
	public int unyield() {
		int location = state.pop().location;
		if (state.isEmpty()) {
			yielded = false;
		}
		return location;
	}
	
	public void set(int index, Object value) {
		current.localMap.set(index - 1, value);
	}
	
	public void set(int index, boolean value) {
		current.localMap.set(index - 1, new Boolean(value));
	}
	
	public void set(int index, int value) {
		current.localMap.set(index - 1, new Int(value));
	}
	
	public Object getObject(int index) {
		return current.localMap.get(index - 1);
	}
	
	public boolean getBoolean(int index) {
		return ((Boolean) current.localMap.get(index - 1)).value;
	}
	
	public int getInt(int index) {
		return ((Int) current.localMap.get(index - 1)).value;
	}

	public void push(Object value) {
		current.localStack.push(value);
	}

	public void push(boolean value) {
		current.localStack.push(new Boolean(value));
	}

	public void push(int value) {
		current.localStack.push(new Int(value));
	}

	public Object popObject() {
		return current.localStack.pop();
	}

	public boolean popBoolean() {
		return ((Boolean) current.localStack.pop()).value;
	}

	public int popInt() {
		return ((Int) current.localStack.pop()).value;
	}

	private static final class State {

		public final int location;

		public final List<Object> localMap = new ArrayList<Object>();
		public final Stack<Object> localStack = new Stack<Object>();

		public State(int location) {
			this.location = location;
		}

	}

	private static final class Boolean {

		public final boolean value;

		public Boolean(boolean value) {
			this.value = value;
		}

	}

	private static final class Int {

		public final int value;

		public Int(int value) {
			this.value = value;
		}

	}

	// TODO Finish primitive classes here.

}
