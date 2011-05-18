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
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import wyjc.runtime.concurrency.Actor;
import wyjc.runtime.concurrency.Scheduler;

/**
 * A Whiley process, which mirrors an actor in the Actor Model of concurrency.
 * 
 * @author Timothy Jones
 */
public final class Process extends Actor {
	
	private static final Scheduler scheduler = new Scheduler();
	
	// The spawned state of the process.
	private final WhileyRecord state;

	// The saved state of the call stack when an actor yields.
	private final Stack<State> stack = new Stack<State>();

	public Process(WhileyRecord state) {
		super(scheduler);
		this.state = state;
	}

	public WhileyRecord getState() {
		return state;
	}

	/**
	 * Halts the actor's computation, thereby yielding control of the thread.
	 * A resumption of the computation is also scheduled when threads become
	 * available for use.
	 * 
	 * @param pc The program counter from the continuation rewriter.
	 * @param locals The state of the local variables.
	 */
	public void yield(int pc, WhileyRecord locals) {
		halt(pc, locals);
		scheduleResume();
	}
	
	/**
	 * Saves the local state of the method, then unwinds the stack and halts
	 * the actor's computation. Use of resume will synchronously reverse the
	 * effects of halt, and scheduleResume will asynchronously reverse it.
	 * 
	 * @param pc The program counter from the continuation rewriter.
	 * @param locals The state of the local variables.
	 */
	private void halt(int pc, WhileyRecord locals) {
		// TODO Unwind the stack.
	}

	@Override
	public void resume() {
		try {
			Object result = getCurrentMethod().invoke(null, getCurrentArguments());
			
			if (result instanceof State) {
				// TODO Unwind the call stack into the state stack.
				stack.push((State) result);
			} else {
				completeCurrentMessage(result);
			}
		} catch (IllegalArgumentException iax) {
			// Not possible - caught by the language compiler.
		} catch (IllegalAccessException iax) {
			// Not possible - all message invocations are on public methods. 
		} catch (InvocationTargetException itx) {
			failCurrentMessage(itx);
			
			// TODO Work out how to react to async exceptions on a root method.
			// Do we send information to the process that sent the message? 
		}
	}
	
	@Override
	public String toString() {
		return state + "@" + System.identityHashCode(this);
	}
	
	/**
	 * Creates and returns a new <code>System</code> process from the standard
	 * library, for entry into the actor system. Asynchronously passing the
	 * <code>main</code> message to the resulting system is the typical entry
	 * point for the runtime.
	 * 
	 * @return A new Whiley <code>System</code> process.
	 */
	public static Process newSystemProcess() {
		Process sysout = new Process(null);
		WhileyRecord fields = new WhileyRecord();
		fields.put("out", sysout);
		Process system = new Process(fields);
		return system;
	}

	private static final class State {

		public final int pc;
		public final WhileyRecord locals;

		public State(int pc, WhileyRecord locals) {
			this.pc = pc;
			this.locals = locals;
		}

	}

}
