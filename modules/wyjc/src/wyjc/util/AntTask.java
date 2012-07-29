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

package wyjc.util;

import java.io.*;
import java.util.*;

import wybs.lang.*;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.*;
import wyc.builder.Whiley2WyilBuilder;
import wyc.lang.WhileyFile;
import wyil.Pipeline;
import wyil.lang.WyilFile;
import wyjc.Wyil2JavaBuilder;
import wyjvm.lang.ClassFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class AntTask extends wyc.util.AntTask {
	
	public static class Registry extends wyc.util.AntTask.Registry {
		public void associate(Path.Entry e) {
			String suffix = e.suffix();
			
			if(suffix.equals("class")) {				
				e.associate(ClassFile.ContentType, null);				
			} else {
				super.associate(e);
			}
		}
		
		public String suffix(Content.Type<?> t) {
			if(t == ClassFile.ContentType) {
				return "class";
			} else {
				return super.suffix(t);
			}
		}
	}
	
	/**
	 * The purpose of the class file filter is simply to ensure only binary
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter classFileFilter = new FileFilter() {
		public boolean accept(File f) {
			String name = f.getName();
			return name.endsWith(".class") || f.isDirectory();
		}
	};
	
	/**
	 * The wyil source directory is the filesystem directory from which the
	 * compiler will look for (wyil) source files.
	 */
	protected DirectoryRoot wyilSourceDir;		
	
	/**
	 * Identifies which wyil files generated from whiley source files which
	 * should be considered for compilation. By default, all files reachable
	 * from <code>whileyDestDir</code> are considered.
	 */
	protected Content.Filter<WyilFile> wyilBinaryIncludes = Content.filter("**", WyilFile.ContentType);
	
	/**
	 * Identifies which standalone wyil files should be considered for
	 * compilation. By default, all files reachable from
	 * <code>wyilSourceDir</code> are considered.
	 */
	protected Content.Filter<WyilFile> wyilSourceIncludes = Content.filter("**", WyilFile.ContentType);
	
	
	/**
	 * Identifies which wyil files generated from whiley source files should not
	 * be considered for compilation. This overrides any identified by
	 * <code>wyilBinaryIncludes</code> . By default, no files files reachable from
	 * <code>wyilDestDir</code> are excluded.
	 */
	protected Content.Filter<WyilFile> wyilBinaryExcludes = null;
	
	/**
	 * Identifies which standalone wyil files should not be considered for
	 * compilation. This overrides any identified by
	 * <code>wyilSourceIncludes</code> . By default, no files files reachable
	 * from <code>wyilSourceDir</code> are excluded.
	 */
	protected Content.Filter<WyilFile> wyilSourceExcludes = null;
	
	public AntTask() {
		super(new Registry());
	}
	
	@Override
	public void setSrcdir(File srcdir) throws IOException {
		super.setSrcdir(srcdir);
		this.wyilSourceDir = new DirectoryRoot(srcdir, wyilFileFilter, registry);
	}
	
	@Override
	public void setIncludes(String includes) {
		super.setIncludes(includes);
		
    	String[] split = includes.split(",");
    	Content.Filter<WyilFile> wyilSourceFilter = null;
    	Content.Filter<WyilFile> wyilBinaryFilter = null;
    	
		for (String s : split) {
			if (s.endsWith(".whiley")) {
				// in this case, we are explicitly includes some whiley source
				// files. This implicitly means the corresponding wyil files are
				// included.
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilBinaryFilter = wyilBinaryFilter == null ? nf : Content.or(nf,
						wyilBinaryFilter);
			} else if (s.endsWith(".wyil")) {
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilSourceFilter = wyilSourceFilter == null ? nf : Content.or(
						nf, wyilSourceFilter);
			}
		}
    	
    	this.wyilBinaryIncludes = wyilBinaryFilter;
       	this.wyilSourceIncludes = wyilSourceFilter;
    }
    
	@Override
    public void setExcludes(String excludes) {
    	super.setExcludes(excludes);
    	
		String[] split = excludes.split(",");
		Content.Filter<WyilFile> wyilBinaryFilter = null;
		Content.Filter<WyilFile> wyilSourceFilter = null;
		
		for (String s : split) {
			if (s.endsWith(".whiley")) {
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilBinaryFilter = wyilBinaryFilter == null ? nf : Content.or(
						nf, wyilBinaryFilter);
			} else if (s.endsWith(".wyil")) {
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilSourceFilter = wyilSourceFilter == null ? nf : Content.or(
						nf, wyilSourceFilter);
			}
		}
    	
    	this.wyilBinaryExcludes = wyilBinaryFilter;
    	this.wyilSourceExcludes = wyilSourceFilter;
    }
   
	@Override
	protected void addBuildRules(SimpleProject project) {
		
		// Add default build rule for converting whiley files into wyil files. 
		super.addBuildRules(project);
		
		// Now, add build rule for converting wyil files into class files using
		// the Wyil2JavaBuilder.
		
		Wyil2JavaBuilder jbuilder = new Wyil2JavaBuilder();

		if (verbose) {
			jbuilder.setLogger(new Logger.Default(System.err));
		}

		StandardBuildRule rule = new StandardBuildRule(jbuilder);
		
		rule.add(whileyDestDir, wyilBinaryIncludes, wyilBinaryExcludes, whileyDestDir,
				WyilFile.ContentType, ClassFile.ContentType);
		
		rule.add(wyilSourceDir, wyilSourceIncludes, wyilSourceExcludes, whileyDestDir,
				WyilFile.ContentType, ClassFile.ContentType);
				
		project.add(rule);
	}
    
	@Override
	protected List<Path.Entry<?>> getSourceFileDelta() throws IOException {
		
		// First, determine all whiley source files which are out-of-date with
		// respect to their wyil files. 
		List<Path.Entry<?>> sources = super.getSourceFileDelta();
		
		// Second, look for any wyil files which are out-of-date with their
		// respective class file.
		for (Path.Entry<WyilFile> sourceFile : wyilSourceDir.get(wyilBinaryIncludes)) {
			Path.Entry<ClassFile> binary = whileyDestDir.get(sourceFile.id(),
					ClassFile.ContentType);

			// first, check whether wyil file out-of-date with source file
			if (binary == null
					|| binary.lastModified() < sourceFile.lastModified()) {
				sources.add(sourceFile);
			}
		}
		
		// done
		return sources;
	}	
}
