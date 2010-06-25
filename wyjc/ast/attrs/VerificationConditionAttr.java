// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.ast.attrs;

import java.util.*;

import wyone.core.WEnvironment;
import wyone.core.WType;
import wyone.core.WVariable;
import wyone.theory.logic.WFormula;

/**
 * A vericiation condition attribute is used to store the verification condition
 * used to discharge a given check.
 * 
 * @author djp
 * 
 */
public final class VerificationConditionAttr implements Attribute {
	private final WEnvironment environment;
	private final WFormula condition;

	public VerificationConditionAttr(WFormula condition, WEnvironment environment) {
		this.condition = condition;
		this.environment = environment;
	}

	public WFormula condition() {
		return condition;
	}
	
	public WEnvironment environment() {
		return environment;
	}
	
	public String toString() {
		
		// first, construct reverse map
		HashMap<WType,Set<String>> renv = new HashMap();
		for(Map.Entry<String, WType> e : environment) {
			Set<String> vars = renv.get(e.getValue());
			if(vars == null) {
				vars = new HashSet<String>();
				renv.put(e.getValue(),vars);
			}
			vars.add(e.getKey());
		}
		
		String r = "";
		for(Map.Entry<WType, Set<String>> e : renv.entrySet()) {
			r += e.getKey() + " ";
			boolean firstTime=true;
			for(String v : e.getValue()) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			r += "; ";
		}
		return r + condition;
	}
}
