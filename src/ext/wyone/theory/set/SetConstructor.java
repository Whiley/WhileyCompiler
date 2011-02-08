package wyone.theory.set;

import java.util.*;

import wyone.core.Constructor;
import wyone.core.Value;

public class SetConstructor extends Constructor.Base<Constructor> {
	public SetConstructor(Set<Constructor> items) {
		super("{}",items);
	}
	
	public Constructor substitute(Map<Constructor,Constructor> binding) {
		HashSet nitems = new HashSet();		
		boolean pchanged = false;
		boolean composite = true;
		for(Constructor p : subterms) {						
			Constructor np = p.substitute(binding);			
			composite &= np instanceof Value;			
			if(np != p) {						
				nitems.add(np);				
				pchanged=true;	
			} else { 
				nitems.add(p);
			}
		}		
		if(composite) {			
			return Value.V_SET(nitems);
		} else if(pchanged) {			
			return new SetConstructor(nitems);			
		} else {
			return this;
		}
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		for(Constructor v : subterms) {
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += v;
		}
		return r + "}";
	}
}
