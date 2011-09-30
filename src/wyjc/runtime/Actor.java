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

package wyjc.runtime;

import wyjc.runtime.concurrency.Scheduler;
import wyjc.runtime.concurrency.Strand;

/**
 * A Whiley process, which mirrors an actor in the Actor Model of concurrency.
 * 
 * @author Timothy Jones
 */
public final class Actor extends Strand {
	
	private Object state;
	
	/**
	 * @param state The internal state of the actor.
	 * @param scheduler The scheduler to use for threading.
	 */
	public Actor(Object state, Scheduler scheduler) {
		super(scheduler);
		this.state = state;
	}

	/**
	 * @return The internal state of the actor.
	 */
	public Object getState() {
		return state;
	}

	/**
	 * @param state The internal state of the actor. 
	 * @return This actor (Useful for chaining).
	 */
	public Actor setState(Object state) {
		this.state = state;
		return this;
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
	public static Actor newSystemProcess(Scheduler scheduler) {
		Actor sysout = new Actor(null, scheduler);
		Record data = new Record();
		data.put("out", sysout);
		Actor system = new Actor(data, scheduler);
		return system;
	}

}
