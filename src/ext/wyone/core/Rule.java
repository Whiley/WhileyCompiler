package wyone.core;

/**
 * An inference rule represents a way of generating new constructors from
 * existing ones. In some cases, the new constructors may subsume the old
 * ones. For example, suppose we have:
 * 
 * <pre>
 * x < 1
 * y < 0
 * x == y
 * </pre>
 * 
 * In this case, we might generate <code>x < 0</code> by applying an
 * inference rule which substitutes y for x in the program. Thus,
 * <code>x<0</code> will subsume <code>x<1</code>.
 * 
 * @author djp
 * 
 */
public class Rule {
	public final String name;
	
	public Rule(String name) {
		this.name = name;
	}
}
