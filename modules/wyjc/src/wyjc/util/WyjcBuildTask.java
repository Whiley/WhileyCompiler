package wyjc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import wybs.lang.Content;
import wybs.lang.Logger;
import wybs.lang.Path;
import wybs.util.DirectoryRoot;
import wybs.util.SimpleProject;
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
	public void setIncludes(String includes) {
		super.setIncludes(includes);
		
    	String[] split = includes.split(",");    	
    	Content.Filter<WyilFile> wyilFilter = null;
    	
		for (String s : split) {
			if (s.endsWith(".whiley")) {
				// in this case, we are explicitly including some whiley source
				// files. This implicitly means the corresponding wyil files are
				// included.
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(nf,
						wyilFilter);
			} else if (s.endsWith(".wyil")) {
				// in this case, we are explicitly including some wyil files.
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(nf,
						wyilFilter);
			}
		}
    	
		if(wyilFilter != null) {
			this.wyilIncludes = wyilFilter;
		}
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

