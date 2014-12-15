package wyopcl.util;

import wyil.lang.Constant;
/**
 * This class stores all the information about Lambda function
 * @author mw169
 *
 */
public final class Closure extends Constant {
	public final Constant.Lambda lambda;
	public final Constant.Tuple params;
	public final Constant.Type type;
	
	public static Closure V_Closure(Constant.Lambda lambda, Constant.Tuple params, Constant.Type type) {
		return new Closure(lambda, params, type);
	}
	
	private Closure(Constant.Lambda lambda, Constant.Tuple parameters, Constant.Type returntype){
		this.lambda = lambda;
		this.params = parameters;
		this.type = returntype;
	}
	
	public Constant.Tuple parameters(){
		return params;
	}
	
	public Constant.Lambda lambda(){
		return lambda;
	}
	
	@Override
	public String toString() {
		return "Closure [lambda=" + lambda + ", parameters=" + params + ", returntype=" + type + "]";
	}


	@Override
	public int compareTo(Constant o) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public wyil.lang.Type type() {
		// TODO Auto-generated method stub
		return null;
	}
}
