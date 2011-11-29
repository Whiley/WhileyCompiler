package wyil.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wyil.lang.ModuleID;
import wyil.lang.PkgID;

/**
 * A WDirectory represents a directory on the file system. Using this, we can
 * list items on the path and see what is there.
 * 
 * @author djp
 * 
 */
public class WDirectory implements WContainer {
	private final PkgID pid;
	private final File dir;	
	private ArrayList<WItem> contents;	

	public WDirectory(PkgID pid, File dir) {
		this.pid = pid;
		this.dir = dir;				
	}

	public PkgID id() {
		return pid;
	}

	public List<WItem> list() throws IOException {
		if (contents == null) {
			contents = new ArrayList<WItem>();
			for (File file : dir.listFiles()) {				
				if (file.isDirectory()) {
					// TODO: need to modify filter
					contents.add(new WDirectory(pid.append(file.getName()),
							file));
				} else if (file.isFile()) {						
					String filename = file.getName();
					String suffix = "";
					int pos = filename.lastIndexOf('.');
					if (pos > 0) {
						suffix = filename.substring(pos+1);
						filename = filename.substring(0, pos);						
					}					
					
					ModuleReader reader = WSystem.getModuleReader(suffix);
					if (reader != null) {
						contents.add(new WFile(new ModuleID(pid, filename),
								file, reader));
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