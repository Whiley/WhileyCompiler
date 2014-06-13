package wycs.solver.smt;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Smt2File {

    /**
     * A list of lines in this file. Uses a {@link java.util.LinkedHashSet} as the container to
     * remove any duplicate {@link wycs.solver.smt.Stmt.DeclareFun} and {@link
     * wycs.solver.smt.Stmt.DeclareSort}s. Doing this means we don't have to manually check for
     * their existence and ensures they are uniquely defined.
     */
    private final Set<Line> lines = new LinkedHashSet<Line>();

    public Smt2File() {}

    public void addLines(Line... lines) {
        addLines(Arrays.asList(lines));
    }

    public void addLines(Collection<Line> lines) {
        if (lines.contains(null)) {
            throw new NullPointerException("lines cannot contain null");
        }

        this.lines.addAll(lines);
    }

    public void clearLines() {
        lines.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Line line : lines) {
            sb.append(line);

            if (!(line instanceof Block)) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}

