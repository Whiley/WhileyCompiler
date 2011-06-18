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

import wyjc.runtime.concurrency.Messager;
import wyjc.runtime.concurrency.Scheduler;

/**
 * A Whiley process, which mirrors an actor in the Actor Model of concurrency.
 * 
 * @author Timothy Jones
 */
public final class Actor extends Messager {
	
	private static final Scheduler scheduler = new Scheduler();
	
	// The spawned state of the process.
	private final Object state;

	public Actor(Object state) {
		super(scheduler);
		
		this.state = state;
	}

	public Object getState() {
		return state;
	}
	
	@Override
	public void resume() {
		try {
			Object result = getCurrentMethod().invoke(null, getCurrentArguments());
			
			if (!isYielded()) {
				// Completes the message and moves on to the next one.
				completeCurrentMessage(result);
			}
		} catch (IllegalArgumentException iax) {
			// Not possible - caught by the language compiler.
			System.err.println("Warning - illegal arguments in actor resumption.");
		} catch (IllegalAccessException iax) {
			// Not possible - all message invocations are on public methods.
			System.err.println("Warning - illegal access in actor resumption.");
		} catch (InvocationTargetException itx) {
			// Fails the message and moves on to the next one.
			failCurrentMessage(itx); 
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
	public static Actor newSystemProcess() {
		Actor sysout = new Actor(null);
		WhileyRecord fields = new WhileyRecord();
		fields.put("out", sysout);
		Actor system = new Actor(fields);
		return system;
	}

}
