package wycs.solver.smt;

/**
 * Utility class for constants representing the result of a SMT solver.
 *
 * @author Henry J. Wylde
 */
public final class Result {

    /**
     * Result is satisfiable; there is one or more models that make the assertions true.
     */
    public static final String SAT = "sat";
    /**
     * Result is unsatisfiable: there is one or more models that make the assertions false.
     */
    public static final String UNSAT = "unsat";
    /**
     * Result is unknown: may have timed out or was unable to come to a conclusion about the
     * assertions.
     */
    public static final String UNKNOWN = "unknown";

    /**
     * This class cannot be instantiated.
     */
    private Result() {}
}
