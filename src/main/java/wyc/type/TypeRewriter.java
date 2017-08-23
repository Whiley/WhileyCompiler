package wyc.type;

import wyal.lang.WyalFile.Type;

/**
 * <p>
 * Responsible for rewriting a given type in some way whilst maintaining
 * semantic equivalence. Often (though not exclusively) this is used for
 * simplifying types. For example, <code>int|int</code> could be simplfiied to
 * <code>int</code>. Type simplification is necessary, for example, to prevent
 * infinite loops in some operations (e.g. type extraction). Type simplification
 * is also useful in making generated types easier to read.
 * </p>
 * <p>
 * Type simplification can be performed over a range of constructs. The
 * following highlights some examples:
 * </p>
 * <ul>
 * <li><b>Negations</b>. For example, <code>!!int</code> can be rewritten to
 * <code>int</code>. Likewise, <code>!({int f}|int)</code> can be rewritten to
 * <code>(!{int f})|(!int)</code>.</li>
 * <li><b>Unions</b>. For example, <code>int|null|int</code> can be rewritten to
 * <code>int|null</code>. Likewise, <code>{int|null f}</code> can be rewritten
 * to <code>{int f}|{null f}</code>.</li>
 * <li><b>Intersections</b>. For example, <code>int&int</code> can be rewritten
 * to <code>int</code>. Likewise, <code>{int|null f}&{int f}</code> can be
 * rewritten to <code>{int f}</code>.</li>
 * </ul>
 * <p>
 * This interface does not specify what rewrites will be applied. It only
 * provides a concept of type simplification in abstract sense. Specific
 * implementations of this interface will choose what rules to apply based on
 * their own use case (e.g. for internal algorithms, for human readability,
 * etc).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface TypeRewriter {
	/**
	 * Apply the rewrite rules to the given type producing a potentially updated
	 * type. If no rewrites are applied, then the original <code>Type</code>
	 * object should be returned intact.
	 *
	 * @param type
	 *            The type to be rewritten
	 * @return
	 */
	Type rewrite(Type type);
}
