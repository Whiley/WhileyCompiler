package wycore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wycore.lang.Builder;
import wycore.lang.Path;

public class AbstractBuildRule {	
	private final Builder builder;
	private final ArrayList<Path.Filter> sources;

	public AbstractBuildRule(Builder builder, Collection<Path.Filter> rules) {
		this.builder = builder;
		this.sources = new ArrayList(rules);		
	}
	
	public AbstractBuildRule(Builder builder, Path.Filter... filters) {
		this.builder = builder;
		this.sources = new ArrayList();
		for(Path.Filter r : filters) {
			this.sources.add(r);
		}
	}
	
	public List<Path.Entry> dependents() {
		
	}
	
	public boolean apply() throws Exception {
				
	}
}
