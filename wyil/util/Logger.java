package wyil.util;

public interface Logger {
	/**
	 * Log a message, along with a time. The time is used to indicate how long
	 * it took for the action being reported. This is used primarily to signal
	 * that a given stage has been completed in a certain amount of time.
	 * 
	 * @param msg
	 * @param time
	 */
	public void logTimedMessage(String msg, long time);
}
