package wyil.dfa;

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
		
		List<Code> body = method.body();
		
		// First, build the label map
		int pos = 0;
		for(Code s : body) {
			if(s instanceof Code.Label) {
				Code.Label lab = (Code.Label) s;				
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
			

			Code stmt = body.get(current);

			// prestore represents store going into this point
			T store = stores.get(current);

			if(store == null) {
				store = emptyStore;
			}

			if(stmt instanceof Code.Goto) {
				Code.Goto gto = (Code.Goto) stmt;
				int target = labels.get(gto.target);
				merge(target,store,worklist);
			} else if(stmt instanceof Code.IfGoto) {				
				Code.IfGoto gto = (Code.IfGoto) stmt;
				int target = labels.get(gto.target);
				T t_store = transfer(true,gto,store);
				T f_store = transfer(false,gto,store);				
				merge(target,t_store,worklist);
				merge(current+1,f_store,worklist);
			} else if(stmt instanceof Code.Return) {
				// collect the final store as the one at the end of the list
				merge(body.size(),transfer(stmt,store),worklist);			
			} else if(!(stmt instanceof Code.Label)){				
				merge(current+1,transfer(stmt,store),worklist);
			} else if(stmt instanceof Code.Label) {
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
	
	public abstract T transfer(Code stmt, T m);
	
	/**
	 * Transfer function for Code Expressions.  This is used primarily for
	 * evaluating conditional expressions.
	 * 
	 * @param branch side this transfer is for
	 * @param gto condition branch
	 * @param m FlowSet to use in evaluation
	 */
	public abstract T transfer(boolean branch, Code.IfGoto gto, T m);

}
