package wycs;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import wycs.lang.*;
import wycs.io.*;

public class WycsMain {
	public static PrintStream errout;
	
	/**
	 * Initialise the error output stream so as to ensure it will display
	 * unicode characters (when possible). Additionally, extract version
	 * information from the enclosing jar file.
	 */
	static {
		try {
			errout = new PrintStream(System.err, true, "UTF8");
		} catch(Exception e) {
			errout = System.err;
		}
	}
	
	public static void main(String[] args) {	
		boolean verbose = true;
		try {
			File file = new File(args[0]);			
			Lexer lexer = new Lexer(file);
			Parser parser = new Parser(file,lexer.scan());
			WycsFile wf = parser.parse();
			
		} catch (Throwable e) {
			errout.println("internal failure (" + e.getMessage() + ")");
			if (verbose) {
				e.printStackTrace(errout);
			}		
		}	
	}
}
