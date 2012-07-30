package wyjc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import wybs.lang.Content;
import wybs.lang.Path;
import wybs.util.DirectoryRoot;
import wyc.util.WycBuildTask.Registry;
import wyil.lang.WyilFile;
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
	
	public WyjcBuildTask() {
		super(new Registry());
	}
	
	@Override
	public void setWyilDir(File wyildir) throws IOException {
		super.setWyilDir(wyildir);
		if (classDir == null) {
			this.classDir = new DirectoryRoot(wyildir, classFileFilter,
					registry);
		}
	}

	public void setClassDir(File classdir) throws IOException {
		this.classDir = new DirectoryRoot(classdir, classFileFilter,
				registry);
	}
}		

