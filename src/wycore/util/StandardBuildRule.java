package wycore.util;

import java.util.ArrayList;
import java.util.List;

import wycore.lang.*;

public class StandardBuildRule implements BuildRule {	
	private final Builder builder;
	private final ArrayList<Entry> entries;

	public StandardBuildRule(Builder builder) {
		this.builder = builder;
		this.entries = new ArrayList<Entry>();
	}
	
	public void add(Path.Root source, Path.Root target, Path.Filter includes, Path.Filter excludes) {
		this.entries.add(new Entry(source, target, includes, excludes));
	}
	
	public void add(Path.Root source, Path.Root target, Path.Filter includes) {
		this.entries.add(new Entry(source, target, includes, null));
	}
	
	public List<Path.Entry> dependents() throws Exception {
		return null;
	}
	
	public void apply() throws Exception {
				
	}
	
	private final static class Entry {
		final Path.Root source;
		final Path.Root target;
		final Path.Filter includes;
		final Path.Filter excludes;
		
		public Entry(Path.Root srcRoot, Path.Root targetRoot, Path.Filter includes, Path.Filter excludes) {
			this.source = srcRoot;
			this.target = targetRoot;
			this.includes = includes;
			this.excludes = excludes;
		}		
	}
}
