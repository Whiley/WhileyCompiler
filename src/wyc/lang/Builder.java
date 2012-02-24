package wyc.lang;

import java.util.List;

import wyc.io.Path;

public interface Builder<S,T> {
	public void build(Rule<S,T> rule, List<Path.Entry<S>> delta);
}
