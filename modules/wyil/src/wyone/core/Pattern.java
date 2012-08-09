package wyone.core;

import java.util.ArrayList;
import java.util.Collection;

import wyone.util.Pair;
import wyone.util.SyntacticElement;

public class Pattern extends SyntacticElement.Impl {
	
	public static final Any T_ANY = new Any();	
		
	public static final class Any extends Pattern {
		private Any() {			
		}		
		public String toString() {
			return "*";
		}
	}
	
	public static final class Term  extends Pattern {		
		public final String name;
		public final ArrayList<Pair<Pattern,String>> params;

		private Term(String name, Collection<Pair<Pattern,String>> params) {			
			this.name = name;
			this.params = new ArrayList<Pair<Pattern,String>>(params);
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
}
