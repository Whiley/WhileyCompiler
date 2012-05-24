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

package wyc.builder;

import java.io.*;
import java.util.*;

import wyil.*;
import wyil.lang.*;
import wyil.util.*;
import wybs.lang.*;
import wybs.util.*;
import wyc.lang.*;
import wyc.stages.*;

/**
 * Responsible for managing the process of turning source files into binary code
 * for execution. Each source file is passed through a pipeline of stages that
 * modify it in a variety of ways. The main stages are:
 * <ol>
 * <li>
 * <p>
 * <b>Lexing and Parsing</b>, where the source file is converted into an
 * Abstract Syntax Tree (AST) representation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Name Resolution</b>, where the fully qualified names of all external
 * symbols are determined.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Type Propagation</b>, where the types of all expressions are determined by
 * propagation from e.g. declared parameter types.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>WYIL Generation</b>, where the the AST is converted into the Whiley
 * Intermediate Language (WYIL). A number of passes are then made over this
 * before it is ready for code generation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Code Generation</b>. Here, the executable code is finally generated. This
 * could be Java bytecode, or something else (e.g. JavaScript).
 * </p>
 * </li>
 * </ol>
 * Every stage of the compiler can be configured by setting various options.
 * Stages can also be bypassed (typically for testing) and new ones can be
 * added.
 * 
 * @author David J. Pearce
 * 
 */
public final class WhileyBuilder implements Builder {	
	
	/**
	 * The master namespace for identifying all resources available to the
	 * builder. This includes all modules declared in the project being compiled
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final NameSpace namespace;		
	
	/**
	 * The list of stages which must be applied to a Wyil file.
	 */
	private final List<Transform> stages;
	
	private Logger logger;
	
	/**
	 * A map of the source files currently being compiled.
	 */
	private final HashMap<Path.ID, Path.Entry<WhileyFile>> srcFiles = new HashMap<Path.ID, Path.Entry<WhileyFile>>();

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Trie,ArrayList<Path.ID>> importCache = new HashMap();	
		
	public WhileyBuilder(NameSpace namespace, Pipeline pipeline) {
		this.stages = pipeline.instantiate(this);
		this.logger = Logger.NULL;
		this.namespace = namespace;
	}

	public NameSpace namespace() {
		return namespace;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void build(List<Pair<Path.Entry<?>,Path.Entry<?>>> delta) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		// ========================================================================
		// Parse and register source files
		// ========================================================================
		
		srcFiles.clear();
		int count=0;
		for (Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			if (f.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> sf = (Path.Entry<WhileyFile>) f;
				WhileyFile wf = sf.read();
				count++;				
				srcFiles.put(wf.module, sf);
			}
		}

		logger.logTimedMessage("Parsed " + count + " source file(s).",
				System.currentTimeMillis() - start, memory - runtime.freeMemory());

		// ========================================================================
		// Flow Type source files
		// ========================================================================
		
		GlobalResolver resolver = new GlobalResolver(this);
		
		runtime = Runtime.getRuntime();
		start = System.currentTimeMillis();		
		memory = runtime.freeMemory();
		
		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			if (f.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> sf = (Path.Entry<WhileyFile>) f;			
				WhileyFile wf = sf.read();								
				new FlowTyping(resolver).propagate(wf);						
			}
		}		
		
		logger.logTimedMessage("Typed " + count + " source file(s).",
				System.currentTimeMillis() - start, memory - runtime.freeMemory());
		
		// ========================================================================
		// Code Generation
		// ========================================================================
		
		runtime = Runtime.getRuntime();
		start = System.currentTimeMillis();		
		memory = runtime.freeMemory();	

		GlobalGenerator globalGen = new GlobalGenerator(this,resolver);
		CodeGeneration generator = new CodeGeneration(this,globalGen,resolver);
		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			Path.Entry<?> s = (Path.Entry<?>) p.second();
			if (f.contentType() == WhileyFile.ContentType && s.contentType() == WyilFile.ContentType) {
				Path.Entry<WhileyFile> source = (Path.Entry<WhileyFile>) f;
				Path.Entry<WyilFile> target = (Path.Entry<WyilFile>) s;				
				WhileyFile wf = source.read();								
				WyilFile wyil = generator.generate(wf);
				target.write(wyil);
			}
		}
		
		logger.logTimedMessage("Generated code for " + count + " source file(s).",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
		
		// ========================================================================
		// Pipeline Stages
		// ========================================================================
				
		for(Transform stage : stages) {
			for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
				Path.Entry<?> f = p.second();
				if (f.contentType() == WyilFile.ContentType) {			
					Path.Entry<WyilFile> wf = (Path.Entry<WyilFile>) f;
					process(wf.read(),stage);
				}				
			}
		}	
	
		// ========================================================================
		// Done.
		// ========================================================================
		
		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Compiled " + delta.size() + " file(s)",
				endTime - start, memory - runtime.freeMemory());
	}
	
	// ======================================================================
	// Public Accessors
	// ======================================================================
	
	public boolean exists(Path.ID id) {
		try {
			return namespace.exists(id, WhileyFile.ContentType)
					|| namespace.exists(id, WyilFile.ContentType);
		} catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * Determine whether a given name exists or not.
	 * 
	 * @param nid --- Name ID to check
	 * @return
	 */
	public boolean isName(NameID nid) throws Exception {		
		Path.ID mid = nid.module();
		Path.Entry<WhileyFile> wf = srcFiles.get(mid);
		if(wf != null) {
			// FIXME: check for the right kind of name			
			return wf.read().hasName(nid.name());
		} else {			
			Path.Entry<WyilFile> m = namespace.get(mid,WyilFile.ContentType);
			// FIXME: check for the right kind of name
			return m.read().hasName(nid.name());			
		}
	}	
	
	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param imp
	 * @return
	 */
	public List<Path.ID> imports(Trie key) throws ResolveError {		
		try {
			ArrayList<Path.ID> matches = importCache.get(key);
			if (matches != null) {
				// cache hit
				return matches;
			} else {
				// cache miss
				matches = new ArrayList<Path.ID>();	
				
				for(Path.Entry<WhileyFile> sf : srcFiles.values()) {
					if(key.matches(sf.id())) {						
						matches.add(sf.id());
					}
				}

				if(key.isConcrete()) {
					// A concrete key is one which does not contain a wildcard.
					// Therefore, it corresponds to exactly one possible item.
					// It is helpful, from a performance perspective, to use
					// NameSpace.exists() in such case, as this conveys the fact
					// that we're only interested in a single item.
					if(namespace.exists(key,WyilFile.ContentType)) {
						matches.add(key);
					}
				} else {
					Content.Filter<?> binFilter = Content.filter(key,
							WyilFile.ContentType);
					for (Path.ID mid : namespace.match(binFilter)) {					
						matches.add(mid);
					}
				}
												
				importCache.put(key, matches);
			}
			
			return matches;
		} catch(Exception e) {
			throw new ResolveError(e.getMessage(),e);
		}
	}	
			
	/**
	 * Get the source file associated with a given module identifier. If the
	 * source file does not exist, null is returned.
	 * 
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public WhileyFile getSourceFile(Path.ID mid) throws Exception {
		Path.Entry<WhileyFile> e = srcFiles.get(mid);
		if(e != null) {
			return e.read();
		} else {
			return null;
		}
	}
	
	/**
	 * Get the (compiled) module associated with a given module identifier. If
	 * the module does not exist, a resolve error is thrown.
	 * 
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public WyilFile getModule(Path.ID mid) throws Exception {
		return namespace.get(mid, WyilFile.ContentType).read();
	}
	
	// ======================================================================
	// Private Implementation
	// ======================================================================

	private void process(WyilFile module, Transform stage) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();
		String name = name(stage.getClass().getSimpleName());		
		
		try {						
			stage.apply(module);			
			logger.logTimedMessage("[" + module.filename() + "] applied "
					+ name, System.currentTimeMillis() - start, memory - runtime.freeMemory());
			System.gc();
		} catch (RuntimeException ex) {
			logger.logTimedMessage("[" + module.filename() + "] failed on "
					+ name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			throw ex;
		} catch (IOException ex) {
			logger.logTimedMessage("[" + module.filename() + "] failed on "
					+ name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			throw ex;
		}
	}
	
	private static String name(String camelCase) {
		boolean firstTime = true;
		String r = "";
		for(int i=0;i!=camelCase.length();++i) {
			char c = camelCase.charAt(i);
			if(!firstTime && Character.isUpperCase(c)) {
				r += " ";
			} 
			firstTime=false;
			r += Character.toLowerCase(c);;
		}
		return r;
	}			
}
