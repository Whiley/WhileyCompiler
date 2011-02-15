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

package wyone;

import java.io.*;
import java.util.*;

import wyil.util.SyntaxError;
import wyone.core.*;
import wyone.io.*;

/**
 * This class provides a simple text interface to the Solver, allowing simple
 * conditions to be checked for satisfiability.
 * 
 * @author djp
 * 
 */
public class Main {
		
	public static void main(String[] args) {	
		try {
			try {
				if(args.length == 0) {
					System.out.println("usage: java wyone.Main <spec-file>");
					System.exit(1);
				} 				
				
				boolean debug = false;			
				int fileArgsBegin = 0;
				for (int i = 0; i != args.length; ++i) {
					if (args[i].startsWith("-")) {
						String arg = args[i];
						if (arg.equals("-debug")) {
							debug = true;
						} else {
							throw new RuntimeException("Unknown option: " + args[i]);
						}

						fileArgsBegin = i + 1;
					}
				}
				
				long start = System.currentTimeMillis();
				
				String specfile = args[fileArgsBegin];
				SpecLexer lexer = new SpecLexer(specfile);
				SpecParser parser = new SpecParser(specfile,lexer.scan());
				SpecFile spec = parser.parse();
				new JavaFileWriter(System.out).write(spec);
				
				start = System.currentTimeMillis() - start;
				System.out.println("Time: " + start + "ms");				
				
			} catch(SyntaxError e) {				
				outputSourceError(e.filename(),e.start(),e.end(),e.getMessage());
			}
		} catch(IOException e) {
			System.err.println("i/o error: " + e.getMessage());
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
