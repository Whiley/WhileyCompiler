package wyil.path;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class SourceDirectoryRoot extends DirectoryRoot {
	
	public SourceDirectoryRoot(String dir) throws IOException {
		super(dir);		
	}
	
	public SourceDirectoryRoot(File dir) {
		super(dir);		
	}
	
	public String suffix() {
		return ".whiley";
	}
	
	public FileFilter filter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".whiley");
			}			
		};
	}
}
