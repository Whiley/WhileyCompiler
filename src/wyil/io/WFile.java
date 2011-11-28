package wyil.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import wyil.lang.Module;
import wyil.lang.ModuleID;

/**
 * A WFile is a file on the file system which represents a Whiley module. The
 * file may be encoded in a range of different formats. For example, it may be a
 * source file and/or a binary wyil file.
 * 
 * @author djp
 * 
 */
public class WFile implements WSystem.ModuleItem {
	private final ModuleID mid;
	private final File file;		
	private final ModuleReader reader;
	
	public WFile(ModuleID mid, File file, ModuleReader reader) {
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

