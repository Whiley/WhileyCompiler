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
public class BinaryDirectoryRoot implements Path.Root {
	private static final FileFilter filter = new FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".class");
		}
	};
	
	private final java.io.File dir;	
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @param filter
	 *            --- filter which determines what constitutes a valid entry for
	 *            this directory.
	 * @throws IOException
	 */
	public BinaryDirectoryRoot(String path) throws IOException {
		this.dir = new File(path);				
	}
	
	/**
	 * Construct a directory root from a given directory and file filter.
	 * 
	 * @param file
	 *            --- location of directory on filesystem.
	 * @param filter
	 *            --- filter which determines what constitutes a valid entry for
	 *            this directory.
	 */
	public BinaryDirectoryRoot(java.io.File dir) {
		this.dir = dir;
	}
	
	public boolean exists(PkgID pkg) throws IOException {
		File location = new File(dir + File.separator + pkg.fileName());
		return location.exists() && location.isDirectory();
	}
	
	public List<Path.Entry> list(PkgID pkg) throws IOException {
		File location = new File(dir + File.separator + pkg.fileName());

		if (location.exists() && location.isDirectory()) {
			ArrayList<Path.Entry> entries = new ArrayList<Path.Entry>();

			for (File file : location.listFiles(filter)) {
				String filename = file.getName();
				String name = filename.substring(0, filename.lastIndexOf('.'));
				ModuleID mid = new ModuleID(pkg, name);
				entries.add(new Entry(mid, file));				
			}

			return entries;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	public Entry lookup(ModuleID mid) throws IOException {
		File location = new File(dir + File.separator + mid.fileName()
				+ ".class");
		if (location.exists()) {
			return new Entry(mid, location);
		} else {
			return null; // not found
		}
	}

	public String toString() {
		return dir.getPath();
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
		
		public long lastModified() {
			return file.lastModified();
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