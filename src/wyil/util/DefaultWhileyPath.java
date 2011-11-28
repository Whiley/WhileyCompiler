package wyil.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import wyil.ModuleReader;
import wyil.WhileyPath;
import wyil.lang.*;

/**
 * Provides a default implementation of WhileyPath.Package, representing a
 * file system directory. This caches the directory contents on first load,
 * and refreshes them accordingly.
 */
public class DefaultWhileyPath implements WhileyPath {

	// =============================================================
	// Interfaces
	// =============================================================

	public interface ItemCreator {
		WhileyPath.Item create(PkgID root, File file);
	}
	
	// =============================================================
	// Default Implementations
	// =============================================================
	
	public class Directory implements WhileyPath.PackageItem {
		private final PkgID pid;
		private final File dir;
		private final FileFilter filter;
		private ArrayList<WhileyPath.Item> contents;

		public Directory(PkgID pid, File dir, FileFilter filter) {
			this.pid = pid;
			this.dir = dir;
			this.filter = filter;
		}

		public PkgID id() {
			return pid;
		}

		public List<WhileyPath.Item> list() throws IOException {
			if (contents == null) {
				contents = new ArrayList<WhileyPath.Item>();
				for (File file : dir.listFiles()) {
					if (filter.accept(file)) {
						if (file.isDirectory()) {
							// TODO: need to modify filter
							contents.add(new Directory(pid.append(file
									.getName()), file, filter));
						} else if (file.isFile()) {
							WhileyPath.Item item = DefaultWhileyPath.this
									.createFromSuffix(pid, file);
							if (item != null) {
								contents.add(item);
							}
						}
					}
				}
			}
			return Collections.unmodifiableList(contents);
		}

		public void close() throws IOException {
			// not needed
		}

		public void refresh() throws IOException {
			contents = null;
		}
	}
	
	public static class ModuleFile implements WhileyPath.ModuleItem {
		private final ModuleID mid;
		private final File file;		
		private final ModuleReader reader;
		
		public ModuleFile(ModuleID mid, File file, ModuleReader reader) {
			this.mid = mid;
			this.file = file;
			this.reader = reader;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public Module read() throws IOException {
			return reader.read(mid, new FileInputStream(file));
		}
		
		public void close() throws IOException {
			
		}
		
		public void refresh() throws IOException {
			
		}
	}	
	
	// =============================================================
	// Body
	// =============================================================	
	private ArrayList<WhileyPath.Item> items;
	private HashMap<String,ItemCreator> suffixMap;	
	
	public DefaultWhileyPath() {
		items = new ArrayList<WhileyPath.Item>();
	}

	public List<WhileyPath.Item> items() {
		return Collections.unmodifiableList(items);
	}
	
	public void add(WhileyPath.Item item) {
		items.add(item);
	}

	/**
	 * <p>
	 * Create a WhileyPath Item from a file based on its suffix. This allows the
	 * user to specify different creators for different suffixes in a system.
	 * For example, in the whiley-to-java compiler, we want the "class" suffix
	 * to create items for reading whiley modules from class files.
	 * </p>
	 * 
	 * @param root
	 * @param file
	 * @return
	 */
	protected WhileyPath.Item createFromSuffix(PkgID root, File file) {		
		String filename = file.getName();
		int idx = filename.lastIndexOf('.');
		if(idx > 0) {
			String suffix = filename.substring(idx+1);
			ItemCreator creator = suffixMap.get(suffix);
			if(creator != null) {				
				return creator.create(root, file);
			}
		}		
		return null;
	}
}
