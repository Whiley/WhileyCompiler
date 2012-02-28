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
import java.util.List;

import wycore.lang.*;
import wyil.util.Pair;

/**
 * <p>
 * Provides a straightforward, yet flexible implementation of BuildRule. This
 * build rule supports both include and exclude filters, multiple source roots
 * and permits each source root to be associated with a different target root.
 * It is expected that this rule is sufficiently flexible for the majority of
 * situations encountered.
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
	
	public void add(Path.Root source, Path.Root target, Path.Filter<?> includes, Path.Filter<?> excludes) {
		this.items.add(new Item(source, target, includes, excludes));
	}
	
	public void add(Path.Root source, Path.Root target, Path.Filter<?> includes) {
		this.items.add(new Item(source, target, includes, null));
	}
	
	public ArrayList<Path.Entry<?>> dependents() throws Exception {
		ArrayList<Path.Entry<?>> dependents = new ArrayList();
		for (int i=0;i!=items.size();++i) {
			final Item item = items.get(i);
			final Path.Root source = item.source;		
			final Path.Filter<?> includes = item.includes;
			final Path.Filter<?> excludes = item.excludes;

			for (Path.Entry<?> se : source.get(includes)) {
				if (excludes == null || excludes.match(se) == null) {
					dependents.add(se);
				}
			}
		}
		return dependents;
	}
	
	public void apply() throws Exception {
		ArrayList<Pair<Path.Entry<?>, Path.Entry<?>>> delta = new ArrayList();
		for (int i = 0; i != items.size(); ++i) {
			final Item item = items.get(i);
			final Path.Root source = item.source;
			final Path.Root target = item.target;
			final Path.Filter<?> includes = item.includes;
			final Path.Filter<?> excludes = item.excludes;
			
			for (Path.Entry<?> se : source.get(includes)) {
				if (excludes == null || excludes.match(se) == null) {
					Path.Entry<?> te = target.create(se.id(), null);
					if(se.lastModified() > te.lastModified()) {
						// hit
						delta.add(new Pair(se, te));
					}
				}
			}
		}
		
		if(!delta.isEmpty()) {
			builder.build(delta);
		}
	}
	
	private final static class Item {
		final Path.Root source;
		final Path.Root target;
		final Path.Filter<?> includes;
		final Path.Filter<?> excludes;
		
		public Item(Path.Root srcRoot, Path.Root targetRoot, Path.Filter includes, Path.Filter excludes) {
			this.source = srcRoot;
			this.target = targetRoot;
			this.includes = includes;
			this.excludes = excludes;
		}		
	}
}
