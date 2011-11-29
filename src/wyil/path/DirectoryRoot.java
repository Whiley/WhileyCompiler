package wyil.path;

import java.io.*;
import java.util.*;

import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.PkgID;

/**
 * A Directory represents a directory on the file system. Using this, we can
 * list items on the path and see what is there.
 * 
 * @author djp
 * 
 */
public class DirectoryRoot implements Path.Root {
	private final java.io.File dir;	

	public DirectoryRoot(String dir) throws IOException {
		this.dir = new File(dir);		
	}
	
	public DirectoryRoot(java.io.File dir) {
		this.dir = dir;				
	}
	
	public List<Path.Entry> list(PkgID pkg) throws IOException {
		File location = new File(dir + pkg.fileName());
		
		if (location.exists() && location.isDirectory()) {
			ArrayList<Path.Entry> entries = new ArrayList<Path.Entry>();
			
			// FIXME: update to search for whiley files as well
			
			for (File file : location.listFiles()) {
				String filename = file.getName();
				if (filename.endsWith(".class")) {
					String name = filename.substring(0, filename.length() - 6);
					ModuleID mid = new ModuleID(pkg, name);
					entries.add(new Entry(mid, file));
				}
			}
			
			return entries;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * A WFile is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author djp
	 * 
	 */
	public static class Entry implements Path.Entry {
		private final ModuleID mid;
		private final java.io.File file;		
		
		public Entry(ModuleID mid, java.io.File file) {
			this.mid = mid;
			this.file = file;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public String location() {
			return file.getPath();
		}
		
		public String suffix() {
			String filename = file.getName();
			String suffix = "";
			int pos = filename.lastIndexOf('.');
			if (pos > 0) {
				suffix = filename.substring(pos + 1);
			}
			return suffix;
		}
		
		public InputStream contents() throws IOException {
			return new FileInputStream(file);
		}		
	}	
}