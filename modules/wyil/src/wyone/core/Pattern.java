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
	
	public HashMap<String,int[]> routes() {
		HashMap<String,int[]> env = new HashMap<String,int[]>();
		buildRoutes(new ArrayList<Integer>(),env);
		return env;
	}	
	
	protected abstract void buildRoutes(ArrayList<Integer> route, HashMap<String,int[]> environment);	
			
	public static final class Leaf extends Pattern {
		public Type.Reference type;
		
		public Leaf(Type.Reference type) {
			this.type = type;
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
		public final boolean unbound;
		public final boolean sequential;

		public Term(String name, boolean sequential,
				Collection<Pair<Pattern, String>> params, boolean unbound,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.params = new ArrayList<Pair<Pattern, String>>(params);
			this.unbound = unbound;
			this.sequential = sequential;
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
		
		public Pattern route(int route) {
			return params.get(route).first();
		}
		
		public boolean isUnbounded(int child) {
			return unbound && child + 1 == params.size();
		}
		
		public String toString() {			
			if(params.isEmpty()) {
				return name;
			} else {
				String r = name;
				if(sequential) {
					r += "(";
				} else {
					r += "{";
				}
				boolean firstTime=true;
				for(int i=0;i!=params.size();++i) {
					Pair<Pattern,String> p = params.get(i);
					if(!firstTime) {
						r += ",";
					}
					firstTime=false;
					String var = p.second();					
					r += p.first();
					
					if(unbound) {
						r += "...";
					}
					if(var != null) {
						r += " " + var;
					} 				
				}
				if(sequential) {
					r += ")";
				} else {
					r += "}";
				}
				return r;
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
