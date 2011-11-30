package wyil.path;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class BinaryDirectoryRoot extends DirectoryRoot {
	public BinaryDirectoryRoot(String dir) throws IOException {
		super(dir);		
	}
	
	public BinaryDirectoryRoot(File dir) {
		super(dir);
	}

	public String suffix() {
		return ".class";
	}

	public FileFilter filter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".class");
			}
		};
	}
}
