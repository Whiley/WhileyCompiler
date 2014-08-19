package wycs.solver.smt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumeration of the external solvers supported by the {@link wycs.transforms.SmtVerificationCheck}
 * transform.
 *
 * @author Henry J. Wylde
 */
public enum Solver {

    /**
     * CVC4 solver.
     * <p/>
     * Requires the "incremental" argument to allow usage of {@link wycs.solver.smt.Stmt.Push} and
     * {@link wycs.solver.smt.Stmt.Pop}.
     */
    CVC4("--incremental"),
    /**
     * Z3 solver.
     */
    Z3;

    /**
     * A list of extra arguments the solver should be called with.
     */
    private List<String> args;

    /**
     * Creates a new {@code Solver} with the given extra arguments.
     *
     * @param args the extra arguments.
     */
    private Solver(String... args) {
        this.args = Arrays.asList(args);
    }

    /**
     * Gets the extra arguments this solver should be called with.
     *
     * @return the extra arguments.
     */
    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }
}
