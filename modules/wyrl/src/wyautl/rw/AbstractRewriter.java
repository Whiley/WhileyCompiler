// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyautl.rw;

import java.io.IOException;
import java.util.Comparator;

import wyautl.core.Automata;
import wyautl.core.Automaton;
import wyautl.core.Schema;
import wyautl.core.Automaton.State;
import wyautl.io.PrettyAutomataWriter;
import wyautl.rw.Rewriter.Stats;

public abstract class AbstractRewriter implements Rewriter {

	/**
	 * The schema used by automata being reduced. This is primarily useful for
	 * debugging purposes.
	 */
	protected final Schema schema;

	/**
	 * Used to count the number of unsuccessful inferences (i.e. those
	 * successful inference rule activations which did not result in a
	 * changed automaton after reduction). 
	 */
	private int numInferenceFailures;

	/**
	 * Used to count the number of successful inferences (i.e. those
	 * successful inference rule activations which did result in a
	 * changed automaton after reduction). 
	 */
	private int numInferenceSuccesses;
	
	/**
	 * Used to count the total number of activations made for inference
	 * rules. 
	 */
	private int numInferenceActivations;

	/**
	 * Used to count the number of unsuccessful reductions (i.e. those
	 * successful reduction rule activations which did not result in a
	 * changed automaton after reduction). 
	 */
	private int numReductionFailures;

	/**
	 * Used to count the number of successful reductions (i.e. those
	 * successful reduction rule activations which did result in a
	 * changed automaton after reduction). 
	 */
	private int numReductionSuccesses;
	
	/**
	 * Used to count the total number of activations made for reduction
	 * rules. 
	 */
	private int numReductionActivations;
	
	/**
	 * Counts the total number of activation probes, including those which
	 * didn't generate activations.
	 */
	protected int numProbes;

	/**
	 * Temporary space used for the various automata operations.
	 */
	private int[] tmp = new int[0];
	
	public AbstractRewriter(Schema schema) {
		this.schema = schema;		
	}	
		
	@Override
	public Rewriter.Stats getStats() {
		return new Stats(numProbes, numReductionActivations,
				numReductionFailures, numReductionSuccesses,
				numInferenceActivations, numInferenceFailures,
				numInferenceSuccesses);
	}

	@Override
	public void resetStats() {
		this.numProbes = 0;
		this.numReductionActivations = 0;		
		this.numReductionFailures = 0;
		this.numReductionSuccesses = 0;
		this.numInferenceActivations = 0;		
		this.numInferenceFailures = 0;
		this.numInferenceSuccesses = 0;
	}
	
	/**
	 * This method should be used to apply a given activation of an inference
	 * rule onto an automaton during rewriting.
	 * 
	 * @param automaton
	 *            The automaton being reduced.
	 * @param activation
	 *            The inference rule activation to be applied.
	 * @returns True if the activation was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean applyInference(Automaton automaton, Activation activation) {
		int nStates = automaton.nStates();
		
		// First, attempt to apply the inference rule
		// activation.								
		numInferenceActivations++;
		
		if (activation.apply(automaton)) {

			// Yes, the inference rule was applied; now we must
			// try and reduce the automaton as much as possible to check whether
			// any new information was actually generated or not. If we end up
			// with the original automaton, then no new information was
			// inferred.

			if(doPartialReduction(automaton, nStates)) {

				// In this case, the automaton has changed state
				// and, therefore, all existing activations must
				// be invalidated. To do this, we break out of
				// the outer for-loop and restart the inference
				// process from scratch
				numInferenceSuccesses++;
				//System.out.println("APPLIED: " + activation.rule.getClass().getName());
				return true;

			} else {
				
				// In this case, the automaton has not changed
				// state after reduction and, therefore, we
				// consider this activation to have failed.								
				numInferenceFailures++;
			}
		} else {	

			// In this case, the activation failed so we simply
			// continue on to try another activation.
		}
		
		return false;
	}

	/**
	 * <p>
	 * Reduce the upper states of a given automaton as much as possible. The
	 * pivot point indicates the portion of the automaton which is "new" (i.e.
	 * above the pivot) versus that which is "old" (i.e. below the pivot).
	 * States above the pivot are those which need to be reduced, whilst those
	 * below the pivot are considered to be already fully reduced (and therefore
	 * do not need further reducing).
	 * </p>
	 * 
	 * <p>
	 * This function is used during the application of an inference rule. An
	 * important aspect of this is that the function must indicate whether or
	 * not <i>the original automaton was left after reduction</i>. That is when,
	 * after reduction, all states above the <code>pivot</code> have been
	 * eliminated, but no state below the pivot has. This indicates that the new
	 * states introduced by the inference rule were reduced away leaving an
	 * automaton identical to before the rule was applied. When this happens,
	 * the inference rule has not been successfully applied and we should
	 * continue to search for other rules which can be applied.
	 * </p>
	 * 
	 * <p>
	 * The generally accepted strategy for checking whether the original
	 * automaton remains is as follows: firstly, reductions are only applied to
	 * states above the pivot point (which includes the pivot index itself);
	 * secondly, after a reduction is successfully applied all unreachable
	 * states above the pivot are eliminated (to prevent against the continued
	 * reapplication of a reduction rule); thirdly, when the fixed-point is
	 * reached, the automaton is fully compacted. If during the final
	 * compaction, any state below the pivot becomes unreachable, then the
	 * original automaton was not retained; likewise, if after compaction the
	 * number of states exceeds the pivot, then it was not retained either.
	 * </p>
	 * 
	 * @param automaton
	 *            The automaton to be reduced.
	 * @param pivot
	 *            The pivot point for the partial reduction. All states above
	 *            this (including the pivot index itself) are eligible for
	 *            reduction; all those below are not.
	 * @return True if the original automaton was not retained (i.e. if some new
	 *         information has been generated).
	 */
	protected abstract boolean doPartialReduction(Automaton automaton, int pivot);
	
	/**
	 * This method should be used to apply a given reduce activation onto an
	 * automaton during a partial reduction.
	 * 
	 * @param automaton
	 *            The automaton being reduced.
	 * @param pivot
	 *            The pivot point for the partial reduction. All states above
	 *            this (including the pivot index itself) are eligible for
	 *            reduction; all those below are not.
	 * @param activation
	 *            The reduction rule activation to be applied.
	 * @returns True if the activation was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean applyPartialReduction(Automaton automaton, int pivot, Activation activation) {
		numReductionActivations++;
		
		if(activation.apply(automaton)) {
			
			// We need to eliminate any states added during the activation which
			// have become unreachable. This is because if such states remain in
			// the automaton, then they will cause an infinite loop of
			// re-activations. More specifically, where we activate on a state
			// and rewrite it, but then it remains and so we repeat.								

			if (automaton.nStates() > tmp.length) {
				tmp = new int[automaton.nStates() * 2];
			}

			Automata.eliminateUnreachableStates(automaton, pivot,
					automaton.nStates(), tmp);
//			System.out.println("========================");
//			System.out.println("APPLIED: " + activation.rule.getClass().getName());
//			try {
//			new PrettyAutomataWriter(System.err, SCHEMA, "And",
//					"Or").write(automaton);
//			} catch(IOException e) {}
//			System.out.println("\n");
			numReductionSuccesses++;
			return true;
		} else {
			
			// In this case, the activation failed so we simply
			// continue on to try another activation. 							
			numReductionFailures++;
			
			return false;
		}
	}
	
	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
			// $4<NotT($2<^Type>)>
			Schema.Term("NotT",Schema.Or(Schema.Any, Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.String))), Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any)))),
			// $7<AndT($5<^{$2<^Type>...}>)>
			Schema.Term("AndT",Schema.Set(true)),
			// $7<OrT($5<^{$2<^Type>...}>)>
			Schema.Term("OrT",Schema.Set(true)),
			// $7<TupleT(^[$2<^Type>...])>
			Schema.Term("TupleT",Schema.List(true)),
			// $9<SetT(^[^bool,$3<^Type>])>
			Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Or(Schema.Any, Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.String))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))))),
			// AnyT
			Schema.Term("AnyT"),
			// VoidT
			Schema.Term("VoidT"),
			// BoolT
			Schema.Term("BoolT"),
			// IntT
			Schema.Term("IntT"),
			// RealT
			Schema.Term("RealT"),
			// StringT
			Schema.Term("StringT"),
			// VarT(^string)
			Schema.Term("VarT",Schema.String),
			// $8<FunctionT(^[$2<^Type>,$2,$2...])>
			Schema.Term("FunctionT",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.String))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true))),Schema.Any)),
			// True
			Schema.Term("True"),
			// False
			Schema.Term("False"),
			// Num(^real)
			Schema.Term("Num",Schema.Real),
			// Var(^string)
			Schema.Term("Var",Schema.String),
			// $7<Tuple(^[$2<^Expr>...])>
			Schema.Term("Tuple",Schema.List(true)),
			// $9<Load(^[$2<^Expr>,^int])>
			Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Num",Schema.Real), Schema.Term("String",Schema.String)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true)), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Term("Var",Schema.Any)), Schema.Term("LengthOf",Schema.Any)), Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Or(Schema.Any, Schema.Any, Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any))))), Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true))),Schema.Any))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any)))), Schema.Any), Schema.Any),Schema.Int)),
			// $4<LengthOf($2<^Expr>)>
			Schema.Term("LengthOf",Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Num",Schema.Real), Schema.Term("String",Schema.String)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true)), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Any, Schema.Or(Schema.Any, Schema.Term("Var",Schema.Any), Schema.Term("Load",Schema.List(true,Schema.Any,Schema.Int)))), Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Or(Schema.Any, Schema.Any, Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any))))), Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true))),Schema.Any))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any)))), Schema.Any), Schema.Any)),
			// $9<Fn(^[^string,$3<^Expr>...])>
			Schema.Term("Fn",Schema.List(true,Schema.String)),
			// String(^string)
			Schema.Term("String",Schema.String),
			// $4<Not($2<^$28<BExpr>>)>
			Schema.Term("Not",Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Term("Var",Schema.String), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Or(Schema.Or(Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Num",Schema.Real), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true))), Schema.Or(Schema.Any, Schema.Any, Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true)))),Schema.Int))), Schema.Term("LengthOf",Schema.Any)), Schema.Any, Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Any)), Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Any,Schema.Any))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any)))),
			// $7<And($5<^{$2<^$31<BExpr>>...}>)>
			Schema.Term("And",Schema.Set(true)),
			// $7<Or($5<^{$2<^$31<BExpr>>...}>)>
			Schema.Term("Or",Schema.Set(true)),
			// $14<Equals(^[$2<^Type>,^{|$4<^Expr>,$4|}[$4<^Expr>,$4]])>
			Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.String))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Num",Schema.Real), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true)), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Or(Schema.Or(Schema.Any, Schema.Term("Var",Schema.Any), Schema.Term("Load",Schema.List(true,Schema.Any,Schema.Int))), Schema.Term("LengthOf",Schema.Any)), Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any)), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Or(Schema.Any, Schema.Any, Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any))))), Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true))),Schema.Any))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any)))), Schema.Any), Schema.Any),Schema.Any))),
			// $12<Mul($10<^[^real,^{|$3<^$20<AExpr>>...|}[$3<^$20<AExpr>>...]]>)>
			Schema.Term("Mul",Schema.List(true,Schema.Real,Schema.Bag(true))),
			// $8<Div(^[$2<^$16<AExpr>>,$2])>
			Schema.Term("Div",Schema.List(true,Schema.Or(Schema.Any, Schema.Term("Num",Schema.Real), Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Or(Schema.Or(Schema.Term("Var",Schema.String), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Any, Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Any)), Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true))),Schema.Any))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any))))), Schema.Any),Schema.Int))), Schema.Term("LengthOf",Schema.Any))),Schema.Any)),
			// $12<Sum($10<^[^real,^{|$3<^$20<AExpr>>...|}[$3<^$20<AExpr>>...]]>)>
			Schema.Term("Sum",Schema.List(true,Schema.Real,Schema.Bag(true))),
			// $10<Equation($8<^[^AType,$4<^$29<AExpr>>]>)>
			Schema.Term("Equation",Schema.List(true,Schema.Or(Schema.Term("IntT"), Schema.Term("RealT")),Schema.Or(Schema.Term("Num",Schema.Real), Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any)), Schema.Or(Schema.Or(Schema.Term("Var",Schema.String), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Any, Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Any, Schema.Any, Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Any, Schema.Term("Inequality",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true))),Schema.Any))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any))))), Schema.Any),Schema.Int))), Schema.Term("LengthOf",Schema.Any))))),
			// $10<Inequality($8<^[^AType,$4<^$29<AExpr>>]>)>
			Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Term("IntT"), Schema.Term("RealT")),Schema.Or(Schema.Term("Num",Schema.Real), Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any)), Schema.Or(Schema.Or(Schema.Term("Var",Schema.String), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Any, Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Any, Schema.Any, Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Any, Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true))),Schema.Any))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any))))), Schema.Any),Schema.Int))), Schema.Term("LengthOf",Schema.Any))))),
			// $7<Set(^{$2<^Expr>...})>
			Schema.Term("Set",Schema.Set(true)),
			// $10<SubsetEq(^[^$19<SetT(^[^bool,$13<^Type>])>,^$94<SExpr>,^$94])>
			Schema.Term("SubsetEq",Schema.List(true,Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Or(Schema.Any, Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.String))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))))),Schema.Or(Schema.Or(Schema.Or(Schema.Term("Var",Schema.Any), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Num",Schema.Real), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Or(Schema.Any, Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Or(Schema.Any, Schema.Any, Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any))))), Schema.Term("Equation",Schema.Any)))), Schema.Or(Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any)))), Schema.Any)),Schema.Int))), Schema.Term("LengthOf",Schema.Any)), Schema.Term("Set",Schema.Set(true))),Schema.Any)),
			// $19<ForAll($17<^[^{^[^Var(^string),$4<^Type>]...},$13<^$122<BExpr>>]>)>
			Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Term("Var",Schema.String), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Or(Schema.Or(Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Num",Schema.Real), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true))), Schema.Or(Schema.Any, Schema.Any, Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true)))),Schema.Int))), Schema.Term("LengthOf",Schema.Any)), Schema.Any, Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Any)), Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Any,Schema.Any))), Schema.Or(Schema.Any, Schema.Term("Exists",Schema.Any))))),
			// $19<Exists($17<^[^{^[^Var(^string),$4<^Type>]...},$13<^$122<BExpr>>]>)>
			Schema.Term("Exists",Schema.List(true,Schema.Set(true),Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Or(Schema.Term("Var",Schema.String), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Load",Schema.List(true,Schema.Or(Schema.Or(Schema.Or(Schema.Any, Schema.Any, Schema.Or(Schema.Or(Schema.Term("Tuple",Schema.List(true)), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Num",Schema.Real), Schema.Term("String",Schema.Any)), Schema.Term("Set",Schema.Set(true))), Schema.Term("Tuple",Schema.List(true))), Schema.Or(Schema.Any, Schema.Any, Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Any, Schema.Term("Set",Schema.Set(true)))),Schema.Int))), Schema.Term("LengthOf",Schema.Any)), Schema.Any, Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Equals",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("AnyT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("TupleT",Schema.List(true)), Schema.Term("VarT",Schema.Any))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))),Schema.Bag(true,Schema.Any,Schema.Any)))), Schema.Or(Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Any, Schema.Any),Schema.Any)), Schema.Term("Equation",Schema.Any))), Schema.Term("SubsetEq",Schema.List(true,Schema.Any,Schema.Any,Schema.Any))), Schema.Or(Schema.Any, Schema.Term("ForAll",Schema.Any)))))
		});
	
	/**
	 * Complete the final step of a partial reduction, where the automaton is
	 * compacted down. This operation checks to see whether any state below the
	 * pivot has been eliminated and/or whether there remain states above the
	 * pivot. If either of these hold, then the automaton is considered to have
	 * changed and this function returns <code>true</code>.
	 * 
	 * @param automaton
	 *            The automaton being reduced.
	 * @param pivot
	 *            The pivot point for the partial reduction. All states above
	 *            this (including the pivot index itself) were eligible for
	 *            reduction; all those below were not.
	 * @returns True if the reduction was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean completePartialReduction(Automaton automaton, int pivot) {
		
		// First, we eliminate all unreachable states from the automaton.
		int nStates = automaton.nStates();
		
		if (nStates > tmp.length) {
			tmp = new int[nStates * 2];
		}
		
		Automata.eliminateUnreachableStates(automaton, 0, nStates, tmp);	
		
		// Second, we compact the automaton down.
		
		boolean changed = false;
		int j=0;
		for(int i=0;i!=nStates;++i) {
			State ith = automaton.get(i);
			if(ith != null) {		
				tmp[i] = j;
				automaton.set(j++,ith);				
			} else if(i < pivot) {
				changed = true;
			}
		}
		
		// Third, trim and remap the automaton. Note, at this point, j holds the
		// number of states in the compacted automaton.
		
		automaton.resize(j);
		automaton.remap(tmp);
						
		return changed || j != pivot;
	}	
	
	/**
	 * A standard comparator for comparing rewrite rules. This favours minimum
	 * guarantees over maximum pay off. That is, a rule with a minimum / maximum
	 * guarantee of <code>1 / 1</code> will be favoured over a rule with a
	 * guarantee of <code>0 / 10</code>. The latter has a greater potential
	 * payoff, but a lower minimum payoff.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class MinRuleComparator<T extends RewriteRule>
			implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			int r1_minimum = o1.minimum();
			int r2_minimum = o2.minimum();
			if (r1_minimum > r2_minimum) {
				return -1;
			} else if (r1_minimum < r2_minimum) {
				return 1;
			}

			int r1_maximum = o1.maximum();
			int r2_maximum = o2.maximum();
			if (r1_maximum > r2_maximum) {
				return -1;
			} else if (r1_maximum < r2_maximum) {
				return 1;
			}

			return 0;
		}

	}

	/**
	 * A standard comparator for comparing rewrite rules. This favours maximum
	 * opportunity over guaranteed minimum pay off. That is, a rule with a
	 * minimum / maximum guarantee of <code>0 / 10</code> will be favoured over
	 * a rule with a guarantee of <code>0 / 1</code>. The former has a greater
	 * potential payoff, but a lower minimum payoff.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class MaxRuleComparator<T extends RewriteRule>
			implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			int r1_minimum = o1.minimum();
			int r2_minimum = o2.minimum();
			if (r1_minimum < r2_minimum) {
				return -1;
			} else if (r1_minimum > r2_minimum) {
				return 1;
			}

			int r1_maximum = o1.maximum();
			int r2_maximum = o2.maximum();
			if (r1_maximum < r2_maximum) {
				return -1;
			} else if (r1_maximum > r2_maximum) {
				return 1;
			}

			return 0;
		}

	}

	/**
	 * Signals that a limit on number of permitted probes has been reached. This
	 * is used simply to prevent rewriting from continuing for ever. In
	 * other words, it's a simple form of timeout.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	protected static final class MaxProbesReached extends RuntimeException {

	}	
}
