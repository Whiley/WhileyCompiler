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
	private final PkgID pid;
	private final java.io.File dir;	

	public DirectoryRoot(PkgID pid, java.io.File dir) {
		this.pid = pid;
		this.dir = dir;				
	}

	public PkgID id() {
		return pid;
	}

	public List<Path.Entry> list(PkgID pkg) throws IOException {
		pkg.fileName();
		return null;
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