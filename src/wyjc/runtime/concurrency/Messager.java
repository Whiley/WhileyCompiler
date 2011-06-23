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
