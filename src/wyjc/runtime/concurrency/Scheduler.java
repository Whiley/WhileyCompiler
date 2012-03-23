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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A task scheduler for the actor system that distributes the processes amongst
 * a certain number of threads. Once all threads are busy newly scheduled tasks
 * are delayed until an existing thread becomes available.
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
	 * Creates a new scheduler with a cached thread pool, meaning threads will be
	 * booted up as needed, rather than all at once.
	 */
	public Scheduler() {
		pool = Executors.newCachedThreadPool(new SchedulerThreadFactory());
	}
	
	/**
	 * Creates a new scheduler with a thread pool of a fixed size.
	 * 
	 * @param threadCount
	 *          The number of threads to have in the pool.
	 */
	public Scheduler(int threadCount) {
		pool =
		    Executors.newFixedThreadPool(threadCount, new SchedulerThreadFactory());
	}
	
	/**
	 * Schedules the given object to resume as soon as a thread is available.
	 * 
	 * @param resumable
	 *          The object to schedule a resume for.
	 */
	public void scheduleResume(final Strand resumable) {
		synchronized (this) {
			scheduledCount += 1;
		}
		
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				SchedulerThread thread = (SchedulerThread) Thread.currentThread();
				
				try {
					thread.currentStrand = resumable;
					resumable.resume();
				} catch (Throwable th) {
					System.err.println("Warning - actor resumption threw an exception.");
					th.printStackTrace();
				} finally {
					thread.currentStrand = resumable;
				}
				
				synchronized (Scheduler.this) {
					scheduledCount -= 1;
				}
				
				if (scheduledCount == 0) {
					pool.shutdown();
					
					synchronized (Scheduler.this) {
						Scheduler.this.notifyAll();
					}
				}
			}
			
		});
	}
	
	/**
	 * Retrieves the strand controlling the current thread if the current thread
	 * is in this scheduler's thread pool.
	 * 
	 * Messages will mostly arrive from elsewhere in the scheduler, so it is
	 * possible to access the strand that sent them.
	 * 
	 * Note, however, that it is possible for a thread outside of the scheduler to
	 * send a message. When a Whiley program first boots, the main method sets up
	 * a new scheduler, an initial strand, and then sends it an asynchronous
	 * message. Inter-scheduler message sends will also be unable to retrieve a
	 * sender, otherwise the sender would be pulled over into this scheduler.
	 * 
	 * If this method is invoked from outside of this scheduler's thread pool,
	 * then the result will be null.
	 * 
	 * @return The strand controlling the current thread, or null
	 */
	public Strand getCurrentStrand() {
		Thread currentThread = Thread.currentThread();
		
		if (currentThread instanceof SchedulerThread) {
			SchedulerThread thread = (SchedulerThread) currentThread;
			
			if (thread.getScheduler() == this) {
				return thread.getCurrentStrand();
			}
		}
		
		return null;
	}
	
	/**
	 * Notifies the current thread that its controlling strand has changed. Does
	 * nothing if invoked outside the scheduler. For package use only.
	 * 
	 * @param strand
	 *          The strand now controlling the current thread
	 */
	void setCurrentStrand(Strand strand) {
		Thread currentThread = Thread.currentThread();
		
		if (currentThread instanceof SchedulerThread) {
			SchedulerThread thread = (SchedulerThread) currentThread;
			
			if (thread.getScheduler() == this) {
				thread.currentStrand = strand;
			}
		}
	}
	
	/**
	 * A thread that can expose this scheduler and the currently controlling
	 * strand so new tasks can be spawned more easily by those already using it.
	 * 
	 * @author Timothy Jones
	 */
	public class SchedulerThread extends Thread {
		
		private Strand currentStrand;
		
		private SchedulerThread(Runnable task) {
			super(task);
		}
		
		/**
		 * @return The scheduler in charge of this thread.
		 */
		public Scheduler getScheduler() {
			return Scheduler.this;
		}
		
		public Strand getCurrentStrand() {
			return currentStrand;
		}
		
	}
	
	/**
	 * The factory for <code>SchedulerThread</code>.
	 * 
	 * @author Timothy Jones
	 */
	private class SchedulerThreadFactory implements ThreadFactory {
		
		@Override
		public Thread newThread(Runnable task) {
			return new SchedulerThread(task);
		}
		
	}
	
}
