package wyil.stages;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.stages.*;
import wyil.util.Pair;
import wyil.lang.Code.*;
import static wyil.util.SyntaxError.*;

/**
 * <p>
 * The purpose of the expected inference is to identify which the direction in
 * which we expect conditional branches will be taken. In this sense, it's a
 * little like branch prediction. In this case, however, we "expect" that
 * programs are correct and that branches that lead to immediate failure will
 * not be taken.
 * </p>
 * 
 * <p>
 * The reason for this analysis is to simplify the task of verification. Roughly
 * speaking, a branch that will not be taken corresponds to checking for
 * <i>unsatisfiability</i> of the corresponding verification condition (which the
 * theorem prover does quickly). In contrast, branches which can be taken
 * require the theorem prover to generate a model (which, currently the theorem
 * prover does poorly).
 * </p>
 * 
 * <p>
 * The analysis proceeds as a backward flow analysis starting from fail
 * statements.
 * </p>
 * 
 * @author djp
 * 
 */
public class ExpectedInference extends BackwardFlowAnalysis<Boolean> {
	
	public ExpectedInference(ModuleLoader loader) {
		super(loader);		
	}
		
	protected Pair<Stmt, Boolean> propagate(Code.IfGoto ifgoto, Stmt stmt,
			Boolean trueDefFail, Boolean falseDefFail) {
		if(falseDefFail || trueDefFail) {
			Attribute.BranchPredict e = new Attribute.BranchPredict(!trueDefFail,!falseDefFail);
			ArrayList<Attribute> attrs = new ArrayList<Attribute>(stmt.attributes());
			attrs.add(e);
			return new Pair<Stmt,Boolean>(new Stmt(ifgoto,attrs),trueDefFail && falseDefFail);
		} else {
			return new Pair<Stmt,Boolean>(stmt,false);
		}
	}

	protected Pair<Block, Boolean> propagate(Code.Start start, Code.End end,
			Block body, Stmt stmt, Boolean defFail) {
		Pair<Block,Boolean> r = propagate(body,defFail);
		
		Block b = new Block();
		b.add(start);
		b.addAll(r.first());
		b.add(end);
		
		// hmmm, following could be wrong?
		return new Pair<Block,Boolean>(b,false);
	}
	
	protected Pair<Stmt,Boolean> propagate(Stmt stmt, Boolean defFail) {
		if(stmt.code instanceof Code.Fail) {
			defFail = true;
		} else if(stmt.code instanceof Code.Return) {
			defFail = false;
		}
		
		return new Pair<Stmt,Boolean>(stmt,defFail);
	}

	protected Boolean join(Boolean defFail_1, Boolean defFail_2) {
		return defFail_1 && defFail_2;
		
	}
}
