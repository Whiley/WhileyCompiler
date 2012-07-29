package wycc.io;

import java.io.*;

import wyil.lang.WyilFile;

public class CFileWriter {
	private final PrintStream output;
	
	public CFileWriter(OutputStream output) {
		this.output = new PrintStream(output);
	}
	
	public void write(WyilFile module) {
		output.println("// WYIL MODULE: " + module.id());
		
	}	
}
