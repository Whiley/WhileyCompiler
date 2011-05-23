package wyjc.runtime.concurrency;

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
	
	public boolean isYielded() {
		return yielded;
	}
	
	public void yield(int location) {
		state.push(current = new State(location));
		yielded = true;
	}
	
	public int unyield() {
		int location = state.pop().location;
		if (state.isEmpty()) {
			yielded = false;
		}
		return location;
	}
	
	public void push(Object value) {
		current.locals.push(value);
	}
	
	public void push(boolean value) {
		current.locals.push(new Boolean(value));
	}
	
	public void push(int value) {
		current.locals.push(new Integer(value));
	}
	
	public Object popObject() {
		return current.locals.pop();
	}
	
	public boolean popBoolean() {
		return ((Boolean) current.locals.pop()).value;
	}
	
	public int popInt() {
		return ((Integer) current.locals.pop()).value;
	}
	
	private static final class State {
		
		public final int location;
		
		public final Stack<Object> locals = new Stack<Object>();
		
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
	
	private static final class Integer {
		
		public final int value;
		
		public Integer(int value) {
			this.value = value;
		}
		
	}
	
	// TODO Finish primitive classes here.

}
