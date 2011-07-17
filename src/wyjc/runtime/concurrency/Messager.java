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

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

import wyjc.runtime.concurrency.Scheduler.Resumable;

/**
 * A helper class for the actor hierarchy that involves the passing of
 * messages and scheduling resumptions on idle actors.
 * 
 * @author Timothy Jones
 */
public abstract class Messager extends Yielder implements Resumable {

	private final Scheduler scheduler;

	private final Queue<Message> mail = new LinkedList<Message>();

	private Message currentMessage = null, lastSentMessage = null;

	public Messager(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public MessageFuture sendSync(Messager sender, Method method, Object[] args) {
		Message message = new Message(sender, true, method, args);
		addMessage(message);
		return message.getFuture();
	}

	public void sendAsync(Messager sender, Method method, Object[] args) {
		System.err.println(this + " receiving async from " + sender);
		addMessage(new Message(sender, false, method, args));
	}
	
	private void addMessage(Message message) {
		if (addMessageSynchronized(message)) {
			scheduleResume();
		}
		
		// The initial main method has null as a sender.
		// It might be better to change that so we don't have to do this here.
		if (message.sender != null) {
			// We don't have to synchronise this, as an actor can only be doing one
			// thing at a time, and at the moment we know the sender is here too.
			message.sender.lastSentMessage = message;
		}
	}

	private synchronized boolean addMessageSynchronized(Message message) {
		if (currentMessage == null) {
			currentMessage = message;
			return true;
		} else {
			mail.add(message);
			return false;
		}
	}

	protected void scheduleResume() {
		scheduler.scheduleResume(this);
	}

	protected Method getCurrentMethod() {
		return currentMessage.method;
	}

	protected Object[] getCurrentArguments() {
		return currentMessage.arguments;
	}

	protected boolean isLastSentSynchronous() {
		return lastSentMessage.synchronous;
	}

	/**
	 * Completes the current message.
	 * 
	 * Note that this method handles both the resumption of the actor that sent
	 * this message, if synchronous, and the scheduling of the next message, if
	 * one exists.
	 * 
	 * @param result The result of the successful message.
	 */
	protected void completeCurrentMessage(Object result) {
		currentMessage.future.complete(result);
		nextMessage();
	}

	/**
	 * Fails the current message.
	 * 
	 * Note that this method handles both the resumption of the actor that sent
	 * this message, if synchronous, and the scheduling of the next message, if
	 * one exists.
	 * 
	 * @param cause The case of the message failure.
	 */
	protected void failCurrentMessage(Throwable cause) {
		currentMessage.future.fail(cause);
		nextMessage();
	}

	private synchronized void nextMessage() {
		if (currentMessage.synchronous) {
			currentMessage.sender.scheduleResume();
		}

		if (mail.isEmpty()) {
			currentMessage = null;
		} else {
			currentMessage = mail.poll();
			scheduleResume();
		}
	}

	private final class Message {

		private final Messager sender;
		private final boolean synchronous;
		private final Method method;
		private final Object[] arguments;

		private final MessageFuture future;

		public Message(Messager sender, boolean synchronous, Method method,
		    Object[] arguments) {
			this.sender = sender;
			this.synchronous = synchronous;
			this.method = method;
			this.arguments = arguments;

			future = new MessageFuture();
		}

		public MessageFuture getFuture() {
			return future;
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
			if (!completed) {
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

			completed = true;
			this.result = result;
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

			failed = true;
			this.cause = cause;
		}

	}

}
