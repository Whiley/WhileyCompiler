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

import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.tuple.*;
import wyone.theory.quantifier.*;
import wyone.theory.list.*;
import wyone.theory.set.*;
import wyone.theory.type.*;
import wyone.util.*;

/**
 * This class provides a simple text interface to the Solver, allowing simple
 * conditions to be checked for satisfiability.
 * 
 * @author djp
 * 
 */
public class Main {
	
	public static final CompoundHeuristic heuristic = new CompoundHeuristic(
			new NotEqualsHeuristic(), 
			new DisjunctHeuristic(),
			new BoundedNumberHeuristic(true, true), 			
			new BoundedSetHeuristic(),			
			new BoundedNumberHeuristic(true, false),
			new UnboundedNumberHeuristic(true),
			new UnboundedNumberHeuristic(false)		
	);

	public static final InferenceRule[] theories = {		
			new FourierMotzkinSolver(), 
			new CongruenceClosure(),
			new DisjunctInference(),
			new TypeClosure(), 
			new TupleClosure(), 
			new SubsetClosure(),
			new BoundedForallClosure(), 
			new ListForallClosure(),
			new ListLengthClosure(), 
			new LengthOfClosure() 
		};

	public static boolean checkUnsat(String input) {		
		Parser parser = new Parser(input);
		WFormula f = parser.parseInput();		
		Proof r = Solver.checkUnsatisfiable(1000, f,
				heuristic, theories);		
		return r instanceof Proof.Unsat;
	}
	
	public static boolean checkSat(String input) {		
		Parser parser = new Parser(input);
		WFormula f = parser.parseInput();
		Proof r = Solver.checkUnsatisfiable(1000, f,
				heuristic, theories);
		return r instanceof Proof.Sat;
	}
	
	public static void main(String[] args) {	
		try {
			try {
				if(args.length == 0) {
					System.out.println("usage: java Solve <input-file>");
					System.exit(1);
				} 				
				
				int timeout = 1000; // milli-seconds;
				boolean proof = false;				
				
				int fileArgsBegin = 0;
				for (int i = 0; i != args.length; ++i) {
					if (args[i].startsWith("-")) {
						String arg = args[i];
						if(arg.equals("-timeout")) {
							timeout = Integer.parseInt(args[++i]);
						} else if (arg.equals("-proof")) {
							proof = true;
						} else if (arg.equals("-debug")) {
							Solver.debug = true;
						} else {
							throw new RuntimeException("Unknown option: " + args[i]);
						}

						fileArgsBegin = i + 1;
					}
				}
				
				long start = System.currentTimeMillis();
				
				Parser parser = new Parser(new File(args[fileArgsBegin]));
				WFormula f = parser.parseInput();								
				System.out.println("Parsed: " + f);				
				Proof r = Solver.checkUnsatisfiable(timeout, f,
						heuristic, theories);
				
				if(r instanceof Proof.Unsat) {
					System.out.println("Unsatisfiable");
				} else if(r instanceof Proof.Sat) {
					Proof.Sat satp = (Proof.Sat) r;
					System.out.println("Satisfiable: " + satp.model());
				} else {
					System.out.println("Satisfiability Unknown");
				}												
				
				start = System.currentTimeMillis() - start;
				System.out.println("Time: " + start + "ms");				
				
			} catch(SyntaxError e) {				
				outputSourceError(e.filename(),e.start(),e.end(),e.getMessage());
			}
		} catch(IOException e) {
			System.err.println("i/o error: " + e.getMessage());
		}
	}	
			
	public static void printLine(int indent, int width) {
		indent(indent);
		
		for(int i=indent;i<width;++i) {
			System.out.print("=");
		}
		
		System.out.println();
	}
	
	public static void indent(int indent) {
		for(int i=0;i!=indent;++i) {
			System.out.print(" ");
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
