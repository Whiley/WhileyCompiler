// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// * Neither the name of the <organization> nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc.runtime.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A helper class for the actor hierarchy that involves the yielding of thread
 * control and saving of local state.
 * 
 * @author Timothy Jones
 */
public abstract class Yielder {
	
	private final Stack<State> state = new Stack<State>();
	private State current = null;
	
	private boolean yielded = false;
	protected boolean shouldYield = false;
	
	/**
	 * @return Whether the object is currently yielding or has yielded
	 */
	public boolean isYielded() {
		return yielded;
	}
	
	/**
	 * @return Whether the object should yield when it next decides whether to
	 */
	public boolean shouldYield() {
		return shouldYield;
	}
	
	/**
	 * Pushes a new local state object onto the stack. Calls to the push and set
	 * methods after calling this method will attach to the pushed object for
	 * later retrieval with <code>unyield</code>.
	 * 
	 * @param location
	 *          The location of the computation in the method
	 */
	public void yield(int location) {
		state.push(current = new State(location));
		yielded = true;
	}
	
	/**
	 * Pops a local state object off of the stack, moving the get and pop and pop
	 * methods onto the next local values.
	 */
	public void unyield() {
		state.pop();
		
		if (state.isEmpty()) {
			yielded = false;
		}
		
		current = yielded ? state.peek() : null;
	}
	
	/**
	 * Gets the location of the state on the top of the stack. If the stack is
	 * empty, it returns -1.
	 * 
	 * @return The location of the current state, or -1 if no such state exists
	 */
	public int getCurrentStateLocation() {
		if (state.isEmpty()) {
			return -1;
		}
		
		return state.peek().location;
	}
	
	public void set(int index, Object value) {
		current.localMap.put(index, value);
	}
	
	public void set(int index, boolean value) {
		current.localMap.put(index, value);
	}
	
	public void set(int index, char value) {
		current.localMap.put(index, value);
	}
	
	public void set(int index, double value) {
		current.localMap.put(index, value);
	}
	
	public void set(int index, int value) {
		current.localMap.put(index, value);
	}
	
	public void set(int index, float value) {
		current.localMap.put(index, value);
	}
	
	public void set(int index, long value) {
		current.localMap.put(index, value);
	}
	
	public Object getObject(int index) {
		return current.localMap.get(index);
	}
	
	public boolean getBool(int index) {
		return (Boolean) current.localMap.get(index);
	}
	
	public char getChar(int index) {
		return (Character) current.localMap.get(index);
	}
	
	public double getDouble(int index) {
		return (Double) current.localMap.get(index);
	}
	
	public int getInt(int index) {
		return (Integer) current.localMap.get(index);
	}
	
	public float getFloat(int index) {
		return (Float) current.localMap.get(index);
	}
	
	public long getLong(int index) {
		return (Long) current.localMap.get(index);
	}
	
	public void push(Object value) {
		current.localStack.push(value);
	}
	
	public void push(boolean value) {
		current.localStack.push(value);
	}
	
	public void push(char value) {
		current.localStack.push(value);
	}
	
	public void push(double value) {
		current.localStack.push(value);
	}
	
	public void push(int value) {
		current.localStack.push(value);
	}
	
	public void push(float value) {
		current.localStack.push(value);
	}
	
	public void push(long value) {
		current.localStack.push(value);
	}
	
	public Object popObject() {
		return current.localStack.pop();
	}
	
	public boolean popBool() {
		return (Boolean) current.localStack.pop();
	}
	
	public char popChar() {
		return (Character) current.localStack.pop();
	}
	
	public double popDouble() {
		return (Double) current.localStack.pop();
	}
	
	public int popInt() {
		return (Integer) current.localStack.pop();
	}
	
	public float popFloat() {
		return (Float) current.localStack.pop();
	}
	
	public long popLong() {
		return (Long) current.localStack.pop();
	}
	
	private static final class State {
		
		private final int location;
		
		private final Map<Integer, Object> localMap =
		    new HashMap<Integer, Object>();
		private final Stack<Object> localStack = new Stack<Object>();
		
		public State(int location) {
			this.location = location;
		}
		
	}
	
}
