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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * A helper class for the actor hierarchy that involves the passing of messages
 * and scheduling resumptions on idle actors.
 * 
 * @author Timothy Jones
 */
public abstract class Messager extends Yielder {
	
	private final Scheduler scheduler;
	
	private final Queue<Message> mail = new LinkedList<Message>();
	
	private Message currentMessage = null;
	
	public Messager(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	/**
	 * Sends a synchronous message to this messager, to be executed at some point
	 * in the future, once all other messages in the queue are processed.
	 * 
	 * The primary difference between this method and <code>sendAsync</code> is
	 * how it manages the sender. Here, the sender will be informed that it should
	 * yield without resuming, and should not be resumed until this new message is
	 * complete, where this messager will resume it personally.
	 * 
	 * This method can be invoked from outside of the messager's scheduler (this
	 * includes other schedulers), but it will block the invoking thread instead
	 * of yielding. In this case, the future is guaranteed to be filled when the
	 * thread is freed.
	 * 
	 * @param method
	 *          The entry method of the message
	 * @param args
	 *          The entry arguments of the message
	 * 
	 * @return A future representing the result of the message.
	 * 
	 * @throws InterruptedException
	 *           The thread block was interrupted (cannot occur for
	 *           intra-scheduler message sends)
	 */
	public MessageFuture sendSync(Method method, Object[] args)
	    throws InterruptedException {
		Messager sender = scheduler.getCurrentStrand();
		
		SyncMessage senderMessage = null;
		if (sender != null && sender.currentMessage instanceof SyncMessage) {
			senderMessage = (SyncMessage) sender.currentMessage;
		}
		
		SyncMessage message = new SyncMessage(method, args, sender, senderMessage);
		
		// Cause a failure if a deadlock will occur.
		SyncMessage check = message;
		Stack<Messager> stack = new Stack<Messager>();
		stack.push(this);
		
		while (check != null) {
			stack.push(check.sender);
			
			if (check.sender == this) {
				String out = "Deadlock: ";
				while (stack.size() > 1) {
					out += stack.pop() + " -> ";
				}
				throw new RuntimeException(out + stack.pop());
			}
			
			check = check.senderMessage;
		}
		
		// The sender may be null if the message was sent outside of the scheduler.
		if (sender != null) {
			synchronized (this) {
				// If this actor isn't busy, run the message on the current thread.
				if (currentMessage == null) {
					currentMessage = message;
					message.directCall = true;
					
					controlThisThread();
					resume();
					
					sender.controlThisThread();
					
					// If the message completed, just return its final value.
					if (!isYielded()) {
						sender.shouldYield = false;
						return message.future;
					}
					
					message.directCall = false;
				}
			}
			
			sender.shouldYield = true;
		}
		
		addMessage(message);
		
		if (sender == null) {
			synchronized (message.future) {
				message.future.wait();
			}
		}
		
		return message.future;
	}
	
	/**
	 * Sends an asynchronous message to this messager, to be executed at some
	 * point in the future, once all other messages in the queue are processed.
	 * 
	 * The primary difference between this method and <code>sendSync</code> is how
	 * it manages the sender. Here, the sender is completely ignored.
	 * 
	 * @param method
	 *          The entry method of the message
	 * @param args
	 *          The entry arguments of the message
	 */
	public void sendAsync(Method method, Object[] args) {
		addMessage(new Message(method, args));
	}
	
	/**
	 * Evaluates whether the current message is a synchronous one. If not, it must
	 * be an asynchronous message.
	 * 
	 * This method is not synchronized. Only use this within the actor's control,
	 * or synchronize manually.
	 * 
	 * @return Whether the current message is synchronous
	 */
	protected boolean isCurrentMessageSynchronous() {
		return currentMessage instanceof SyncMessage;
	}
	
	/**
	 * Resume the current message (The message may not have started yet).
	 */
	protected abstract void resume();
	
	/**
	 * Force this messager to take control of the current thread.
	 */
	protected abstract void controlThisThread();
	
	/**
	 * Takes a message and either adds it to the queue orat sent this message, if
	 * synchronous, and the scheduling of the next mes begins working on it. If it
	 * should begin work, it automatically schedules itself to resume.
	 * 
	 * @param message
	 *          The message to add
	 */
	private void addMessage(Message message) {
		boolean resume;
		
		synchronized (this) {
			if (currentMessage == null) {
				currentMessage = message;
				resume = true;
			} else {
				mail.add(message);
				resume = false;
			}
		}
		
		if (resume) {
			scheduleResume();
		}
	}
	
	/**
	 * Schedules the messager to resume at an undetermined point in the future. If
	 * the messager is not ready to resume, this will cause it to immediately
	 * resume once it reaches a ready state.
	 */
	protected abstract void scheduleResume();
	
	/**
	 * Invokes the method passed by the current message with the arguments passed
	 * by the current message.
	 * 
	 * Note that this intentionally does not address the fact that the current
	 * message doesn't always exist, and so should only be called when it is
	 * certain to exist.
	 * 
	 * @return The result of the invocation
	 */
	protected Object invokeCurrentMethod() throws IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {
		return currentMessage.method.invoke(null, currentMessage.arguments);
	}
	
	/**
	 * Completes the current message.
	 * 
	 * Note that this method handles both the resumption of the actor that sent
	 * this message, if synchronous, and the scheduling of the next message, if
	 * one exists.
	 * 
	 * @param result
	 *          The result of the successful message
	 */
	protected void completeCurrentMessage(Object result) {
		if (currentMessage instanceof SyncMessage) {
			((SyncMessage) currentMessage).future.complete(result);
		}
		
		nextMessage();
	}
	
	/**
	 * Fails the current message.
	 * 
	 * Note that this method handles both the resumption of the actor that sent
	 * this message, if synchronous, and the scheduling of the next message, if
	 * one exists.
	 * 
	 * @param cause
	 *          The case of the message failure
	 */
	protected void failCurrentMessage(Throwable cause) {
		if (currentMessage instanceof SyncMessage) {
			((SyncMessage) currentMessage).future.fail(cause);
		}
		
		// TODO There's no crash if the entry method fails.
		nextMessage();
	}
	
	private void nextMessage() {
		if (currentMessage instanceof SyncMessage) {
			SyncMessage message = (SyncMessage) currentMessage;
			
			// If the sender is null, then the sendSync method will cause the thread
			// the method was invoked on to block. The message completion will have
			// already called notifyAll and freed it, so this is irrelevant.
			if (message.sender != null && !message.directCall) {
				message.sender.scheduleResume();
			}
		}
		
		boolean resume;
		
		synchronized (this) {
			if (mail.isEmpty()) {
				currentMessage = null;
				resume = false;
				
				// Threads can block while this messager is busy with the wait method.
				notifyAll();
			} else {
				currentMessage = mail.poll();
				resume = true;
			}
		}
		
		if (resume) {
			scheduleResume();
		}
	}
	
	private class Message {
		
		private final Method method;
		private final Object[] arguments;
		
		public Message(Method method, Object[] arguments) {
			// This is a hack for actor arguments. TODO Remove reflection entirely.
			if (arguments[0] == null) {
				arguments[0] = Messager.this;
			}
			
			this.method = method;
			this.arguments = arguments;
		}
		
	}
	
	private class SyncMessage extends Message {
		
		private final Messager sender;
		private final SyncMessage senderMessage;
		
		private boolean directCall = false;
		
		private final MessageFuture future;
		
		public SyncMessage(Method method, Object[] arguments, Messager sender,
		    SyncMessage senderMessage) {
			super(method, arguments);
			this.sender = sender;
			this.senderMessage = senderMessage;
			this.future = new MessageFuture();
		}
		
	}
	
	public static final class MessageFuture {
		
		private boolean completed = false;
		private boolean failed = false;
		
		private Object result;
		private Throwable cause;
		
		private MessageFuture() {}
		
		public boolean isFailed() {
			return failed;
		}
		
		public Object getResult() {
			if (!completed || failed) {
				throw new IllegalStateException(
				    "Requested result from incomplete message.");
			}
			
			return result;
		}
		
		public Throwable getCause() {
			if (!failed) {
				throw new IllegalStateException(
				    "Requested failure cause from message which has not failed.");
			}
			
			return cause;
		}
		
		private void complete(Object result) {
			if (completed) {
				throw new IllegalStateException(
				    "Attempted to complete an already completed message.");
			}
			if (failed) {
				throw new IllegalStateException(
				    "Attempted to complete a failed message.");
			}
			
			synchronized (this) {
				completed = true;
				this.result = result;
				
				notifyAll();
			}
		}
		
		private void fail(Throwable cause) {
			if (completed) {
				throw new IllegalStateException(
				    "Attempted to fail a completed message.");
			}
			if (failed) {
				throw new IllegalStateException(
				    "Attempted to fail an already failed message.");
			}
			
			synchronized (this) {
				failed = true;
				this.cause = cause;
				
				notifyAll();
			}
		}
		
	}
	
}
