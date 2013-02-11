package wycs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import wycs.lang.*;
import wycs.solver.Verifier;
import wycs.io.*;
import wyone.util.SyntaxError;

public class WycsMain {
	public static PrintStream errout;
	
	/**
	 * Initialise the error output stream so as to ensure it will display
	 * unicode characters (when possible). Additionally, extract version
	 * information from the enclosing jar file.
	 */
	static {
		try {
			errout = new PrintStream(System.err, true, "UTF-8");
		} catch(Exception e) {
			errout = System.err;
			System.err.println("Warning: terminal does not support unicode");
		}
	}
	
	public static boolean run(String[] args) throws IOException {
		boolean verbose = false;
		File file = new File(args[0]);			
		Lexer lexer = new Lexer(file);
		Parser parser = new Parser(file.getName(),lexer.scan());
		WycsFile wycs = parser.parse();
		new WycsFileWriter(System.out).write(wycs);
		List<Boolean> results = new Verifier(verbose).verify(wycs);
		for(int i = 0;i!=results.size();++i) {
			if(!results.get(i)) {
				return false;
			} 
		}		
		return true;
	}
	
	public static void main(String[] args) {
		boolean verbose = true;
		try {
			try {				
				File file = new File(args[0]);			
				Lexer lexer = new Lexer(file);
				Parser parser = new Parser(file.getName(),lexer.scan());
				WycsFile wycs = parser.parse();
				new WycsFileWriter(System.out).write(wycs);
				List<Boolean> results = new Verifier(verbose).verify(wycs);
				for(int i = 0;i!=results.size();++i) {
					if(results.get(i)) {
						System.out.println("Valid");
					} else {
						System.out.println("Invalid");
					}
				}
			} catch (SyntaxError e) {
				outputSourceError(e.filename(), e.start(), e.end(),
						e.getMessage());

				if (verbose) {
					e.printStackTrace(System.err);
				}
			}
		} catch (Throwable e) {
			errout.println("internal failure (" + e.getMessage() + ")");
			if (verbose) {
				e.printStackTrace(errout);
			}		
		}	
	}
	
	/**
	 * This method simply reads in the input file, and prints out a
	 * given line of text, with little markers (i.e. '^') placed
	 * underneath a portion of it.  
	 *
	 * @param fileArg - the name of the file whose line to print
	 * @param start - the start position of the offending region.
	 * @param end - the end position of the offending region.
	 * @param message - the message to print about the error
	 */
	public static void outputSourceError(String fileArg, int start, int end,
			String message) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileArg));
						
		int line = 0;
		String lineText = "";
		while (in.ready() && start >= lineText.length()) {
			start -= lineText.length() + 1;
			end -= lineText.length() + 1;
			lineText = in.readLine();						
			line = line + 1;			
		}		
								
		System.err.println(fileArg + ":" + line + ": " + message);
		System.err.println(lineText);	
		for (int i = 0; i <= start; ++i) {
			if (lineText.charAt(i) == '\t') {
				System.err.print("\t");
			} else {
				System.err.print(" ");
			}
		}				
		for (int i = start; i <= end; ++i) {		
			System.err.print("^");
		}
		System.err.println("");		
	}		
}
