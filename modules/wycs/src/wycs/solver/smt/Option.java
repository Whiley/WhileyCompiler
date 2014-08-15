package wycs.solver.smt;

/**
 * A utility class for some options provided by some of the solvers specified in {@link
 * wycs.solver.smt.Solver}. These options are used in a {@link wycs.solver.smt.Stmt.SetOption}
 * statement.
 *
 * @author Henry J. Wylde
 */
public final class Option {

    /**
     * Boolean. Sets whether the solver should print out "success" for each statement executed.
     */
    public static final String PRINT_SUCCESS = ":print-success";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should automatically
     * configurate itself.
     */
    public static final String Z3_AUTO_CONFIG = ":auto-config";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should automatically
     * configurate itself.
     */
    public static final String Z3_SMT_AUTO_CONFIG = ":smt.auto-config";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should pull nested
     * quantifiers out to the front of an expression.
     */
    public static final String Z3_PULL_NESTED_QUANTIFIERS = ":pull-nested-quantifiers";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should pull nested
     * quantifiers out to the front of an expression.
     */
    public static final String Z3_SMT_PULL_NESTED_QUANTIFIERS = ":smt.pull-nested-quantifiers";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should use its model based
     * quantifier instantiation method for resolution.
     */
    public static final String Z3_MBQI = ":mbqi";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should use its model based
     * quantifier instantiation method for resolution.
     */
    public static final String Z3_SMT_MBQI = ":smt.mbqi";

    /**
     * This class may not be instantiated.
     */
    private Option() {}
}
