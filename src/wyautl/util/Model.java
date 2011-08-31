package wyautl.util;

import java.util.*;

public class Model<T> {
	private final ArrayList<T> model;
	
	private Model(ArrayList<T> seedValues) {
		this.model = seedValues;
	}
	
	public int size() {
		return model.size();
	}
	
	public T get(int i) {
		return model.get(i);
	}
		
	/**
	 * An instance of Config determines the necessary parameters of the model.
	 * 
	 * @author djp
	 * 
	 */
	public static class KindInfo {
		/**
		 * Determine whether this kind is non-sequential or not.
		 */
		public boolean NOSEQUENTIAL;

		/**
		 * Determine minimum number of children this kind can have.
		 */
		public int MIN_CHILDREN;
		
		/**
		 * Determine maximum number of children this kind can have.
		 */
		public int MAX_CHILDREN;				
	}	
}
