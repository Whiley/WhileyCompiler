package wyjc.runtime.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public final class Scheduler {

	private int scheduled = 1;

	private ExecutorService pool;

	public Scheduler() {
		pool = Executors.newCachedThreadPool();
	}

	public Scheduler(int threadCount) {
		pool = Executors.newFixedThreadPool(threadCount);
	}
	
	private synchronized void increaseCount() {
		scheduled += 1;
	}
	
	private synchronized void decreaseCount() {
		scheduled -= 1;
	}

	public void scheduleResume(Resumable resumable) {
		System.err.println(resumable);
		
		increaseCount();
		
		pool.execute(new Resumer(resumable));
	}

	public static interface Resumable {

		public void resume();

	}

	private class Resumer implements Runnable {

		private final Resumable process;

		public Resumer(Resumable process) {
			this.process = process;
		}

		@Override
		public void run() {
			try {
				process.resume();
			} catch (Throwable th) {}

			decreaseCount();
			
			if (scheduled == 0) {
				pool.shutdown();
			}
		}

	}

}
