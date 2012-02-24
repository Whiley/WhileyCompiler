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

package wysrc.builder;

import java.io.*;
import java.util.*;

import wyil.*;
import wyil.io.ModuleReader;
import wyil.lang.*;
import wyil.util.*;
import wysrc.lang.*;
import wysrc.stages.*;
import wyc.io.Path;
import wyc.lang.Project;

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
public final class Builder {		
	private final Project project;		
	private final ArrayList<Transform> stages;
	
	/**
	 * A map of the source files currently being compiled.
	 */
	private HashMap<ModuleID,SourceFile> srcFiles = new HashMap<ModuleID,SourceFile>();

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Triple<PkgID,String,String>,ArrayList<ModuleID>> importCache = new HashMap();	
	
	
	public Builder(Project project, List<Transform> stages) {
		this.stages = new ArrayList<Transform>(stages);
		this.project = project;
	}
	
	public void build(List<Path.Entry<SourceFile>> delta) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();
				
		srcFiles.clear();
		ArrayList<SourceFile> wyfiles = new ArrayList<SourceFile>();
		for (Path.Entry<SourceFile> f : delta) {			
			SourceFile wf = f.read();
			wyfiles.add(wf);
			srcFiles.put(wf.module, wf);
		}
				
		List<Module> modules = buildModules(wyfiles);
		finishCompilation(modules);		
		
		long endTime = System.currentTimeMillis();
		project.logTimedMessage("Compiled " + delta.size() + " file(s)",endTime-start, memory - runtime.freeMemory());		
	}
	
	// ======================================================================
	// Public Accessors
	// ======================================================================
	
	public boolean isPackage(PkgID pid) {
		return project.isPackage(pid);
	}
	
	public boolean isModule(ModuleID mid) {
		return project.isModule(mid);
	}
	
	/**
	 * Determine whether a given name exists or not.
	 * 
	 * @param nid --- Name ID to check
	 * @return
	 */
	public boolean isName(NameID nid) throws ResolveError {		
		ModuleID mid = nid.module();
		SourceFile wf = srcFiles.get(mid);
		if(wf != null) {
			// FIXME: check for the right kind of name			
			return wf.hasName(nid.name());
		} else {
			try {				
				Module m = project.get(mid);
				// FIXME: check for the right kind of name
				return m.hasName(nid.name());
			} catch(ResolveError e) {
				return false;
			}
		}
	}	
	
	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param imp
	 * @return
	 */
	public List<ModuleID> imports(SourceFile.Import imp) throws ResolveError {		
		Triple<PkgID, String, String> key = new Triple<PkgID, String, String>(
				imp.pkg, imp.module, imp.name);
		try {
			ArrayList<ModuleID> matches = importCache.get(key);
			if (matches != null) {
				// cache hit
				return matches;
			} else {
				// cache miss
				matches = new ArrayList<ModuleID>();				
				if(project.isPackage(imp.pkg)) {					
					for (ModuleID mid : project.get(imp.pkg)) {						
						if (imp.matchModule(mid.module())) {
							matches.add(mid);
						}
					}				
				}
				importCache.put(key, matches);
			}
			
			return matches;
		} catch(ResolveError e) {
			throw e;
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
	public SourceFile getSourceFile(ModuleID mid) {
		return srcFiles.get(mid);
	}
	
	/**
	 * Get the (compiled) module associated with a given module identifier. If
	 * the module does not exist, a resolve error is thrown.
	 * 
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public Module getModule(ModuleID mid) throws ResolveError {
		return project.get(mid);
	}
	
	// ======================================================================
	// Private Implementation
	// ======================================================================

	/**
	 * This method puts the given module through the second half of the
	 * compilation pipeline. In particular, it propagates and generates types
	 * for all expressions used within the module, as well as checking for
	 * definite assignment and performing verification checking.
	 * 
	 * @param wf
	 */
	private void finishCompilation(List<Module> modules) throws Exception {	
		for(Module m : modules) {
			project.update(m);
		}
		for(Transform stage : stages) {
			for(Module module : modules) {
				process(module,stage);
			}
		}		
	}
	
	private void process(Module module, Transform stage) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();
		String name = name(stage.getClass().getSimpleName());		
		
		try {						
			stage.apply(module);			
			project.logTimedMessage("[" + module.filename() + "] applied "
					+ name, System.currentTimeMillis() - start, memory - runtime.freeMemory());
			System.gc();
		} catch (RuntimeException ex) {
			project.logTimedMessage("[" + module.filename() + "] failed on "
					+ name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			throw ex;
		} catch (IOException ex) {
			project.logTimedMessage("[" + module.filename() + "] failed on "
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
	
	private List<Module> buildModules(List<SourceFile> delta) {
		GlobalResolver resolver = new GlobalResolver(this);
		
		for(SourceFile wf : delta) {
			Runtime runtime = Runtime.getRuntime();
			long start = System.currentTimeMillis();		
			long memory = runtime.freeMemory();					
			new FlowTyping(resolver).propagate(wf);
			project.logTimedMessage("[" + wf.filename + "] flow typing",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());			
		}		
		
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();	
		// FIXME: this is knackered!
		List<Module> modules = new CodeGeneration(this,resolver).generate(delta);			
		project.logTimedMessage("code generation",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());		
		return modules;
	}		
}
