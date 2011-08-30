package wyts.lang;

import wyil.lang.NameID;
import static wyts.lang.Node.*;
import wyil.util.Pair;


public class DefaultSubtypeOperator extends SubtypeInference {
	public DefaultSubtypeOperator(Node[] fromGraph, Node[] toGraph) {
		super(fromGraph,toGraph);
	}
	
	public boolean isSubType(int from, int to) {
		Node fromNode = fromGraph[from];
		Node toNode = toGraph[to];	
		
		if(fromNode.kind == toNode.kind) { 
			switch(fromNode.kind) {
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) fromNode.data;
				NameID nid2 = (NameID) toNode.data;				
				return nid1.equals(nid2);
			case K_SET:
			case K_LIST:
			case K_PROCESS: {
				return assumptions.isSubtype((Integer) fromNode.data,(Integer) toNode.data);
			}
			case K_DICTIONARY: {
				// binary node
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) fromNode.data;
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) toNode.data;
				return assumptions.isSubtype(p1.first(),p2.first()) && assumptions.isSubtype(p1.second(),p2.second());  					
			}		
			case K_TUPLE:  {
				// nary nodes
				int[] elems1 = (int[]) fromNode.data;
				int[] elems2 = (int[]) toNode.data;
				if(elems1.length != elems2.length){ return false; }
				for(int i=0;i<elems1.length;++i) {
					if(!assumptions.isSubtype(elems1[i],elems2[i])) { return false; }
				}
				return true;
			}
			case K_METHOD:
			case K_FUNCTION:  {
				// nary nodes
				int[] elems1 = (int[]) fromNode.data;
				int[] elems2 = (int[]) toNode.data;
				if(elems1.length != elems2.length){
					return false;
				}
				// Check (optional) receiver value first (which is contravariant)
				int e1 = elems1[0];
				int e2 = elems2[0];
				if((e1 == -1 || e2 == -1) && e1 != e2) {
					return false;
				} else if (e1 != -1 && e2 != -1
						&& !assumptions.isSupertype(e1,e2)) {
					return false;
				}
				// Check return value first (which is covariant)
				e1 = elems1[1];
				e2 = elems2[1];
				if(!assumptions.isSubtype(e1,e2)) {
					return false;
				}
				// Now, check parameters (which are contra-variant)
				for(int i=2;i<elems1.length;++i) {
					e1 = elems1[i];
					e2 = elems2[i];
					if(!assumptions.isSupertype(e1,e2)) {
						return false;
					}
				}
				return true;
			}
			case K_RECORD:		
			{
				// labeled nary nodes
				Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) fromNode.data;
				Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) toNode.data;				
				if(fields1.length != fields2.length) {
					return false;
				}
				for (int i = 0; i != fields2.length; ++i) {
					Pair<String, Integer> e1 = fields1[i];
					Pair<String, Integer> e2 = fields2[i];						
						if (!e1.first().equals(e2.first())
								|| !assumptions.isSubtype(e1.second(),
										e2.second())) {
							return false;
						}
				}					
				return true;					
			} 		
			case K_UNION: {									
				int[] bounds2 = (int[]) toNode.data;		
				for(int j : bounds2) {				
					if(!assumptions.isSubtype(from,j)) { return false; }								
				}
				return true;					
			}
			case K_LABEL:
				throw new IllegalArgumentException("attempting to minimise open recurisve type");		
			default:
				// primitive types true immediately
				return true;
			}		
		} else if(fromNode.kind == K_ANY || toNode.kind == K_VOID) {
			return true;
		} else if(fromNode.kind == K_UNION) {
			int[] bounds1 = (int[]) fromNode.data;		

			// check every bound in c1 is a subtype of some bound in c2.
			for(int i : bounds1) {				
				if(assumptions.isSubtype(i,to)) {
					return true;
				}								
			}
			return false;	
		} else if(toNode.kind == K_UNION) {
			int[] bounds2 = (int[]) toNode.data;		

			// check some bound in c1 is a subtype of some bound in c2.
			for(int j : bounds2) {				
				if(!assumptions.isSubtype(from,j)) {
					return false;
				}								
			}
			return true;	
		}
		
		return false;
	}
	
	public boolean isSuperType(int from, int to) {
		Node fromNode = fromGraph[from];
		Node toNode = toGraph[to];	
		
		if(fromNode.kind == toNode.kind) { 
			switch(fromNode.kind) {
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) fromNode.data;
				NameID nid2 = (NameID) toNode.data;				
				return nid1.equals(nid2);
			case K_SET:
			case K_LIST:
			case K_PROCESS: {
				return assumptions.isSupertype((Integer) fromNode.data,(Integer) toNode.data);
			}
			case K_DICTIONARY: {
				// binary node
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) fromNode.data;
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) toNode.data;
				return assumptions.isSupertype(p1.first(),p2.first()) && assumptions.isSupertype(p1.second(),p2.second());  					
			}		
			case K_TUPLE:  {
				// nary nodes
				int[] elems1 = (int[]) fromNode.data;
				int[] elems2 = (int[]) toNode.data;
				if(elems1.length != elems2.length){ return false; }
				for(int i=0;i<elems1.length;++i) {
					if(!assumptions.isSupertype(elems1[i],elems2[i])) { return false; }
				}
				return true;
			}
			case K_METHOD:
			case K_FUNCTION:  {
				// nary nodes
				int[] elems1 = (int[]) fromNode.data;
				int[] elems2 = (int[]) toNode.data;
				if(elems1.length != elems2.length){
					return false;
				}
				// Check (optional) receiver value first (which is contravariant)
				int e1 = elems1[0];
				int e2 = elems2[0];
				if((e1 == -1 || e2 == -1) && e1 != e2) {
					return false;
				} else if (e1 != -1 && e2 != -1
						&& !assumptions.isSubtype(e1,e2)) {
					return false;
				}
				// Check return value first (which is covariant)
				e1 = elems1[1];
				e2 = elems2[1];
				if(!assumptions.isSupertype(e1,e2)) {
					return false;
				}
				// Now, check parameters (which are contra-variant)
				for(int i=2;i<elems1.length;++i) {
					e1 = elems1[i];
					e2 = elems2[i];
					if(!assumptions.isSubtype(e1,e2)) {
						return false;
					}
				}
				return true;
			}
			case K_RECORD:		
			{
				// labeled nary nodes
				Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) fromNode.data;
				Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) toNode.data;				
				if(fields1.length != fields2.length) {
					return false;
				}
				for (int i = 0; i != fields2.length; ++i) {
					Pair<String, Integer> e1 = fields1[i];
					Pair<String, Integer> e2 = fields2[i];						
						if (!e1.first().equals(e2.first())
								|| !assumptions.isSupertype(e1.second(),
										e2.second())) {
							return false;
						}
				}					
				return true;					
			} 		
			case K_UNION: {														
				int[] bounds1 = (int[]) toNode.data;		

				// check every bound in c1 is a subtype of some bound in toNode.
				for(int i : bounds1) {				
					if(!assumptions.isSupertype(i,to)) {
						return false;
					}								
				}
				return true;
			}									
			case K_LABEL:
				throw new IllegalArgumentException("attempting to minimise open recurisve type");		
			default:
				// primitive types true immediately
				return true;
			}		
		} else if(fromNode.kind == K_VOID || toNode.kind == K_ANY) {
			return true;
		} else if(fromNode.kind == K_UNION) {
			int[] bounds1 = (int[]) fromNode.data;		

			// check every bound in c1 is a subtype of some bound in c2.
			for(int i : bounds1) {				
				if(!assumptions.isSupertype(i,to)) {
					return false;
				}								
			}
			return true;	
		} else if(toNode.kind == K_UNION) {
			int[] bounds2 = (int[]) toNode.data;		

			// check some bound in c1 is a subtype of some bound in c2.
			for(int j : bounds2) {				
				if(assumptions.isSupertype(from,j)) {
					return true;
				}								
			}				
		}						
		
		return false;
	}
}
