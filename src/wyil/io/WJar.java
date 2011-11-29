package wyil.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.*;

import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.PkgID;

/**
 * Represents a Jar file on the file system.
 * 
 * @author djp
 *
 */
public class WJar implements WSystem.PackageItem {
	private final PkgID pid;
	private final JarFile jf;	
	private final WSystem root;
	private ArrayList<WSystem.Item> contents;
	
	public WJar(PkgID pid, JarFile dir, WSystem root) {
		this.pid = pid;
		this.jf = dir;		
		this.root = root;
	}

	public PkgID id() {
		return pid;
	}
	
	public List<WSystem.Item> list() throws IOException {
		if(contents == null) {
			Enumeration<JarEntry> entries = jf.entries();
			contents = new ArrayList<WSystem.Item>(); 
			while(entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				String filename = e.getName();				
				String suffix = "";
				int pos = filename.lastIndexOf('.');
				if (pos > 0) {
					suffix = filename.substring(pos+1);
					filename = filename.substring(0, pos);						
				}		
				
				ModuleReader reader = root.getModuleReader(suffix);
				if(reader != null) {
					// Now, construct the package id
					String[] split = filename.split("\\/");
					PkgID pkg = pid;
					for (int i = 0; i != split.length - 1; ++i) {						
						pkg = pkg.append(split[i]);
					}					
					// Then, the module id
					ModuleID mid = new ModuleID(pkg,split[split.length - 1]);		
					contents.add(new Entry(mid, jf, e, reader));
				}				
			}
		}
		return contents;
	}
	
	public void refresh() {
		contents = null;
	}
	
	public static class Entry implements WSystem.ModuleItem {
		private final ModuleID mid;
		private final JarFile parent;
		private final JarEntry entry;		
		private final ModuleReader reader;
		
		public Entry(ModuleID mid, JarFile parent, JarEntry entry, ModuleReader reader) {
			this.mid = mid;
			this.parent = parent;
			this.entry = entry;
			this.reader = reader;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public Module read() throws IOException {
			return reader.read(mid, parent.getInputStream(entry));
		}
		
		public void close() throws IOException {
			
		}
		
		public void refresh() throws IOException {
			
		}
	}
}
