package wycs.io;

import java.io.*;
import java.util.*;
import wycs.lang.*;

public class WycsFileWriter {
	private PrintWriter out;
	
	public WycsFileWriter(Writer writer) {
		this.out = new PrintWriter(writer);
	}
	
	public WycsFileWriter(PrintWriter writer) {
		this.out = writer;
	}
	
	public WycsFileWriter(OutputStream writer) {
		this.out = new PrintWriter(writer);
	}
	
	public void write(WycsFile wf) {
		for(Stmt s : wf.stmts()) {
			out.println(s);
		}
		out.flush();
	}	
}
