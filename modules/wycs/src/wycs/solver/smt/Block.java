package wycs.solver.smt;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a block of statements. A block is surrounded by a {@link wycs.solver.smt.Stmt.Push}
 * and {@link wycs.solver.smt.Stmt.Pop} statement in order to have any declarations and assertions
 * localised to itself.
 *
 * @author Henry J. Wylde
 */
public final class Block implements Element {

    /**
     * A list of elements in this block. Uses a {@link java.util.LinkedHashSet} as the container to
     * remove any duplicate {@link wycs.solver.smt.Stmt.DeclareFun}, {@link
     * wycs.solver.smt.Stmt.DeclareSort}, {@link wycs.solver.smt.Stmt.DefineFun}, {@link
     * wycs.solver.smt.Stmt.DefineSort} and {@link wycs.solver.smt.Stmt.Assert}s. Doing this means
     * we don't have to manually check for their existence and ensures they are uniquely defined.
     */
    private final Set<Element> elements = new LinkedHashSet<Element>();

    /**
     * Appends the given elements to this block.
     *
     * @param elements the elements to add.
     */
    public void append(Element... elements) {
        append(Arrays.asList(elements));
    }

    /**
     * Appends the given elements to this block.
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
     * Clears this block, removing all elements.
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

        sb.append(new Stmt.Push(1));
        sb.append("\n");
        for (Element element : elements) {
            sb.append(element);
            sb.append("\n");
        }
        sb.append(new Stmt.Pop(1));
        sb.append("\n");

        return sb.toString();
    }
}
