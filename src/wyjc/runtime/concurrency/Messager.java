package wyjc.runtime.concurrency;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A helper class for the actor hierarchy that involves the passing of
 * messages and scheduling resumptions on idle actors.
 * 
 * @author Timothy Jones
 */
public abstract class Messager extends Yielder {

	private final Queue<Message> mail = new LinkedList<Message>();

	private Message currentMessage = null;

	public Messager(Scheduler scheduler) {
		super(scheduler);
	}

	public MessageFuture sendSync(Messager sender, Method method, Object[] args) {
		Message message = new Message(sender, true, method, args);
		addMessage(message);
		return message.getFuture();
	}

	public void sendAsync(Messager sender, Method method, Object[] args) {
		addMessage(new Message(sender, false, method, args));
	}

	private synchronized void addMessage(Message message) {
		if (currentMessage == null) {
			currentMessage = message;
			scheduleResume();
		} else {
			mail.add(message);
		}
	}

	private synchronized void nextMessage() {
		if (mail.isEmpty()) {
			currentMessage = null;
		} else {
			currentMessage = mail.poll();
			scheduleResume();
		}
	}

	protected Method getCurrentMethod() {
		return currentMessage.method;
	}

	protected Object[] getCurrentArguments() {
		return currentMessage.arguments;
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
		currentMessage.complete(result);
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
		currentMessage.fail(cause);
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

		public void complete(Object result) {
			future.complete(result);

			nextMessage();
		}

		public void fail(Throwable cause) {
			future.fail(cause);

			nextMessage();
		}

		private void nextMessage() {
			if (synchronous) {
				sender.scheduleResume();
			}

			Messager.this.nextMessage();
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
