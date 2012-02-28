// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
package wycore.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wycore.lang.*;
import wyil.util.Pair;

/**
 * <p>
 * Provides a straightforward, yet flexible implementation of BuildRule. This
 * build rule supports both include and exclude filters, multiple source roots
 * and permits each source root to be associated with a different target root.
 * </p>
 * <p>
 * It is expected that this rule is sufficiently flexible for the majority of
 * situations encountered. However, the major limitation is an assumption that
 * every target file corresponds to exactly one source file.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class StandardBuildRule implements BuildRule {	
	private final Builder builder;
	private final ArrayList<Item> items;

	public StandardBuildRule(Builder builder) {
		this.builder = builder;
		this.items = new ArrayList<Item>();
	}
	
	public void add(Path.Root source, Content.Type<?> from, Path.Root target,
			Content.Type<?> to, Path.Filter includes, Path.Filter excludes) {
		this.items.add(new Item(source, from, target, to, includes, excludes));
	}

	public void add(Path.Root source, Content.Type<?> from, Path.Root target,
			Content.Type<?> to, Path.Filter includes) {
		this.items.add(new Item(source, from, target, to, includes, null));
	}

	public Set<Path.Entry<?>> dependentsOf(Path.Entry<?> entry) throws Exception {
		for (int i = 0; i != items.size(); ++i) {
			final Item item = items.get(i);
			final Path.Root target = item.target;
			final Path.Root source = item.source;
			final Path.Filter includes = item.includes;
			final Path.Filter excludes = item.excludes;
			final Content.Type<?> to = item.to;
			final Content.Type<?> from = item.from;

			if(source.contains(entry) && from == entry.contentType()
					&& includes.matches(entry.id()) 
					&& (excludes == null || excludes.matches(entry.id()))) {
				Set<Path.Entry<?>> result = new HashSet<Path.Entry<?>>();
				result.add(target.create(entry.id(), to));
				return result;
			}
		}
		return Collections.EMPTY_SET;
	}	

	public void apply(Set<Path.Entry<?>> targets) throws Exception {
		ArrayList<Pair<Path.Entry<?>,Path.Entry<?>>> delta = new ArrayList();
		
		for (Path.Entry<?> te : targets) {
			for (int i = 0; i != items.size(); ++i) {
				final Item item = items.get(i);
				final Path.Root source = item.source;
				final Path.Root target = item.target;
				final Path.Filter includes = item.includes;
				final Path.Filter excludes = item.excludes;
				final Content.Type<?> from = item.from;
				final Content.Type<?> to = item.to;
				
				if (target.contains(te) && to == te.contentType()
						&& includes.matches(te.id()) 
						&& (excludes == null || excludes.matches(te.id()))) {
					Path.Entry<?> se = source.get(te.id(), from);
					if(targets.contains(se)) {
						// this indicates a dependency that has not yet been
						// resolved, so we need to wait until it is (by some
						// other rule).
						return;
					}
					delta.add(new Pair<Path.Entry<?>,Path.Entry<?>>(se,te));
				}
			}
		}
		
		if(!delta.isEmpty()) {
			builder.build(delta);
			for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
				targets.remove(p.second());
			}
		}
	}
	
	private final static class Item {
		final Path.Root source;
		final Path.Root target;
		final Content.Type<?> from;
		final Content.Type<?> to;
		final Path.Filter includes;
		final Path.Filter excludes;

		public Item(Path.Root srcRoot, Content.Type<?> from,
				Path.Root targetRoot, Content.Type<?> to, Path.Filter includes,
				Path.Filter excludes) {
			this.source = srcRoot;
			this.target = targetRoot;
			this.from = from;
			this.to = to;
			this.includes = includes;
			this.excludes = excludes;
		}
	}
}
