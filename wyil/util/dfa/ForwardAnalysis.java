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

package wyil.util.dfa;

import java.util.*;
import wyil.lang.*;

public abstract class ForwardAnalysis<T extends FlowSet> {
		
	protected final HashMap<Integer, T> stores = new HashMap<Integer, T>();	
	protected final HashMap<String,Integer> labels = new HashMap(); 
	
	/**
	 * Begins the Forward Analysis traversal of a method
	 */	
	public void start(Module.Case method, T startStore, T emptyStore) {	
		stores.clear(); // need to reset for subsequent calls
		labels.clear();
		
		Block body = method.body();
		
		// First, build the label map
		int pos = 0;
		for(Stmt s : body) {
			if(s.code instanceof Code.Label) {
				Code.Label lab = (Code.Label) s.code;				
				labels.put(lab.label,pos);
			}
			pos++;			
		}
		
		// Second, initialise the worklist
		HashSet<Integer> worklist = new HashSet();
		stores.put(0, startStore);
		worklist.add(0);
		
		// third, iterate until a fixed point is reached!
		while(!worklist.isEmpty()) {
			Integer current = select(worklist);
			
			if(current == body.size()) {
				continue;
			}
			

			Stmt stmt = body.get(current);
			Code code = stmt.code;
			
			// prestore represents store going into this point
			T store = stores.get(current);

			if(store == null) {
				store = emptyStore;
			}

			if(code instanceof Code.Goto) {
				Code.Goto gto = (Code.Goto) code;
				int target = labels.get(gto.target);
				merge(target,store,worklist);
			} else if(code instanceof Code.IfGoto) {				
				Code.IfGoto gto = (Code.IfGoto) code;
				int target = labels.get(gto.target);
				T t_store = transfer(true,stmt,store);
				T f_store = transfer(false,stmt,store);				
				merge(target,t_store,worklist);
				merge(current+1,f_store,worklist);
			} else if(code instanceof Code.Return) {
				// collect the final store as the one at the end of the list
				merge(body.size(),transfer(stmt,store),worklist);			
			} else if(!(code instanceof Code.Label)){				
				merge(current+1,transfer(stmt,store),worklist);
			} else if(code instanceof Code.Label) {
				merge(current+1,store,worklist);
			}			
		}
	}	
	
	/**
	 * Merges a FlowSet with a certain Point and adds it to the worklist
	 * if something changes
	 * 
	 * @param p Destination Point for merge
	 * @param m FlowSet to merge into point
	 */
	private void merge(Integer p, T m, HashSet<Integer> worklist) {		
		T tmp = stores.get(p);		
		if(tmp != null) {
			T ntmp = (T) tmp.join(m);
			if(ntmp != tmp) {
				stores.put(p, ntmp);				
				worklist.add(p); 
			}			
		} else {
			stores.put(p, m);
			worklist.add(p);
		}
	}
	
	/**
	 * Selects the next Point from the worklist
	 * 
	 * @return Next Point from worklist, or null if worklist is empty
	 */
	private Integer select(HashSet<Integer> worklist) {
		if(!worklist.isEmpty()) {
			Integer p = worklist.iterator().next();
			worklist.remove(p);
			return p;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Transfer function for Code Statements
	 * 
	 * @param e Expr to evaluate
	 * @param m FlowSet to use in evaluation
	 */
	
	public abstract T transfer(Stmt stmt, T m);
	
	/**
	 * Transfer function for Code Expressions.  This is used primarily for
	 * evaluating conditional expressions.
	 * 
	 * @param branch side this transfer is for
	 * @param gto condition branch
	 * @param m FlowSet to use in evaluation
	 */
	public abstract T transfer(boolean branch, Stmt gto, T m);

}
