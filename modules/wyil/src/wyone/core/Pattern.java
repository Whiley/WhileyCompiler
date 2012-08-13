package wyone.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wyone.core.Type.Compound.Kind;
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
		public Type type;
		
		public Leaf(Type type) {
			this.type = type;
		}
		
		protected void buildRoutes(ArrayList<Integer> route,
				HashMap<String, int[]> environment) {

		}
		public String toString() {
			return type.toString();
		}		
	}
	
	public static final class Compound extends Pattern {
		public final Kind kind;
		public final ArrayList<Pair<Pattern, String>> elements;
		public final boolean unbounded;

		public Compound(Type.Compound.Kind kind, boolean unbound,
				Collection<Pair<Pattern, String>> params,
				Attribute... attributes) {
			super(attributes);
			this.kind = kind;
			this.elements = new ArrayList<Pair<Pattern, String>>(params);
			this.unbounded = unbound;			
		}
		
		protected void buildRoutes(ArrayList<Integer> route,
				HashMap<String, int[]> environment) {
			int i=0;
			for(Pair<Pattern,String> p : elements) {
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
			return elements.get(route).first();
		}
		
		public boolean isUnbounded(int child) {
			return unbounded && child + 1 == elements.size();
		}
	}
	
	public static final class Term extends Pattern {		
		public final String name;
		public final Pattern data;
		
		public Term(String name, Pattern data,
				Attribute... attributes) {
			super(attributes);
			this.name = name;	
			this.data = data;
		}
				
		protected void buildRoutes(ArrayList<Integer> route,
				HashMap<String, int[]> environment) {
			data.buildRoutes(route,environment);
		}
				
		public String toString() {			
			return name + " " + data;
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
