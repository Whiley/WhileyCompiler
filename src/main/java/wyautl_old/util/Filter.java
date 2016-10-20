// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyautl_old.util;

import java.io.*;
import java.util.*;

import wyautl_old.io.*;
import wyautl_old.lang.*;
import wyfs.io.BinaryInputStream;
import wyfs.io.BinaryOutputStream;

public class Filter {
	public static void main(String[] args) {
		boolean binaryIn = false;
		boolean binaryOut = false;
		InputStream input = System.in;
		OutputStream output = System.out;
		int index = 0;
		boolean reduce = false;
		boolean extract = false;
		boolean simplify = false;
		boolean minimise = false;
		boolean canonicalise = false;
		boolean verbose = false;

		try {
			while(index < args.length) {
				if(args[index].equals("-b")) {
					binaryIn=true;
					binaryOut=true;
				} else if(args[index].equals("-bin")) {
					binaryIn=true;
				} else if(args[index].equals("-bout")) {
					binaryOut=true;
				} else if(args[index].equals("-f")) {
					String filename = args[++index];
					input = new FileInputStream(filename);
				} else if(args[index].equals("-o")) {
					String filename = args[++index];
					output = new FileOutputStream(filename);
				} else if(args[index].equals("-r") || args[index].equals("-reduce")) {
					reduce=true;
				} else if(args[index].equals("-e") || args[index].equals("-extract")) {
					extract=true;
				} else if(args[index].equals("-s") || args[index].equals("-simplify")) {
					simplify=true;
				} else if(args[index].equals("-m") || args[index].equals("-minimise")) {
					minimise=true;
				}  else if(args[index].equals("-c") || args[index].equals("-canonicalise")) {
					canonicalise=true;
				} else if(args[index].equals("-v") || args[index].equals("-verbose")) {
					verbose = true;
				}
				index++;
			}

			GenericReader<Automaton> reader;
			GenericWriter<Automaton> writer;
			if(binaryIn) {
				BinaryInputStream bis = new BinaryInputStream(input);
				reader = new BinaryAutomataReader(bis);
			} else {
				reader = null; // TODO
			}
			if(binaryOut) {
				BinaryOutputStream bos = new BinaryOutputStream(output);
				writer = new BinaryAutomataWriter(bos);
			} else {
				writer = new TextAutomataWriter(output);
			}
			int nread = 0;
			int nwritten = 0;
			try {
				HashSet<Automaton> visited = new HashSet<Automaton>();
				while(true) {
					Automaton automaton = reader.read();
					nread++;

					if(extract) {
						automaton = Automata.extract(automaton,0);
					}

					if(minimise) {
						automaton = Automata.minimise(automaton);
					}

					if(canonicalise) {
						Automata.canonicalise(automaton,null);
					}

					if(!reduce || !visited.contains(automaton)) {
						nwritten++;
						writer.write(automaton);
						if(reduce) { visited.add(automaton); }
					}

					if(verbose) {
						System.err.print("\rRead " + nread + " automata, wrote " + nwritten+ ".");
					}
				}
			} catch(EOFException e) {

			}
			if(!verbose) {
				System.err.print("\rRead " + nread + " automata, wrote " + nwritten+ ".");
			}
		} catch(IOException ex) {
			System.out.println("Exception: " + ex);
		}
	}
}
