package wycs.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wybs.lang.Content;
import wybs.lang.Path;

import wycs.io.*;

public class WycsFile {
	
	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<WycsFile> ContentType = new Content.Type<WycsFile>() {
		public Path.Entry<WycsFile> accept(Path.Entry<?> e) {			
			if (e.contentType() == this) {
				return (Path.Entry<WycsFile>) e;
			} 			
			return null;
		}

		public WycsFile read(Path.Entry<WycsFile> e, InputStream input) throws IOException {			
			WycsFileReader reader = new WycsFileReader(e.id().toString(),input);
			WycsFile mi = reader.read();
			return mi;				
		}

		public void write(OutputStream output, WycsFile module) throws IOException {
			WycsFileWriter writer = new WycsFileWriter(output);
			writer.write(module);
		}

		public String toString() {
			return "Content-Type: wycs";
		}
	};	

	// =========================================================================
	// State
	// =========================================================================

	private final String filename;
	private final ArrayList<Stmt> stmts;

	// =========================================================================
	// Constructors
	// =========================================================================

	public WycsFile(String filename, Collection<Stmt> stmts) {
		this.filename = filename;
		this.stmts = new ArrayList<Stmt>(stmts);
	}
	
	// =========================================================================
	// Accessors
	// =========================================================================
	
	public List<Stmt> stmts() {
		return stmts;
	}
	
	public String filename() {
		return filename;
	}
}
