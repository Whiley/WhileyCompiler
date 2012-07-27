// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.theory.quantifier;

import java.util.*;

import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;

public final class QuantifierInstantiation implements InferenceRule {

	public void infer(WFormula delta, SolverState state, Solver solver) {
		if (delta instanceof WForall) {
			infer((WForall) delta, state, solver);
		} else if (delta instanceof WLiteral) {
			infer((WLiteral) delta, state, solver);
		}
	}

	private static void infer(WForall f, SolverState state, Solver solver) {
		if (f.sign()) {
			ArrayList<WLiteral> triggers = findTriggers(f);
			HashMap<WVariable, WExpr> binding = new HashMap();
			instantiateQuantifier(f, 0, triggers, binding, state, solver);
		} else {
			instantiateExistential(f, state, solver);
		}
	}

	private static void infer(WLiteral l, SolverState state, Solver solver) {

		// FIXME: the following loop could definitely be optimised a *lot*
		for (WFormula _f : state) {
			if (_f instanceof WForall) {
				WForall f = (WForall) _f;

				ArrayList<WLiteral> triggers = findTriggers(f);

				for (int i = 0; i != triggers.size(); ++i) {
					WLiteral trigger = triggers.get(i);

					HashMap<WVariable, WExpr> binding = new HashMap<WVariable, WExpr>();
					HashMap<WVariable, WExpr> nbinding = bind(l,
							trigger, f.variables(), binding);

					if (nbinding != null) {
						ArrayList<WLiteral> ntrigs = new ArrayList<WLiteral>(
								triggers);
						ntrigs.remove(i);

						instantiateQuantifier(f, 0, ntrigs, nbinding, state,
								solver);
					}
				}
			}
		}
	}

	private static int skolem_idx = 0;
	private static void instantiateExistential(WForall f, SolverState state,
			Solver solver) {

		// FIXME: this is currently broken, since it could result in an infinte
		// number of skolem instantiations.

		// exists elimination via skolem constant
		HashMap<WExpr, WExpr> binding = new HashMap<WExpr, WExpr>();
		for (WVariable v : f.variables()) {
			String skolem = "#" + skolem_idx++;
			binding.put(v, new WVariable(skolem));
		}
		WFormula instantiation = state.reduce(f.formula().not().substitute(
				binding));
		state.infer(instantiation, solver);
	}

	/**
	 * The following does an exhaustive search of the trigger space for a given
	 * quantifier. This is an expensive operation, and could be significantly
	 * optimised.
	 * 
	 * @param f
	 *            --- the quanfier we're attempting to instantiate
	 * @param index
	 *            --- indicates the trigger we're up to
	 * @param triggers
	 *            --- the list of triggers be searched
	 * @param binding
	 *            --- the current binding
	 * @param delta
	 *            --- the list of new formulas
	 */
	private static void instantiateQuantifier(WForall f, int index,
			ArrayList<WLiteral> triggers,
			HashMap<WVariable, WExpr> binding, SolverState state,
			Solver solver) {	
		
		if (binding.keySet().size() == f.variables().size()) {
			// this indicates we have found a complete instantiation
			WFormula instantiation = f.formula().substitute((HashMap) binding);
			instantiation = state.reduce(instantiation);
			// System.out.println("INSTANTIATED: " + instantiation);
			state.infer(instantiation, solver);
		} else if (index < triggers.size()) {
			WLiteral trigger = triggers.get(index);

			for (WFormula _lit : state) {
				// I think we could do better here by matching against other
				// things, not just literals.
				if(_lit instanceof WLiteral) {					
					WLiteral lit = (WLiteral) _lit;
					HashMap<WVariable, WExpr> nbinding = bind(lit,
							trigger, f.variables(), binding);
					if (nbinding != null) {
						instantiateQuantifier(f, index + 1, triggers, nbinding,
								state, solver);
					}
				}
			}
		}
	}

	public static ArrayList<WLiteral> findTriggers(WForall f) {
		ArrayList<WLiteral> triggers = new ArrayList<WLiteral>();
		findTriggers(f.formula(), f.variables(), triggers);
		return triggers;
	}

	private static void findTriggers(WFormula f,
			Set<WVariable> variables, ArrayList<WLiteral> triggers) {
		if (f instanceof WDisjunct) {
			WDisjunct d = (WDisjunct) f;
			for (WFormula l : d.subterms()) {
				findTriggers(l, variables, triggers);
			}
		} else if (f instanceof WConjunct) {
			WConjunct d = (WConjunct) f;
			for (WFormula l : d.subterms()) {
				findTriggers(l, variables, triggers);
			}
		} else {
			WLiteral l = (WLiteral) f;
			Set<WVariable> lfvs = WExprs.match(WVariable.class,l);									
			for (WVariable v : variables) {
				if (lfvs.contains(v)) {
					triggers.add(l);
					return; // done with this formula
				}
			}
		}
	}

	/**
	 * <p>
	 * The purpose of the match method is to construct a binding between a
	 * "trigger" and a ground literal. A trigger is a parameterised literal.
	 * That is, a literal containing one or more variables which is to be
	 * instantiated. Thus, the binding generated binds a variable from the
	 * trigger to an object from the ground term. For example:
	 * </p>
	 * 
	 * <pre>
	 * 
	 *   trigger     |  ground     | variables 	 
	 *   ------------------------------- 	 
	 *   length(x,z) | length(y,z) | x
	 * </pre>
	 * 
	 * <p>
	 * Here, the variables column indicates which variables in the trigger are
	 * to be instantiated. In this case, the binding produced would be: {x->y}.
	 * </p>
	 * 
	 * <p>
	 * In some cases, it's impossible to create a valid binding. For example:
	 * </p>
	 * 
	 * <pre>
	 * 
	 *   trigger     |  ground     | variables 	 
	 *   ------------------------------- 	 
	 *   length(x,x) | length(y,z) | x
	 * </pre>
	 * 
	 * <p>
	 * There is no possible binding in the above, because this would require x
	 * to bind to both y and z.
	 * </p>
	 * 
	 * @param ground
	 *            - ground literal (receiver is trigger)
	 * @param binding
	 *            - binding being constructed
	 * @param variables
	 *            - set of variables to instantiate in trigger
	 * @return updated binding if successful match, null otherwise
	 */
	private static HashMap<WVariable, WExpr> bind(WLiteral ground,
			WLiteral trigger, Set<WVariable> variables,
			HashMap<WVariable, WExpr> binding) {

		// System.out.println("MATCHING: " + trigger + " AGAINST: " + ground + " BINDING: " + binding);
		
		if (trigger instanceof WEquality && ground instanceof WEquality) {
			// FIXME: this is a temporary measure, which means that we cannot
			// match upon equalities
			return binding;
		} else if (trigger instanceof WInequality
				&& ground instanceof WInequality) {
			// FIXME: this is a temporary measure, which means that we cannot
			// match upon inequalities
			return binding;
		} else if (trigger instanceof WPredicate
				&& ground instanceof WPredicate) {			
			WPredicate tpred = (WPredicate) trigger;
			WPredicate gpred = (WPredicate) ground;
							
			if (tpred.realName().equals(gpred.realName())
					&& tpred.subterms().size() == gpred.subterms().size()) {
			
				List<WExpr> tparams = tpred.subterms();
				List<WExpr> gparams = gpred.subterms();

				for (int i = 0; i != tparams.size(); ++i) {
					WExpr tp = tparams.get(i);
					WExpr gp = gparams.get(i);
					binding = bind(gp, tp, variables, binding);
					if (binding == null) {
						return null;
					}
				}
				
				//System.out.println("GOT BINDING: " + binding);
				
				return binding;
			}

		} else if (trigger instanceof WVariable) {
			WVariable v = (WVariable) trigger;
			if (variables.contains(v)) {
				WExpr b = binding.get(v);
				if (b == null) {
					HashMap<WVariable, WExpr> nbinding = new HashMap<WVariable, WExpr>(
							binding);
					nbinding.put(v, ground);
					return nbinding;
				} else {
					return binding;
				}
			} else if (v.equals(ground)) {
				return binding;
			}
		} 
		
		// this indicates a binding failure
		return null;
	}

	private static HashMap<WVariable, WExpr> bind(WExpr ground,
			WExpr trigger, Set<WVariable> variables,
			HashMap<WVariable, WExpr> binding) {

		if (variables.contains(trigger)) {			
			// for the moment, I only match single variable values ...
			WVariable tval = (WVariable) trigger;
			WExpr val = binding.get(tval);
			if (val == null) {
				HashMap<WVariable, WExpr> nbinding = new HashMap(
						binding);
				nbinding.put(tval, ground);
				return nbinding;
			} else if (val.equals(ground)) {
				return binding;
			}
		} else if (trigger instanceof WVariable
				&& ground instanceof WVariable) {			
			WVariable tpred = (WVariable) trigger;
			WVariable gpred = (WVariable) ground;
							
			if (tpred.name().equals(gpred.name())
					&& tpred.subterms().size() == gpred.subterms().size()) {
			
				List<WExpr> tparams = tpred.subterms();
				List<WExpr> gparams = gpred.subterms();

				for (int i = 0; i != tparams.size(); ++i) {
					WExpr tp = tparams.get(i);
					WExpr gp = gparams.get(i);
					binding = bind(gp, tp, variables, binding);
					if (binding == null) {
						return null;
					}
				}
				
				//System.out.println("GOT BINDING: " + binding);
				
				return binding;
			}

		} else if (ground.equals(trigger)) {
			// the binding is not updated, but at least we can continue.
			return binding;
		}

		return null;
	}
}
