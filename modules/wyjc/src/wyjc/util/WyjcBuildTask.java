package wyjc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import wybs.lang.Content;
import wybs.lang.Logger;
import wybs.lang.Path;
import wybs.util.DirectoryRoot;
import wybs.util.StandardProject;
import wybs.util.StandardBuildRule;
import wyil.lang.WyilFile;
import wyjc.Wyil2JavaBuilder;
import wyjvm.lang.ClassFile;

public class WyjcBuildTask extends wyc.util.WycBuildTask {
	
	public static class Registry extends wyc.util.WycBuildTask.Registry {
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
	
	public WyjcBuildTask() {
		super(new Registry());
	}
	
	@Override
	public void setWhileyDir(File dir) throws IOException {
		super.setWhileyDir(dir);
		if (classDir == null) {
			this.classDir = new DirectoryRoot(dir, classFileFilter,
					registry);
		}
	}
	
	@Override
	public void setWyilDir(File dir) throws IOException {
		super.setWyilDir(dir);
		if (classDir == null) {
			this.classDir = new DirectoryRoot(dir, classFileFilter,
					registry);
		}
	}

	public void setClassDir(File classdir) throws IOException {
		this.classDir = new DirectoryRoot(classdir, classFileFilter,
				registry);
	}
		
	@Override
	protected void addBuildRules(StandardProject project) {
		
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
	protected List<Path.Entry<?>> getModifiedSourceFiles() throws IOException {
		// First, determine all whiley source files which are out-of-date with
		// respect to their wyil files.
		List<Path.Entry<?>> sources = super.getModifiedSourceFiles();

		// Second, look for any wyil files which are out-of-date with their
		// respective class file.
		for (Path.Entry<WyilFile> source : wyilDir.get(wyilIncludes)) {
			Path.Entry<ClassFile> binary = classDir.get(source.id(),
					ClassFile.ContentType);

			// first, check whether wyil file out-of-date with source file
			if (binary == null || binary.lastModified() < source.lastModified()) {
				sources.add(source);
			}
		}

		// done
		return sources;
	}
	
	@Override
	protected void flush() throws IOException {
		super.flush();
		classDir.flush();
	}
}		

