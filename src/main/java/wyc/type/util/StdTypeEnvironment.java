package wyc.type.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import wyc.type.TypeInferer.Environment;
import static wyc.lang.WhileyFile.*;

/**
 * Provides a very simple typing environment which defaults to using the
 * declared type for a variable (this is the "null" case). However, the
 * environment can also be updated to override the declared type with a new type
 * as appropriate.
 *
 * @author David J. Pearce
 *
 */
public class StdTypeEnvironment implements Environment {
	private final Map<Decl.Variable,Type> refinements;

	public StdTypeEnvironment() {
		this.refinements = new HashMap<>();
	}

	public StdTypeEnvironment(Map<Decl.Variable,Type> refinements) {
		this.refinements = new HashMap<>(refinements);
	}

	@Override
	public Type getType(Decl.Variable var) {
		Type refined = refinements.get(var);
		if(refined != null) {
			return refined;
		} else {
			return var.getType();
		}
	}

	@Override
	public Environment refineType(Decl.Variable var, Type refinement) {
		Type type = intersect(getType(var),refinement);
		StdTypeEnvironment r = new StdTypeEnvironment(this.refinements);
		r.refinements.put(var,type);
		return r;
	}

	@Override
	public Set<Decl.Variable> getRefinedVariables() {
		return refinements.keySet();
	}

	@Override
	public String toString() {
		String r = "{";
		boolean firstTime = true;
		for (Decl.Variable var : refinements.keySet()) {
			if (!firstTime) {
				r += ", ";
			}
			firstTime = false;
			r += var.getName() + "->" + getType(var);
		}
		return r + "}";
	}

	private Type intersect(Type left, Type right) {
		// FIXME: a more comprehensive simplification strategy would make sense
		// here.
		if(left == right || left.equals(right)) {
			return left;
		} else {
			return new Type.Intersection(new Type[]{left,right});
		}
	}
}
