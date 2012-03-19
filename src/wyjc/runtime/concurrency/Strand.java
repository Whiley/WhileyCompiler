package wyjc.runtime.concurrency;

import java.lang.reflect.InvocationTargetException;

/**
 * A lightweight thread. Uses a <code>Scheduler</code> and yielding to
 * simulate threaded behaviour without the overhead associated with creating a
 * real thread, allowing large amounts to be created and used.
 * 
 * If <code>Strand</code> is instantiated, it will perform exactly like an
 * actor, except it has no internal state.
 * 
 * Strands are also usable outside of the ordinary Whiley runtime. The
 * <code>sendAsync</code> method will work as expected, and the
 * <code>sendSync</code> method will cause the calling thread to block until
 * the responding method exits. Calling <code>wait</code> on a strand will
 * block until the strand idles.
 * 
 * @author Timothy Jones
 */
public class Strand extends Messager {

	private final Scheduler scheduler;
	
	private long wakeAt = -1;
	
	/**
	 * @param scheduler The scheduler to use for concurrency
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
	protected void scheduleResume() {
		super.scheduleResume();
		
		scheduler.scheduleResume(this);
	}
	
	/**
	 * @param milliseconds The number of milliseconds to sleep for
	 */
	public void sleep(long milliseconds) {
		shouldYield = true;
		shouldResume = true;
		
		wakeAt = System.currentTimeMillis() + milliseconds;
	}

	public void resume() {
		if (wakeAt != -1) {
			if (System.currentTimeMillis() < wakeAt) {
				scheduleResume();
				return;
			} else {
				wakeAt = -1;
			}
		}
		
		try {
			Object result = invokeCurrentMethod();

			if (!isYielded()) {
				// Completes the message and moves on to the next one.
				completeCurrentMessage(result);
			} else if (readyToResume()) {
				// Readies the actor for another resumption.
				scheduleResume();
			}
		} catch (IllegalArgumentException iax) {
			// Not possible - caught by the language compiler.
			System.err.println("Warning - illegal arguments in actor resumption.");
		} catch (IllegalAccessException iax) {
			// Not possible - all message invocations are on public methods.
			System.err.println("Warning - illegal access in actor resumption.");
		} catch (InvocationTargetException itx) {
			// TODO Remove this once an entry method crashes correctly.
			itx.getCause().printStackTrace();
			// Fails the message and moves on to the next one.
			failCurrentMessage(itx.getCause());
		}
	}

}
