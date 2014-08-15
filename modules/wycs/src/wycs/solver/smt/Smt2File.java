package wycs.solver.smt;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a <i>SMT</i> file. A SMT file is made up of a list of statements and may be verified
 * using a SMT solver.
 *
 * @author Henry J. Wylde
 */
public final class Smt2File {

    /**
     * A list of elements in this block. Uses a {@link java.util.LinkedHashSet} as the container to
     * remove any duplicate {@link wycs.solver.smt.Stmt.DeclareFun}, {@link
     * wycs.solver.smt.Stmt.DeclareSort}, {@link wycs.solver.smt.Stmt.DefineFun}, {@link
     * wycs.solver.smt.Stmt.DefineSort} and {@link wycs.solver.smt.Stmt.Assert}s. Doing this means
     * we don't have to manually check for their existence and ensures they are uniquely defined.
     */
    private final Set<Element> elements = new LinkedHashSet<Element>();

    public Smt2File() {}

    /**
     * Appends the given elements to this file.
     *
     * @param elements the elements to add.
     */
    public void append(Element... elements) {
        append(Arrays.asList(elements));
    }

    /**
     * Appends the given elements to this file.
     *
     * @param elements the elements to add.
     */
    public void append(Collection<? extends Element> elements) {
        if (elements.contains(null)) {
            throw new NullPointerException("elements cannot contain null");
        }

        this.elements.addAll(elements);
    }

    /**
     * Clears this file, removing all elements.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (elements.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (Element element : elements) {
            sb.append(element);

            if (!(element instanceof Block)) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}

