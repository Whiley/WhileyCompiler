package wyc.io;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Responsible for "pretty printing" a Whiley File. This is useful for
 * formatting Whiley Files. Also, it can be used to programatically generate
 * Whiley Files.
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyFilePrinter {
	private PrintWriter out;
	
	public WhileyFilePrinter(PrintWriter writer) {
		this.out = writer;
	}
	
	public WhileyFilePrinter(OutputStream stream) {
		this.out = new PrintWriter(new OutputStreamWriter(stream));
	}
}
