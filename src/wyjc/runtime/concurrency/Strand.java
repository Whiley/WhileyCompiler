package wyjc.runtime.concurrency;

import java.lang.reflect.InvocationTargetException;

/**
 * A lightweight thread. Uses a <code>Scheduler</code> and yielding to simulate
 * threaded behaviour without the overhead associated with creating a real
 * thread, allowing large amounts to be created and used.
 * 
 * If <code>Strand</code> is instantiated, it will perform exactly like an
 * actor, except it has no internal state.
 * 
 * Strands are also usable outside of the ordinary Whiley runtime. The
 * <code>sendAsync</code> method will work as expected, and the
 * <code>sendSync</code> method will cause the calling thread to block until the
 * responding method exits. Calling <code>wait</code> on a strand will block
 * until the strand idles.
 * 
 * @author Timothy Jones
 */
public class Strand extends Messager {
	
	private final Scheduler scheduler;
	
	/**
	 * Whether the messager should resume immediately upon completing its yielding
	 * process. This is used mainly to react to a premature resumption, when the
	 * messager is asked to resume before being ready. In this case,
	 * <code>shouldResumeImmediately</code> will cause this actor to immediately
	 * place itself back in the scheduler.
	 */
	private boolean shouldResumeImmediately = false;
	
	/**
	 * Whether the messager is ready to resume. This is important if a message is
	 * sent synchronously and the receiver attempts to resume this messager before
	 * it has completed yielding, in which case the messager will enter an
	 * inconsistent state. Obviously, all messagers are ready to begin with.
	 */
	private boolean isReadyToResume = true;
	
	private long wakeAt = -1;
	
	/**
	 * @param scheduler
	 *          The scheduler to use for concurrency
	 */
	public Strand(Scheduler scheduler) {
		super(scheduler);
		
		this.scheduler = scheduler;
	}
	
	public static Strand getCurrentStrand() {
		Thread thread = Thread.currentThread();
		if (thread instanceof Scheduler.SchedulerThread) {
			return ((Scheduler.SchedulerThread) thread).getCurrentStrand();
		}
		
		return null;
	}
	
	@Override
	protected void controlThisThread() {
		scheduler.setCurrentStrand(this);
	}
	
	@Override
	protected void scheduleResume() {
		synchronized (this) {
			if (!isReadyToResume) {
				shouldResumeImmediately = true;
				return;
			}
			
			isReadyToResume = shouldResumeImmediately = false;
		}
		
		scheduler.scheduleResume(this);
	}
	
	/**
	 * @param milliseconds
	 *          The number of milliseconds to sleep for
	 */
	public void sleep(long milliseconds) throws InterruptedException {
		// TODO Reimplement this without blocking the thread.
		// shouldYield = true;
		// shouldResume = true;
		//
		// wakeAt = System.currentTimeMillis() + milliseconds;
		Thread.sleep(milliseconds);
	}
	
	public void resume() {
//		if (wakeAt != -1) {
//			if (System.currentTimeMillis() < wakeAt) {
//				scheduleResume();
//				return;
//			} else {
//				wakeAt = -1;
//			}
//		}
		
		try {
			Object result = invokeCurrentMethod();
			
			if (!isYielded()) {
				isReadyToResume = true;
				// Completes the message and moves on to the next one.
				completeCurrentMessage(result);
			} else {
				synchronized (this) {
					isReadyToResume = true;
					if (shouldResumeImmediately) {
						// Readies the actor for another resumption.
						scheduleResume();
					}
				}
			}
		} catch (IllegalArgumentException iax) {
			// Not possible - caught by the language compiler.
			System.err.println("Warning - illegal arguments in actor resumption.");
		} catch (IllegalAccessException iax) {
			// Not possible - all message invocations are on public methods.
			System.err.println("Warning - illegal access in actor resumption.");
		} catch (InvocationTargetException itx) {
			// Asynchronous messages aren't caught, so errors bottom out here.
			if (!isCurrentMessageSynchronous()) {
				System.err.println(this + " failed. " + itx.getCause().getMessage());
			}
			
			// Fails the message and moves on to the next one.
			failCurrentMessage(itx.getCause());
		}
	}
	
	@Override
	public String toString() {
		return "strand@" + System.identityHashCode(this);
	}
	
}
