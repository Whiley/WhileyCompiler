package wyil.jvm.rt;

import java.util.*;

public class WhileyProcess {
	private Object state;
	
	public WhileyProcess(Object c) {
		state = c;
	}

	public Object state() {
		return state;
	}
	
	public WhileyProcess clone() {
		return new WhileyProcess(this.state);
	}
	
	public String toString() {
		return state + "@" + System.identityHashCode(this);
	}
	
	public static WhileyProcess systemProcess() {
		// Not sure what the default value should be yet!!!
		return new WhileyProcess(null);
	}
}
