package wyc;

import java.io.FileInputStream;
import java.util.List;

import wyc.io.*;
import wyc.lang.WhileyFile;

/**
 * Simple tool for converting old-style Whiley programs to new-style syntax.
 * 
 * @author David J. Pearce
 *
 */
public class Convert {
	public static void main(String[] args) {
		try {
			FileInputStream fin = new FileInputStream(args[0]);
			WhileyFileLexer wlexer = new WhileyFileLexer(fin);

			List<WhileyFileLexer.Token> tokens = new WhileyFileFilter().filter(wlexer
					.scan());

			WhileyFileParser wfr = new WhileyFileParser(args[0], tokens);
			WhileyFile wf = wfr.read();
			new WhileyFilePrinter(System.out).print(wf);
		} catch(Exception e) {
			System.err.println("ERROR: " + e.toString());
			e.printStackTrace();
			System.exit(1);;
		}
	}
}
