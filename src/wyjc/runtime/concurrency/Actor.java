package wyjc.runtime.concurrency;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

import wyjc.runtime.concurrency.Scheduler.Resumable;

/**
 * A glorified queue that deals with the concurrency issues of actors passing
 * messages to one another while they're distributed across threads. A class
 * intending to become a fulltime actor
 * 
 * @author Timothy Jones
 */
public abstract class Actor implements Resumable {

	private final Scheduler scheduler;

	private final Queue<Message> mail = new LinkedList<Message>();

	private Message currentMessage = null;

	public Actor(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public MessageFuture sendSync(Actor sender, Method method, Object[] arguments) {
		Message message = new Message(sender, true, method, arguments);
		addMessage(message);
		return message.getFuture();
	}

	public void sendAsync(Actor sender, Method method, Object[] arguments) {
		addMessage(new Message(sender, false, method, arguments));
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

	protected void scheduleResume() {
		scheduler.scheduleResume(this);
	}

	protected Method getCurrentMethod() {
		return currentMessage.method;
	}

	protected Object[] getCurrentArguments() {
		return currentMessage.arguments;
	}

	protected void completeCurrentMessage(Object result) {
		currentMessage.complete(result);
	}

	protected void failCurrentMessage(Throwable cause) {
		currentMessage.fail(cause);
	}

	private final class Message {

		private final Actor sender;
		private final boolean synchronous;
		private final Method method;
		private final Object[] arguments;

		private final MessageFuture future;

		public Message(Actor sender, boolean synchronous, Method method,
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
			
			Actor.this.nextMessage();
		}

	}

	public static final class MessageFuture {

		private boolean completed = false;
		private boolean failed = false;

		private Object result;
		private Throwable cause;

		public boolean isCompleted() {
			return completed;
		}

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
