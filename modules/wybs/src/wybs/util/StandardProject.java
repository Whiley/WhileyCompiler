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

package wybs.util;

import java.io.IOException;
import java.util.*;

import wybs.lang.*;

/**
 * <p>
 * Provides a straightforward implementation of NameSpace and a basic build
 * system supporting an arbitrary number of build rules. The namespace is
 * defined by one or more "path roots" which are locations on the file system
 * where named items may be found. Such locations may be, for example,
 * directories. However, they may also be jar files, or even potentially network
 * locations.
 * </p>
 * 
 * @author David J. Pearce
 */
public class StandardProject implements NameSpace {
	
	/**
	 * The roots of all entries known to the system which form the global
	 * namespace used by the builder(s).
	 */	
	protected final ArrayList<Path.Root> roots;
	
	/**
	 * The rules associated with this project for transforming content. It is
	 * assumed that for any given transformation there is only one possible
	 * pathway described.
	 */
	protected final ArrayList<BuildRule> rules;
	
	
	public StandardProject(Collection<Path.Root> roots) {
		this.roots = new ArrayList<Path.Root>(roots);
		this.rules = new ArrayList<BuildRule>();
	}
	
	public StandardProject(Collection<Path.Root>... roots) {
		this.rules = new ArrayList<BuildRule>();
		this.roots = new ArrayList<Path.Root>();
		for(Collection<Path.Root> root : roots) {
			this.roots.addAll(root);		
		}
	}
	
	// ======================================================================
	// Configuration Interface
	// ======================================================================		
			
	/**
	 * Add a new builder to this project.
	 * 
	 * @param data.builder
	 */
	public void add(BuildRule rule) {
		rules.add(rule);
	}
	
	/**
	 * Get the roots associated with this project.
	 * 
	 * @return
	 */
	public List<Path.Root> roots() {
		return roots;
	}
	
	/**
	 * Get the build rules associated with this project.
	 * 
	 * @return
	 */
	public List<BuildRule> rules() {
		return rules;
	}
	
	// ======================================================================
	// Accessors
	// ======================================================================		
	
	public boolean exists(Path.ID id, Content.Type<?> ct) throws Exception {
		for(int i=0;i!=roots.size();++i) {
			if(roots.get(i).exists(id, ct)) {				
				return true;
			}
		}		
		return false;
	}
	
	public <T> Path.Entry<T> get(Path.ID id, Content.Type<T> ct) throws Exception {
		for(int i=0;i!=roots.size();++i) {
			Path.Entry<T> e = roots.get(i).get(id, ct);
			if(e != null) {
				return e;
			}			
		}
		return null;
	}
	
	public <T> ArrayList<Path.Entry<T>> get(Content.Filter<T> filter) throws Exception {
		ArrayList<Path.Entry<T>> r = new ArrayList<Path.Entry<T>>();
		for(int i=0;i!=roots.size();++i) {
			r.addAll(roots.get(i).get(filter));
		}
		return r;
	}
	
	public <T> HashSet<Path.ID> match(Content.Filter<T> filter) throws Exception {
		HashSet<Path.ID> r = new HashSet<Path.ID>();
		for(int i=0;i!=roots.size();++i) {
			r.addAll(roots.get(i).match(filter));
		}
		return r;
	}
	
	// ======================================================================
	// Mutators
	// ======================================================================		

	
	public void flush() throws Exception {
		for(int i=0;i!=roots.size();++i) {
			roots.get(i).flush();
		}
	}
	
	public void refresh() throws Exception {
		for(int i=0;i!=roots.size();++i) {
			roots.get(i).refresh();
		}
	}

	/**
	 * Build a given set of source entries, including all files which dependent
	 * upon them.
	 * 
	 * @param sources
	 * @throws Exception
	 */
	public void build(Collection<? extends Path.Entry<?>> sources) throws Exception {
		HashSet<Path.Entry<?>> allTargets = new HashSet();

		// Firstly, initialise list of targets to rebuild.		
		for (BuildRule r : rules) {
			for (Path.Entry<?> source : sources) {
				allTargets.addAll(r.dependentsOf(source));
			}
		}

		// Secondly, add all dependents on those being rebuilt.
		int oldSize;
		do {
			oldSize = allTargets.size();		
			addVerticalDeps(allTargets);
			addHorizontalDeps(allTargets);			
		} while (allTargets.size() != oldSize);
		
		// Finally, build all identified targets!
		do {
			oldSize = allTargets.size();
			for(BuildRule r : rules) {
				r.apply(allTargets);
			}
		} while(allTargets.size() < oldSize);

		// If we didn't manage to build all the targets, then this indicates
		// that some kind of cyclic dependency situation is present.
		if(!allTargets.isEmpty()) {
			// FIXME: to something proper here.
			System.out.println("Cyclic dependency!");
		}
	}
	
	/**
	 * Vertical dependencies are those between files of differing content type.
	 * For example, a file such as Test.wyil depends vertically on the file
	 * Test.whiley if there is a build rule where Test.whiley => Test.class 
	 * 
	 * @param allTargets
	 * @throws IOException
	 */
	private void addVerticalDeps(HashSet<Path.Entry<?>> allTargets)
			throws IOException {
		HashSet<Path.Entry<?>> delta = new HashSet();
		for (BuildRule r : rules) {
			for (Path.Entry<?> target : allTargets) {
				delta.addAll(r.dependentsOf(target));
			}
		}
		allTargets.addAll(delta);
	}
	
	/**
	 * Horizontal dependencies are those between files of the same content type.
	 * These are trickier to identify as we need to maintain dependence
	 * information between them. For example, Test1.wyil will depende
	 * horizontally on Test2.wyil if Test1.wyil uses something defined in
	 * Test2.wyil.
	 * 
	 * @param allTargets
	 * @throws IOException
	 */
	private void addHorizontalDeps(HashSet<Path.Entry<?>> allTargets) throws IOException {
		
	}
}
