package wyil.lang;

import static wyil.lang.Type.*;
import wyautl.lang.Automata;
import wyil.util.Pair;


public class CoerciveSubtypeOperator extends DefaultSubtypeOperator {
	
	public CoerciveSubtypeOperator(Automata fromAutomata, Automata toAutomata) {
		super(fromAutomata,toAutomata);
	}
	
	public boolean isRelated(int from, int to) {
		return super.isRelated(from, to);
		/*
		State fromNode = fromGraph[from];
		State toNode = toGraph[to];	
			
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
			return assumptions.isSubSet((Integer) fromNode.data,(Integer) toNode.data);
		} else if(fromNode.kind == K_DICTIONARY && toNode.kind == K_LIST) {
			Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) fromNode.data;
			return fromGraph[p1.first()].kind == K_INT
					&& assumptions.isSubSet(p1.second(),
							(Integer) toNode.data);
		} else if(fromNode.kind == K_LIST && toNode.kind == K_STRING) {
			Integer p1 = (Integer) fromNode.data;
			// TO DO: this is a bug here for cases when the element type is e.g. int|real
			return fromGraph[p1].kind == K_ANY || fromGraph[p1].kind == K_INT || fromGraph[p1].kind == K_RATIONAL;
		} else {
			return super.isSubType(from,to);
		}
		*/
	}	
}