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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * 
 * @author Timothy Jones
 */
public final class Process {

	// Thread pool that Actors are distributed across.
	private static final ExecutorService pool = Executors.newCachedThreadPool();

	// The actors which are paused, waiting to be resumed.
	private static final Queue<Process> waiting = new LinkedList<Process>();

	// The spawned state of the process.
	private final WhileyRecord state;

	// The saved state of the call stack when an actor yields.
	private final Stack<State> stack = new Stack<State>();

	// The queue of messages which are waiting to be executed by this actor.
	private final Queue<Message> mailbox = new LinkedList<Message>();
	
	// The currently executing message, null when not paused.
	private Message message = null;

	public Process(WhileyRecord state) {
		this.state = state;
	}

	public WhileyRecord state() {
		return state;
	}

	/**
	 * Saves the local state of the method, then unwinds the stack and yields
	 * control of the thread.
	 * 
	 * @param localState The local state of the variables.
	 */
	public void yield(WhileyRecord locals) {
		yield(locals, true);
	}

	private void yield(WhileyRecord locals, boolean wait) {
		// Save the state here.

		if (wait) {
			waiting.add(this);
		}

		if (!waiting.isEmpty()) {
			waiting.poll().resume();
		}
	}

	/**
	 * Winds the stack back, reinstates the local state, and continues.
	 */
	public void resume() {
		if (message == null) {
			
			// If the mailbox is empty, the actor will power down.
			if (mailbox.isEmpty()) {
				return;
			}
		
			// Time to process the next message.
			message = mailbox.poll();
		}
		
		try {
			Object result = message.method.invoke(null, message.arguments);
			
			if (result instanceof State) {
				// TODO Unwind the call stack into the state stack.
				stack.push((State) result);
			} else {
				if (message.synchronous) {
					message.sender.notify(result);
				}
				message = null;
			}
		} catch (IllegalArgumentException iax) {
			// Not possible - caught by the language compiler.
		} catch (IllegalAccessException iax) {
			// Not possible - all message invocations are on public methods. 
		} catch (InvocationTargetException itx) {
			if (message.synchronous) {
				message.sender.notifyThrown(itx.getCause());
			}
		}
	}

	public void notify(Object result) {
		// TODO Set the synchronous return value.

		waiting.add(this);
	}

	public void notifyThrown(Throwable thrown) {
		// TODO Work out how to react to exceptions from messages.
		
		waiting.add(this);
	}

	/**
	 * Send a message asynchronously to this actor. Will invoke the given method
	 * once the actor frees up execution time.
	 * 
	 * @param method The message "method".
	 * @param arguments The message "arguments"
	 */
	public void asyncSend(Process sender, Method method, Object[] arguments) {
		arguments[0] = this;
		mailbox.add(new Message(sender, method, arguments, false));
	}

	/**
	 * Send a message synchronously to this actor. This will block the sender
	 * until the message is received, and a return value generated.
	 * 
	 * @param method The message "method".
	 * @param arguments The message "arguments".
	 */
	public void syncSend(Process sender, WhileyRecord locals, Method method,
	    Object[] arguments) {
		arguments[0] = this;
		mailbox.add(new Message(sender, method, arguments, true));
		sender.yield(locals, false);
	}

	public void run() {
		// this is where the action happens
		// while (true) {
		// try {
		// Message m = queue.take();
		// Object r = m.method.invoke(null, m.arguments);
		// if(m.synchronous){
		// m.set(r);
		// }
		// } catch(InterruptedException e) {
		// // do nothing I guess
		// } catch(IllegalAccessException e) {
		// // do nothing I guess
		// } catch(InvocationTargetException ex) {
		// // not sure what to do!
		// Throwable e = ex.getCause();
		// if(e instanceof RuntimeException) {
		// RuntimeException re = (RuntimeException) e;
		// throw re;
		// }
		// // do nothing I guess
		// }
		// }
	}

	public String toString() {
		return state + "@" + System.identityHashCode(this);
	}

	public static Process systemProcess() {
		// Not sure what the default value should be yet!!!
		Process sysout = new Process(null);
		HashMap<String, Object> fields = new HashMap<String, Object>();
		fields.put("out", sysout);
		Process system = new Process(new WhileyRecord(fields));
		// sysout.start();
		// system.start();
		return system;
	}

	private static final class Message {

		public final Method method;
		public final Object[] arguments;
		public final boolean synchronous;
		public final Process sender;
		public volatile boolean ready = false;
		public volatile Object result;

		public Message(Process sender, Method method, Object[] arguments,
		    boolean synchronous) {
			this.sender = sender;
			this.method = method;
			this.arguments = arguments;
			this.synchronous = synchronous;
		}

		public synchronized Object get() {
			while (!ready) {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
			return result;
		}

		public synchronized void set(Object result) {
			this.result = result;
			this.ready = true;
			notifyAll();
		}
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
