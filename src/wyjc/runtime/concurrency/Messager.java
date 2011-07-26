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

import wyjc.runtime.concurrency.Scheduler.Resumable;

/**
 * A helper class for the actor hierarchy that involves the passing of messages
 * and scheduling resumptions on idle actors.
 * 
 * @author Timothy Jones
 */
public abstract class Messager extends Yielder implements Resumable {

	private final Scheduler scheduler;

	private final Queue<Message> mail = new LinkedList<Message>();

	private Message currentMessage = null;

	// Note that one the WYIL is fixed, this can be disposed of for
	// currentMessage.future.
	private MessageFuture currentFuture = null;

	private boolean ready = true;
	private boolean shouldResume = true;

	public Messager(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void sendSync(Messager sender, Method method, Object[] args) {
		// TODO This section prevents a synchronous message from the same object.
		// This should be fixed further back up the pipeline, but this is the
		// current temporary solution.
		if (sender == this) {
			sender.shouldYield = false;
			currentFuture = new MessageFuture();

			try {
				currentFuture.complete(method.invoke(null, args));
			} catch (IllegalArgumentException iax) {
				System.err.println("Warning - illegal arguments in actor resumption.");
			} catch (IllegalAccessException iax) {
				System.err.println("Warning - illegal access in actor resumption.");
			} catch (InvocationTargetException itx) {}

			return;
		}

		System.err.println(this + " receiving sync from " + sender);
		Message message = new SyncMessage(method, args, sender);

		// This needs to happen before the message is sent, otherwise this actor
		// might resume the sender before they've finished yielding.
		sender.ready = false;
		sender.shouldYield = true;
		sender.shouldResume = false;
		sender.currentFuture = message.future;

		addMessage(message);
	}

	/**
	 * Performs exactly the same operation as <code>sendSync</code>. Included
	 * purely to help the bytecode understand when a value isn't needed.
	 * 
	 * @param sender
	 * @param method
	 * @param args
	 */
	public void sendSyncVoid(Messager sender, Method method, Object[] args) {
		sendSync(sender, method, args);
	}

	public void sendAsync(Messager sender, Method method, Object[] args) {
		System.err.println(this + " receiving async from " + sender);

		addMessage(new Message(method, args));

		sender.shouldYield = false;
		sender.shouldResume = true;
	}

	private void addMessage(Message message) {
		if (addMessageSynchronized(message)) {
			scheduleResume();
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
		if (ensureReady()) {
			return;
		}

		shouldResume = false;
		scheduler.scheduleResume(this);
	}

	private synchronized boolean ensureReady() {
		if (!ready) {
			shouldResume = true;
			return true;
		}

		return false;
	}

	protected synchronized boolean shouldResume() {
		return shouldResume;
	}

	protected synchronized boolean isReady() {
		return ready;
	}

	protected synchronized void beReady() {
		ready = true;
	}

	protected Method getCurrentMethod() {
		return currentMessage.method;
	}

	protected Object[] getCurrentArguments() {
		return currentMessage.arguments;
	}

	public MessageFuture getCurrentFuture() {
		return currentFuture;
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
		if (currentMessage instanceof SyncMessage) {
			((SyncMessage) currentMessage).sender.scheduleResume();
		}

		if (mail.isEmpty()) {
			currentMessage = null;
		} else {
			currentMessage = mail.poll();
			scheduleResume();
		}
	}

	private class Message {

		private final Method method;
		private final Object[] arguments;

		private final MessageFuture future;

		public Message(Method method, Object[] arguments) {
			this.method = method;
			this.arguments = arguments;

			future = new MessageFuture();
		}

	}

	private class SyncMessage extends Message {

		private final Messager sender;

		public SyncMessage(Method method, Object[] arguments, Messager sender) {
			super(method, arguments);
			this.sender = sender;
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
