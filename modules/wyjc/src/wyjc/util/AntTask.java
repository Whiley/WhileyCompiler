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
	 * The class directory is the filesystem directory where all generated jvm
	 * class files are stored.
	 */
	protected DirectoryRoot classDir;
	
	/**
	 * Identifies which wyil files generated from whiley source files which
	 * should be considered for compilation. By default, all files reachable
	 * from <code>whileyDestDir</code> are considered.
	 */
	protected Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
	
	/**
	 * Identifies which wyil files generated from whiley source files should not
	 * be considered for compilation. This overrides any identified by
	 * <code>wyilBinaryIncludes</code> . By default, no files files reachable from
	 * <code>wyilDestDir</code> are excluded.
	 */
	protected Content.Filter<WyilFile> wyilExcludes = null;
	
	public AntTask() {
		super(new Registry());
	}
	
	public void setClassdir(File classdir) throws IOException {
		this.classDir = new DirectoryRoot(classdir, wyilFileFilter, registry);
	}
	
	@Override
	public void setIncludes(String includes) {
		super.setIncludes(includes);
		
    	String[] split = includes.split(",");    	
    	Content.Filter<WyilFile> wyilFilter = null;
    	
		for (String s : split) {
			if (s.endsWith(".whiley")) {
				// in this case, we are explicitly includes some whiley source
				// files. This implicitly means the corresponding wyil files are
				// included.
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(nf,
						wyilFilter);
			} else if (s.endsWith(".wyil")) {
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(nf,
						wyilFilter);
			}
		}
    	
    	this.wyilIncludes = wyilFilter;    	
    }
    
	@Override
    public void setExcludes(String excludes) {
    	super.setExcludes(excludes);
    	
		String[] split = excludes.split(",");
		Content.Filter<WyilFile> wyilFilter = null;
		
		for (String s : split) {
			if (s.endsWith(".whiley")) {
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(
						nf, wyilFilter);
			} else if (s.endsWith(".wyil")) {
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(
						nf, wyilFilter);
			}
		}
    	
    	this.wyilExcludes = wyilFilter;
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
		
		rule.add(wyilDir, wyilIncludes, wyilExcludes, classDir,
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
		for (Path.Entry<WyilFile> source : wyilDir.get(wyilIncludes)) {
			Path.Entry<ClassFile> binary = classDir.get(source.id(),
					ClassFile.ContentType);

			// first, check whether wyil file out-of-date with source file
			if (binary == null
					|| binary.lastModified() < source.lastModified()) {
				sources.add(source);
			}
		}
		
		// done
		return sources;
	}	
	
	@Override
	public void execute() throws BuildException {
        if (whileyDir == null && wyilDir == null) {
            throw new BuildException("whileydir or wyildir must be specified");
        }
       
        if(!compile()) {
        	throw new BuildException("compilation errors");
        }        	        
    }
}
