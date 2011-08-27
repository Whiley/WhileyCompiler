package wyil.lang.type;

import static wyil.lang.type.Node.K_ANY;
import static wyil.lang.type.Node.K_CHAR;
import static wyil.lang.type.Node.K_DICTIONARY;
import static wyil.lang.type.Node.K_INT;
import static wyil.lang.type.Node.K_LIST;
import static wyil.lang.type.Node.K_RATIONAL;
import static wyil.lang.type.Node.K_SET;
import static wyil.lang.type.Node.K_STRING;
import wyil.util.Pair;


public class CoerciveSubtypeOperator extends DefaultSubtypeOperator {
	
	public CoerciveSubtypeOperator(Node[] fromGraph, Node[] toGraph) {
		super(fromGraph,toGraph);
	}
	
	public boolean isSubType(int from, int to) {
		Node fromNode = fromGraph[from];
		Node toNode = toGraph[to];	
		
		/*
		if(fromNode.kind == K_RECORD && toNode.kind == K_RECORD) {
			// labeled nary nodes
			Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) fromNode.data;
			Pair<String, Integer>[] _fields2 = (Pair<String, Integer>[]) toNode.data;				
			HashMap<String,Integer> fields2 = new HashMap<String,Integer>();
			for(Pair<String,Integer> f : _fields2) {
				fields2.put(f.first(), f.second());
			}
			for (int i = 0; i != fields1.length; ++i) {
				Pair<String, Integer> e1 = fields1[i];
				Integer e2 = fields2.get(e1.first());
				if (e2 == null || !assumptions.isSubtype(e1.second(),e2)) {
					return false;
				}
			}					
			return true;
		} else 
		*/
		if(fromNode.kind == K_CHAR && toNode.kind == K_INT) {
			// ints can flow into chars
			return true;
		} else if(fromNode.kind == K_INT && toNode.kind == K_CHAR) {
			// chars can flow into ints
			return true;
		} else if(fromNode.kind == K_RATIONAL && (toNode.kind == K_INT || toNode.kind == K_CHAR)) {
			// ints or chars can flow into rationals
			return true;
		} else if(fromNode.kind == K_SET && toNode.kind == K_LIST) {
			return assumptions.isSubtype((Integer) fromNode.data,(Integer) toNode.data);
		} else if(fromNode.kind == K_DICTIONARY && toNode.kind == K_LIST) {
			Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) fromNode.data;
			return fromGraph[p1.first()].kind == K_INT
					&& assumptions.isSubtype(p1.second(),
							(Integer) toNode.data);
		} else if(fromNode.kind == K_LIST && toNode.kind == K_STRING) {
			Integer p1 = (Integer) fromNode.data;
			// TO DO: this is a bug here for cases when the element type is e.g. int|real
			return fromGraph[p1].kind == K_ANY || fromGraph[p1].kind == K_INT || fromGraph[p1].kind == K_RATIONAL;
		} else {
			return super.isSubType(from,to);
		}
	}
	
	public boolean isSuperType(int from, int to) {
		Node fromNode = fromGraph[from];
		Node toNode = toGraph[to];	
		
		/*
		if(fromNode.kind == K_RECORD && toNode.kind == K_RECORD) {
			// labeled nary nodes
			Pair<String, Integer>[] _fields1 = (Pair<String, Integer>[]) fromNode.data;
			Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) toNode.data;				
			HashMap<String,Integer> fields1 = new HashMap<String,Integer>();
			for(Pair<String,Integer> f : _fields1) {
				fields1.put(f.first(), f.second());
			}
			for (int i = 0; i != fields2.length; ++i) {
				Pair<String, Integer> e2 = fields2[i];
				Integer e1 = fields1.get(e2.first());
				if (e1 == null || !assumptions.isSupertype(e1,e2.second())) {
					return false;
				}
			}					
			return true;					

		} else 
		*/	
		if(fromNode.kind == K_CHAR && (toNode.kind == K_RATIONAL || toNode.kind == K_INT)) {
			// char can flow into int or rational
			return true;
		} else if(fromNode.kind == K_INT && (toNode.kind == K_RATIONAL || toNode.kind == K_CHAR)) {
			// int can flow into rational or char
			return true;
		} else if(fromNode.kind == K_LIST && toNode.kind == K_SET) {
			return assumptions.isSupertype((Integer) fromNode.data,(Integer) toNode.data);
		} else if(fromNode.kind == K_LIST && toNode.kind == K_DICTIONARY) {
			Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) toNode.data;
			return toGraph[p2.first()].kind == K_INT
					&& assumptions.isSupertype((Integer)fromNode.data,p2.second());								
		} else if(fromNode.kind == K_STRING && toNode.kind == K_LIST) {
			Integer p2 = (Integer) toNode.data;
			// TO DO: this is a bug here for cases when the element type is e.g. int|real
			return toGraph[p2].kind == K_ANY || toGraph[p2].kind == K_INT || toGraph[p2].kind == K_RATIONAL;
		} else {
			return super.isSuperType(from, to);
		}
	}
}