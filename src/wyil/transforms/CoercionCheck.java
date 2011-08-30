package wyil.transforms;

import java.util.*;
import static wyil.util.SyntaxError.syntaxError;
import wyil.*;
import wyil.lang.*;
import wyil.util.*;
import wyts.lang.Automata;

/**
 * <p>
 * The point of the coercion check is to check that all convert bytecodes make
 * sense, and are not ambiguous. For example, consider the following code:
 * </p>
 * 
 * <pre>
 * define Rec1 as { real x, int y }
 * define Rec2 as { int x, real y }
 * define uRec1Rec2 as Rec1 | Rec2
 * 
 * int f(uRec1Rec2 r):
 *  if r is Rec1:
 *      return r.y
 *  else:
 *      return r.x
 * 
 * int g():
 *  rec = { x: 1, y: 1}
 *  return f(rec)
 * </pre>
 * 
 * <p>
 * An implicit coercion will be inserted just before the last statement in
 * <code>g()</code>. This will be:
 * </p>
 * 
 * <pre>
 * convert {int x,int y} => {real x,int y}|{int x,real y}
 * </pre>
 * <p>
 * However, this conversion is ambiguous. This is because we could convert the
 * left-hand side to either of the two options in the right-hand side.  
 * </p>
 * 
 * @author djp
 */
public class CoercionCheck implements Transform {
	private final ModuleLoader loader;
	private String filename;

	public CoercionCheck(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void apply(Module module) {
		filename = module.filename();
		
		for(Module.Method method : module.methods()) {
			check(method);
		}
	}
		
	public void check(Module.Method method) {				
		for (Module.Case c : method.cases()) {
			check(c.body(), method);
		}		
	}
	
	protected void check(Block block,  Module.Method method) {		
		for (int i = 0; i != block.size(); ++i) {
			Block.Entry stmt = block.get(i);
			Code code = stmt.code;
			if (code instanceof Code.Convert) {				
				Code.Convert conv = (Code.Convert) code; 				
				check(conv.from,conv.to,new HashSet<Pair<Automata,Automata>>(),stmt);
			} 
		}	
	}

	/**
	 * Recursively check that there is no ambiguity in coercing type from into
	 * type to. The visited set is necessary to ensure this process terminates
	 * in the presence of recursive types.
	 * 
	 * @param from
	 * @param to
	 * @param visited - the set of pairs already checked.
	 * @param elem - enclosing syntactic element.
	 */
	protected void check(Automata from, Automata to, HashSet<Pair<Automata, Automata>> visited,
			SyntacticElement elem) {
		Pair<Automata,Automata> p = new Pair<Automata,Automata>(from,to);
		if(visited.contains(p)) {
			return; // already checked this pair
		} else {
			visited.add(p);
		}
		if(from == Automata.T_VOID) {
			// also no problem
		} else if(from instanceof Automata.Leaf && to instanceof Automata.Leaf) {
			// no problem
		} else if(from instanceof Automata.Tuple && to instanceof Automata.Tuple) {
			Automata.Tuple t1 = (Automata.Tuple) from;
			Automata.Tuple t2 = (Automata.Tuple) to;
			List<Automata> t1_elements = t1.elements(); 
			List<Automata> t2_elements = t2.elements();
			for(int i=0;i!=t2.elements().size();++i) {
				Automata e1 = t1_elements.get(i);
				Automata e2 = t2_elements.get(i);
				check(e1,e2,visited,elem);
			}
		} else if(from instanceof Automata.Process && to instanceof Automata.Process) {
			Automata.Process t1 = (Automata.Process) from;
			Automata.Process t2 = (Automata.Process) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Automata.Set && to instanceof Automata.Set) {
			Automata.Set t1 = (Automata.Set) from;
			Automata.Set t2 = (Automata.Set) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Automata.Dictionary && to instanceof Automata.Set) {
			Automata.Dictionary t1 = (Automata.Dictionary) from;
			Automata.Set t2 = (Automata.Set) to;
			Automata.Tuple tup = Automata.T_TUPLE(t1.key(),t1.value());
			check(tup,t2.element(),visited,elem);
		} else if(from instanceof Automata.List && to instanceof Automata.Set) {
			Automata.List t1 = (Automata.List) from;
			Automata.Set t2 = (Automata.Set) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Automata.List && to instanceof Automata.Dictionary) {
			Automata.List t1 = (Automata.List) from;
			Automata.Dictionary t2 = (Automata.Dictionary) to;
			check(t1.element(),t2.value(),visited,elem);
		} else if(from instanceof Automata.List && to instanceof Automata.List) {
			Automata.List t1 = (Automata.List) from;
			Automata.List t2 = (Automata.List) to;
			check(t1.element(),t2.element(),visited,elem);
		} else if(from instanceof Automata.Strung && to instanceof Automata.List) {			
			Automata.List t2 = (Automata.List) to;
			check(Automata.T_CHAR,t2.element(),visited,elem);
		} else if(from instanceof Automata.Record && to instanceof Automata.Record) {
			Automata.Record t1 = (Automata.Record) from;
			Automata.Record t2 = (Automata.Record) to;
			HashMap<String,Automata> t1_elements = t1.fields(); 
			HashMap<String,Automata> t2_elements = t2.fields();
			ArrayList<String> fields = new ArrayList<String>(t2.keys());
			for(String s : fields) {
				Automata e1 = t1_elements.get(s);
				Automata e2 = t2_elements.get(s);
				check(e1,e2,visited,elem);
			}			
		} else if(from instanceof Automata.Fun && to instanceof Automata.Fun) {
			Automata.Fun t1 = (Automata.Fun) from;
			Automata.Fun t2 = (Automata.Fun) to;
			List<Automata> t1_elements = t1.params(); 
			List<Automata> t2_elements = t2.params();			
			for(int i=0;i!=t1_elements.size();++i) {
				Automata e1 = t1_elements.get(i);
				Automata e2 = t2_elements.get(i);
				check(e1,e2,visited,elem);
			}			
			check(t1.ret(),t2.ret(),visited,elem);
		} else if(from instanceof Automata.Union) {
			Automata.Union t1 = (Automata.Union) from; 
			for(Automata b : t1.bounds()) {
				check(b,to,visited,elem);
			}
		} else if(to instanceof Automata.Union) {			
			Automata.Union t2 = (Automata.Union) to;			
			
			// First, check for identical type (i.e. no coercion necessary)
			
			for(Automata b : t2.bounds()) {
				if(Automata.isomorphic(from, b)) {
					// no problem
					return;
				}
			}
			
			// Second, check for single non-coercive match
			Automata match = null;			
			
			for(Automata b : t2.bounds()) {
				if(Automata.isSubtype(b,from)) {
					if(match != null) {
						// found ambiguity
						syntaxError("ambiguous coercion (" + from + " => "
								+ to, filename, elem);
					} else {
						check(from,b,visited,elem);
						match = b;						
					}
				}
			}
			
			if(match != null) {
				// ok, we have a hit on a non-coercive subtype.
				return;
			}
			
			// Third, test for single coercive match
			
			for(Automata b : t2.bounds()) {
				if(Automata.isCoerciveSubtype(b,from)) {
					if(match != null) {
						// found ambiguity
						syntaxError("ambiguous coercion (" + from + " => "
								+ to, filename, elem);
					} else {
						check(from,b,visited,elem);
						match = b;						
					}
				}
			}
		}		
	}
}
