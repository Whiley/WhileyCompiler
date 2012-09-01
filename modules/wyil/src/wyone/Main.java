// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce.
// Munged 2012 by Art Protin

package wyone;

import java.io.*;
import java.util.*;

// import wyone.util.SyntaxError;
import wyone.spec.SpecLexer;
import wyone.spec.SpecParser;
import wyone.util.*;
import wyone.core.*;
//import wyone.core.SpecFile.TypeDecl;
import wyone.core.WyoneFile.*;
import wyone.io.*;

/**
 * This class provides a simple text interface to the Solver, allowing simple
 * conditions to be checked for satisfiability.
 * 
 * @author djp
 * 
 */
public class Main {

	private static Map<String, String> optionTags;
	private static Map<String, String> optionStuff;
	static {
		optionTags = new HashMap<String, String>();
		optionStuff = new HashMap<String, String>();
		optionTags.put("-verbose", "false");
		optionStuff.put("-verbose", "");
		optionTags.put("-debug", "false");
		optionStuff.put("-debug", "");
		optionTags.put("-pkg", "true");
		optionStuff.put("-pkg", "");
		optionTags.put("-out", "true");
		optionStuff.put("-out", "");
	}

	private static boolean optBool(String nam) {
		if (! optionStuff.containsKey(nam)) {
			return false;
		}
		String val = optionStuff.get(nam);
		if ((val != null) && (val != "")) {
			return true;
		}
		return false;
	}

	private static String optString(String nam) {
		if (! optionStuff.containsKey(nam)) {
			return null;
		}
		return optionStuff.get(nam);
	}

	public static void main(String[] args) {
		String arg;
		if(args.length == 0) {
			System.out.println("usage: java wyone.Main <options> <spec-file>");
			System.exit(1);
		} 				
		LinkedList<String> waitParm = new LinkedList<String>();
		LinkedList<String> waitFileName = new LinkedList<String>();
		for (int i = 0; i < args.length; ++i) {
			arg = args[i];
			if (optionTags.containsKey(arg)) {
				if (optionTags.get(arg) != "false") {
					waitParm.add(arg);
				} else {
					optionStuff.put(arg, "true");
				}
				continue;
			}
			if (arg.startsWith("-")) {
				throw new RuntimeException("Unknown option: " + arg);
			}
			if (waitParm.size() > 0) {
				optionStuff.put(waitParm.remove(), arg);
			} else {
				waitFileName.add(arg);
			}
		}
		if (waitFileName.size() <= 0) {
			throw new RuntimeException("No filenames given");
		}
		try {
			digestAll(waitFileName);
		} catch(IOException e) {
			System.err.println("I/O error - " + e.getMessage());
		}
	}

	private static void digestAll(LinkedList<String> names) throws IOException {		
		
		PrintStream oFile = null;
		String oName = optString("-out");		

		if (oName.length() > 0) {
			;
			try {
				oFile = new PrintStream(oName);
			} catch(IOException e) {
				System.err.println("i/o error: " + e.getMessage());
			}
		}
		if (oFile == null) {
			oFile = System.out;
		}
		long start = System.currentTimeMillis();
		
		for(String specfile : names) {
			try {
				SpecLexer lexer = new SpecLexer(specfile);
				SpecParser parser = new SpecParser(specfile, lexer.scan());
				WyoneFile spec = parser.parse();
				new TypeChecker().check(spec);			
				//new SpecFileWriter(oFile).write(spec);
				new JavaFileWriter(oFile).write(spec);				
			} catch(SyntaxError e) {
				outputSourceError(e.filename(),e.start(),e.end(),e.getMessage());
				
				if(optBool("-verbose")) {
					e.printStackTrace(System.err);
				}			
			} 
		}
								
		start = System.currentTimeMillis() - start;
		System.err.println("Time: " + start + "ms");
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
