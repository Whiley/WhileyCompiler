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
 * A helper class for the actor hierarchy that involves the passing of messages
 * and scheduling resumptions on idle actors.
 * 
 * @author Timothy Jones
 */
public abstract class Messager extends Yielder implements Resumable {

	private final Scheduler scheduler;

	private final Queue<Message> mail = new LinkedList<Message>();

	private Message currentMessage = null;

	/**
	 * Whether the messager is ready to resume. This is important if a message is
	 * sent synchronously and the receiver attempts to resume this messager before
	 * it has completed yielding, in which case the messager will enter an
	 * inconsistent state. Obviously, all messagers are ready to begin with.
	 * 
	 * See the <code>scheduleResume</code> and <code>beReadyToResume</code>
	 * methods for information on how its use is carried out.
	 */
	private boolean ready = true;

	/**
	 * Whether the messager should resume immediately upon completing its yielding
	 * process. This is used mainly to react to a premature resumption, when the
	 * messager is asked to resume before being ready. In this case, shouldResume
	 * will cause to immediately place itself back in the scheduler. Appropriate
	 * values are assigned in <code>sendSync</code> and <code>sendAsync</code>, so
	 * a default value is not necessary.
	 * 
	 * See the <code>scheduleResume</code> and <code>beReadyToResume</code>
	 * methods for information on how its use is carried out.
	 */
	protected boolean shouldResume = false;

	/**
	 * @param scheduler The scheduler to use for concurrency
	 */
	protected Messager(Scheduler scheduler) {
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
	 * @param sender The sender of the message
	 * @param method The entry method of the message
	 * @param args The entry arguments of the message
	 */
	public MessageFuture sendSync(Messager sender, Method method, Object[] args) {
		SyncMessage message = new SyncMessage(method, args, sender);

		// This needs to happen before the message is sent, otherwise this actor
		// might resume the sender before they've finished yielding.
		sender.ready = false;
		sender.shouldYield = true;
		sender.shouldResume = false;

		addMessage(message);

		return message.future;
	}

	/**
	 * Sends an asynchronous message to this messager, to be executed at some
	 * point in the future, once all other messages in the queue are processed.
	 * 
	 * The primary difference between this method and <code>sendSync</code> is how
	 * it manages the sender. Here, the sender will be informed that it should
	 * yield with an immediate resumption, but this can easily be modified in the
	 * future to better react to the sending context.
	 * 
	 * @param sender The sender of the message
	 * @param method The entry method of the message
	 * @param args The entry arguments of the message
	 */
	public void sendAsync(Messager sender, Method method, Object[] args) {
		// This needs to happen before the message is sent, otherwise the actor
		// could start before these are set to the correct values.
		sender.shouldYield = false;
		sender.shouldResume = true;

		addMessage(new Message(method, args));
	}

	/**
	 * Takes a message and either adds it to the queue or begins working on it. If
	 * it should begin work, it automatically schedules itself to resume.
	 * 
	 * @param message The message to add
	 */
	private void addMessage(Message message) {
		if (addMessageSynchronized(message)) {
			scheduleResume();
		}
	}

	/**
	 * Adds a message to the queue in a thread-safe manner. If the messager isn't
	 * currently working on a message, this method will instead immediately place
	 * it into <code>currentMessage</code>.
	 * 
	 * @param message The message to add
	 * @return Whether the added message is now the current message
	 */
	private synchronized boolean addMessageSynchronized(Message message) {
		if (currentMessage == null) {
			currentMessage = message;
			return true;
		} else {
			mail.add(message);
			return false;
		}
	}

	/**
	 * Schedules the messager to resume at an undetermined point in the future. If
	 * the messager is not ready to resume, this will cause it to immediately
	 * resume once it reaches a ready state.
	 */
	protected void scheduleResume() {
		if (ensureReady()) {
			return;
		}

		shouldResume = false;
		scheduler.scheduleResume(this);
	}

	/**
	 * A synchronised method that checks if the messager is ready to resume. If
	 * not, it remembers that a resumption was attempted, and informs the messager
	 * that it should resume immediately on becoming ready.
	 * 
	 * @return Whether the messager is currently ready
	 */
	private synchronized boolean ensureReady() {
		if (!ready) {
			shouldResume = true;
			return true;
		}

		return false;
	}

	/**
	 * Sets the messager as ready to resume, and returns whether the messager
	 * should resume immediately.
	 * 
	 * The reason this dual functionality is composed into a single method is that
	 * a race condition can occur in trying to separate them out into two, as a
	 * resumption can arrive during the setting of the ready state.
	 * 
	 * @return Whether the messager should resume as a result of being ready
	 */
	protected synchronized boolean readyToResume() {
		ready = true;
		return shouldResume;
	}

	/**
	 * @return The entry method of this messager's current message
	 * @throws NullPointerException There is no current message
	 */
	protected Method getCurrentMethod() throws NullPointerException {
		return currentMessage.method;
	}

	/**
	 * @return The entry arguments of this messager's current message
	 * @throws NullPointerException There is no current message
	 */
	protected Object[] getCurrentArguments() throws NullPointerException {
		return currentMessage.arguments;
	}

	/**
	 * Completes the current message.
	 * 
	 * Note that this method handles both the resumption of the actor that sent
	 * this message, if synchronous, and the scheduling of the next message, if
	 * one exists.
	 * 
	 * @param result The result of the successful message
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
	 * @param cause The case of the message failure
	 */
	protected void failCurrentMessage(Throwable cause) {
		if (currentMessage instanceof SyncMessage) {
			((SyncMessage) currentMessage).future.fail(cause);
		}

		// TODO There's no crash if the entry method fails.
		nextMessage();
	}

	private synchronized void nextMessage() {
		if (currentMessage instanceof SyncMessage) {
			((SyncMessage) currentMessage).sender.scheduleResume();
		}

		if (mail.isEmpty()) {
			currentMessage = null;
			// Threads can block while this messager is busy with the wait method.
			notifyAll();
		} else {
			currentMessage = mail.poll();
			scheduleResume();
		}
	}

	private class Message {

		private final Method method;
		private final Object[] arguments;

		public Message(Method method, Object[] arguments) {
			arguments[0] = Messager.this;

			this.method = method;
			this.arguments = arguments;
		}

	}

	private class SyncMessage extends Message {

		private final Messager sender;

		private final MessageFuture future;

		public SyncMessage(Method method, Object[] arguments, Messager sender) {
			super(method, arguments);
			this.sender = sender;
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
