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
public abstract class Yielder {

	private final Stack<State> state = new Stack<State>();
	private State current = null;

	private boolean yielded = false;

	/**
	 * @return Whether the object is currently yielding or has yielded.
	 */
	public boolean isYielded() {
		return yielded;
	}

	public boolean isEmpty() {
		return state.isEmpty();
	}

	/**
	 * Yields control of the thread, but does not push a new local state object
	 * onto the stack. Useful for when a message is the last action of a method,
	 * where yielding makes sense but saving the local state does not.
	 * 
	 * @throws IllegalStateException The object is already yielded.
	 */
	public void cleanYield() {
		if (yielded) {
			throw new IllegalStateException(
			    "Attempting to cleanly yield while object is already yielded.");
		}
		yielded = true;
	}

	/**
	 * Reverts the yield state introduced by the <code>yield()</code> method, if
	 * there wasn't any stack state pushed. If there was, <code>unyield</code>
	 * will perform the same thing once it reaches the bottom of the stack.
	 * 
	 * @throws IllegalStateException The clean yield is no longer valid.
	 */
	public void revertCleanYield() {
		if (!state.isEmpty()) {
			throw new IllegalStateException(
			    "Attempting to revert a clean yield that is no longer invalid.");
		}
		yielded = false;
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
	 * Pops a local state object off of the stack, moving the <code>get</code>
	 * methods onto the next locals.
	 */
	public void unyield() {
		state.pop();
		
		if (state.isEmpty()) {
			yielded = false;
		}
	}
	
	/**
	 * Gets the location of the state on the top of the stack. If the stack is
	 * empty, it returns -1.
	 * 
	 * @return The location of the current state, or -1 if no such state exists.
	 */
	public int getCurrentStateLocation() {
		if (isEmpty()) {
			return -1;
		}
		
		return state.peek().location;
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

	private static final class State {

		public final int location;

		public final List<Object> localMap = new ArrayList<Object>();

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
