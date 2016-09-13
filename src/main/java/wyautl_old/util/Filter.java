// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
