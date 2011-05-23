package wyjc.runtime.concurrency;

import wyjc.runtime.concurrency.Scheduler.Resumable;

/**
 * A helper top-level class for the actor hierarchy.
 * 
 * @author Timothy Jones
 */
public abstract class Resumer implements Resumable {
	
	private final Scheduler scheduler;
	
	public Resumer(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	/**
	 * Schedules a call to this object's <code>resume</code> method sometime
	 * in the future, using the given scheduler.
	 */
	protected void scheduleResume() {
		scheduler.scheduleResume(this);
	}
	
}
