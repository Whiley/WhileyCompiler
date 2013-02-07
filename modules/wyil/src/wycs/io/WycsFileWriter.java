package wycs.io;

import java.io.*;
import java.util.*;
import wycs.lang.*;

public class WycsFileWriter {
	private PrintStream out;
	
	public WycsFileWriter(PrintStream writer) {
		this.out = writer;
	}
		
	public WycsFileWriter(OutputStream writer) {
		try {
			this.out = new PrintStream(writer, true, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			this.out = new PrintStream(writer);
		}
	}
	
	public void write(WycsFile wf) {
		for(Stmt s : wf.stmts()) {
			out.println(s);
		}
		out.flush();
	}	
}
