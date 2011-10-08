package wyjc.runtime.concurrency;

import java.util.concurrent.ThreadFactory;

public class ActorThreadFactory implements ThreadFactory {

	private final Scheduler scheduler;
	
	public ActorThreadFactory(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	@Override
	public Thread newThread(Runnable task) {
		return new ActorThread(task);
	}
	
	
	public class ActorThread extends Thread {
		
		public ActorThread(Runnable task) {
			super(task);
		}
		
		public Scheduler getScheduler() {
			return scheduler;
		}
		
	}

}
