package wyjc.runtime.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A task scheduler for the actor system that distributes the processes
 * amongst a certain number of threads. Once all threads are busy newly
 * scheduled tasks are delayed until an existing thread becomes available.
 * 
 * @author Timothy Jones
 */
public final class Scheduler {

	// Count of the number of scheduled tasks. When it returns to 0, the thread
	// pool will shut down.
	private int scheduledCount = 0;

	// The thread pool that tasks will be distributed across.
	private ExecutorService pool;

	/**
	 * Creates a new scheduler with a cached thread pool, meaning threads will
	 * be booted up as needed, rather than all at once.
	 */
	public Scheduler() {
		pool = Executors.newCachedThreadPool();
	}

	/**
	 * Creates a new scheduler with a thread pool of a fixed size.
	 * 
	 * @param threadCount The number of threads to have in the pool.
	 */
	public Scheduler(int threadCount) {
		pool = Executors.newFixedThreadPool(threadCount);
	}

	/**
	 * Any object which wants to be distributed as a task needs to be resumable,
	 * and so must implement this interface.
	 * 
	 * @author Timothy Jones
	 */
	public static interface Resumable {

		/**
		 * This is the method that will be invoked when the task is ran on an
		 * available thread.
		 */
		public void resume();

	}

	/**
	 * Schedules the given object to resume as soon as a thread is available.
	 * 
	 * @param resumable The object to schedule a resume for.
	 */
	public void scheduleResume(Resumable resumable) {
		increaseCount();
		pool.execute(new Resumer(resumable));
	}

	/**
	 * Synchronises the increasing of the scheduled counter.
	 */
	private synchronized void increaseCount() {
		scheduledCount += 1;
	}

	/**
	 * Synchronises the decreasing of the scheduled counter.
	 */
	private synchronized void decreaseCount() {
		scheduledCount -= 1;
	}

	/**
	 * Handles the resuming of tasks by implementing runnable, allowing it to run
	 * on a different thread.
	 * 
	 * @author Timothy Jones
	 */
	private class Resumer implements Runnable {

		// The object to resume.
		private final Resumable resumable;

		/**
		 * @param resumable The object to resume.
		 */
		public Resumer(Resumable resumable) {
			this.resumable = resumable;
		}

		@Override
		public void run() {
			try {
				resumable.resume();
			} catch (Throwable th) {
				System.err.println("Warning - actor resumption threw an exception.");
			}

			decreaseCount();

			if (scheduledCount == 0) {
				pool.shutdown();
			}
		}

	}

}
