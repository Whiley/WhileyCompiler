package wybs.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.Builder;
import wyfs.lang.Content;
import wyfs.lang.Path;

/**
 * <p>
 * Provides a straightforward, yet flexible build rule implementation. This
 * build rule supports both include and exclude filters. It is expected that
 * this rule is sufficiently flexible for the majority of situations
 * encountered.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class StdBuildRule implements Build.Rule {
	/**
	 * The builder used to build files using this rule.
	 */
	private final Builder builder;
	
	/**
	 * The source root containing all files which might be built using this
	 * rule. However, whether or not files contained in this root will actually
	 * be built depends on the includes and excludes filters.
	 */
	final Path.Root source;
	
	/**
	 * The destination root into which all files built using this rule are
	 * placed.
	 */
	final Path.Root target;
	
	/**
	 * A content filter used to determine which files contained in the source
	 * root should be built by this rule.
	 */
	final Content.Filter<?> includes;
	
	/**
	 * A content filter used to determine which files contained in the source
	 * root should be not built by this rule.
	 */
	final Content.Filter<?> excludes;
	
	/**
	 * The content type of the source files targeted by this rule.
	 */
	final Content.Type<?> from;
	
	/**
	 * The content type of the binary files targeted by this rule.
	 */
	final Content.Type<?> to;
	
	
	public StdBuildRule(Builder builder, Path.Root srcRoot,
			Content.Filter<?> includes, Content.Filter<?> excludes,
			Path.Root targetRoot, Content.Type<?> from, Content.Type<?> to) {
		this.builder = builder;
		this.source = srcRoot;
		this.target = targetRoot;
		this.from = from;
		this.to = to;
		this.includes = includes;
		this.excludes = excludes;
	}
	
	@Override
	public Set<Path.Entry<?>> apply(Collection<? extends Path.Entry<?>> group) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
