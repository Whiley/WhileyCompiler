package wyjc.runtime.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
	
	private volatile int scheduled = 0;
	
	private final ExecutorService pool;
	
	public Scheduler() {
		pool = Executors.newCachedThreadPool();
	}
	
	public Scheduler(int threadCount) {
		pool = Executors.newFixedThreadPool(threadCount);
	}
	
	public void scheduleResume(Resumable process) {
		increment();
		pool.execute(new Resumer(process));
	}
	
	private synchronized void increment() {
		scheduled += 1;
	}
	
	private synchronized void decrement() {
		scheduled -= 1;
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
			
			decrement();
			if (scheduled == 0) {
				pool.shutdown();
			}
		}
		
	}
	
}
