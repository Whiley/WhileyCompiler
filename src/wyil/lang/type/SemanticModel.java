package wyil.lang.type;

import java.math.*;
import java.util.*;

import org.apache.tools.ant.taskdefs.ManifestTask.Mode;

import wyil.lang.*;

/**
 * <p>
 * The <i>semantic model</i> is used to generate sets of values characterising
 * types in Whiley. Essentially, the model corresponds to a large set of
 * possible Whiley values. Then, for a given type, we can identify the subset
 * that it accepts. In this way, we can compare two types to determine whether
 * (under the model) one is a subtype of another.
 * </p>
 * 
 * <p>
 * <b>Note:</b>Most types (e.g. [int]) correspond to infinite sets, but the
 * model must be finite. Therfore, various parameters are provided for limiting
 * the model size.
 * </p>
 * 
 * @author djp
 * 
 */
public final class SemanticModel {
	private final ArrayList<Value> model;
	
	private SemanticModel(ArrayList<Value> values) {
		this.model = values;
	}
	
	public int size() {
		return model.size();
	}
	
	public Value get(int i) {
		return model.get(i);
	}
	
	public BitSet match(Type t) {
		BitSet r = new BitSet(model.size());
		for(int i=0;i!=model.size();++i) {
			Value v = model.get(i);
			if(match(t,v)) {
				r.set(i);
			}
		}
		return r;
	}
	
	private static boolean match(Type t, Value v) {
		if(t instanceof Type.Any) {
			return true;
		} else if(t instanceof Type.Void) {
			return false;
		} else if(t instanceof Type.Byte && v instanceof Value.Byte) {
			return true;
		} else if(t instanceof Type.Char && v instanceof Value.Char) {
			return true;
		} else if(t instanceof Type.Int && v instanceof Value.Integer) {
			return true;
		} else if(t instanceof Type.Real && v instanceof Value.Rational) {
			return true;
		} else if(t instanceof Type.Strung && v instanceof Value.Strung) {
			return true;
		} else if(t instanceof Type.Tuple && v instanceof Value.Tuple) {
			Type.Tuple tt = (Type.Tuple) t;
			Value.Tuple vt = (Value.Tuple) v;
			List<Type> tt_elems = tt.elements();
			List<Value> vt_elems = vt.values;
			
			if(tt_elems.size() != vt_elems.size()) {
				return false;
			}
			
			for(int i=0;i!=tt_elems.size();++i) {
				Type tt_elem = tt_elems.get(i);
				Value vt_elem = vt_elems.get(i);
				if(!match(tt_elem,vt_elem)) {
					return false;
				}
			}
			
			return true;
		} else if(t instanceof Type.Set && v instanceof Value.Set) {		
			Type.Set ts = (Type.Set) t;
			Type ts_elem = ts.element();
			Value.Set vs = (Value.Set) v;
			
			for(Value vs_value : vs.values) {
				if(!match(ts_elem,vs_value)) {
					return false;
				}
			}
			
			return true;
		} else if(t instanceof Type.List && v instanceof Value.List) {
			Type.List tl = (Type.List) t;
			Type tl_elem = tl.element();
			Value.List vl = (Value.List) v;
			
			for(Value vl_value : vl.values) {
				if(!match(tl_elem,vl_value)) {
					return false;
				}
			}
			
			return true;
		}  else if(t instanceof Type.Dictionary && v instanceof Value.Dictionary) {
			Type.Dictionary td = (Type.Dictionary) t;
			Type td_key = td.key();
			Type td_value = td.value();
			Value.Dictionary vd = (Value.Dictionary) v;
			
			for(Map.Entry<Value,Value> ve : vd.values.entrySet()) {
				Value ve_key = ve.getKey();
				Value ve_value = ve.getValue();
				if(!match(td_key,ve_key) || !match(td_value,ve_value)) {
					return false;
				}
			}
			
			return true;
		} 
		
		return false;
		
		
	}
	
	/**
	 * An instance of Config determines the necessary parameters of the model.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Config {
		/**
		 * The MAX_DEPTH parameter determines the maximum depth of any value.
		 */
		public int MAX_DEPTH;
		
		/**
		 * The MIN_INT parameter determines the minimum integer value considered.
		 */
		public int MIN_INT;
		
		/**
		 * The MAX_INT parameter determines the maximum integer value considered.
		 */
		public int MAX_INT;
		
		/**
		 * The MAX_LIST parameter determines the maximum size of a list.
		 */
		public int MAX_LIST;
		
		/**
		 * The MAX_SET parameter determines the maximum size of a set.
		 */
		public int MAX_SET;
		
		/**
		 * The MAX_ELEMS parameter determines the maximum number of elements in
		 * a tuple.
		 */
		public int MAX_ELEMS;
		
		/**
		 * The MAX_FIELDS parameter determines the maximum number of fields in a
		 * record.
		 */
		public int MAX_FIELDS;
		
	}
	
	public static SemanticModel generate(Config config) {
		ArrayList<Value> model = new ArrayList<Value>();
		
		model.add(Value.V_NULL);
		model.add(Value.V_BOOL(true));
		model.add(Value.V_BOOL(false));
		addIntValues(config.MIN_INT,config.MAX_INT,model);
		
		return new SemanticModel(model);
	}
	
	public static void addIntValues(int min, int max, ArrayList<Value> model) {
		for(int i=min;i<max;++i) {
			model.add(Value.V_INTEGER(BigInteger.valueOf(i)));
		}
	}
	
	public static void main(String[] args) {
		Config config = new Config();
		config.MAX_DEPTH = 2;
		config.MIN_INT = -7;
		config.MAX_INT = 7;
		config.MAX_LIST = 2;
		config.MAX_SET = 2;
		config.MAX_ELEMS = 2;
		config.MAX_FIELDS = 2;
		
		SemanticModel model = generate(config);
		
		for(int i=0;i!=model.size();++i) {
			System.out.println(model.get(i));
		}
		System.out.println("Generated " + model.size() + " values.");
	}
}
