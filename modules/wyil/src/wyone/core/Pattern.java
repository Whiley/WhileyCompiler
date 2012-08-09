package wyone.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wyone.util.Pair;
import wyone.util.SyntacticElement;

public abstract class Pattern extends SyntacticElement.Impl {
	
	public Pattern(Attribute... attributes) {
		super(attributes);
	}
	
	public HashMap<String,Type> environment() {
		HashMap<String,Type> env = new HashMap<String,Type>();
		buildEnvironment(env);
		return env;
	}
	
	public HashMap<String,int[]> routes() {
		HashMap<String,int[]> env = new HashMap<String,int[]>();
		buildRoutes(new ArrayList<Integer>(),env);
		return env;
	}
	
	protected abstract void buildRoutes(ArrayList<Integer> route, HashMap<String,int[]> environment);	
	protected abstract void buildEnvironment(HashMap<String,Type> environment);
	
	
	public abstract Type.Reference type();
	
	public static final class Leaf extends Pattern {
		public final Type.Reference type;
		
		public Leaf(Type.Reference type) {
			this.type = type;
		}
		public Type.Reference type() {
			return type;
		}
		protected void buildEnvironment(HashMap<String,Type> environment) {
			
		}

		protected void buildRoutes(ArrayList<Integer> route,
				HashMap<String, int[]> environment) {

		}
		public String toString() {
			return type.toString();
		}		
	}
	
	public static final class Term  extends Pattern {		
		public final String name;
		public final ArrayList<Pair<Pattern,String>> params;

		public Term(String name, Collection<Pair<Pattern, String>> params,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.params = new ArrayList<Pair<Pattern, String>>(params);
		}
		
		protected void buildRoutes(ArrayList<Integer> route,
				HashMap<String, int[]> environment) {
			int i=0;
			for(Pair<Pattern,String> p : params) {
				Pattern pattern = p.first();
				String var = p.second();
				route.add(i);
				pattern.buildRoutes(route, environment);
				if(var != null) {
					environment.put(var, toIntArray(route));
				}
				route.remove(route.size()-1);
				i=i+1;
			}
		}
		
		protected void buildEnvironment(HashMap<String,Type> environment) {
			for(Pair<Pattern,String> p : params) {
				Pattern pattern = p.first();
				pattern.buildEnvironment(environment);
				String var = p.second();
				if(var != null) {
					environment.put(var,pattern.type());
				}
			}
		}
		
		public Type.Reference type() {
			Type.Reference[] ps = new Type.Reference[params.size()];
			for (int i = 0; i != ps.length; ++i) {
				ps[i] = params.get(i).first().type();
			}
			return Type.T_TERM(name, false, ps);
		}
		
		public String toString() {			
			if(params.isEmpty()) {
				return name;
			} else {
				String r = name + "(";
				boolean firstTime=true;
				for(Pair<Pattern,String> p : params) {
					if(!firstTime) {
						r += ",";
					}
					firstTime=false;
					String var = p.second();
					if(var != null) {
						r += p.first() + " " + var;
					} else {
						r += p.first();
					}
				}
				return r + ")";
			}			
		}
	}
	
	private static int[] toIntArray(ArrayList<Integer> items) {
		int[] r = new int[items.size()];
		for(int i=0;i!=r.length;++i) {
			r[i] = items.get(i);
		}
		return r;
	}
}
