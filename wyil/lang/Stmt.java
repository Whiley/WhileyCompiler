package wyil.lang;

import java.util.*;
import wyil.util.*;

public final class Stmt extends SyntacticElement.Impl {
	public final Code code;
	
	public Stmt(Code code, Attribute... attributes) {
		super(attributes);
		this.code = code;
	}
	
	public Stmt(Code code, Collection<Attribute> attributes) {
		super(attributes);
		this.code = code;
	}
	
	public String toString() {
		String r = code.toString();
		if(attributes().size() > 0) {
			r += " # ";
			boolean firstTime=true;
			for(Attribute a : attributes()) {
				if(!firstTime) {
					r += ", ";
				}
				firstTime=false;
				r += a;
			}
		}
		return r;
	}
}
