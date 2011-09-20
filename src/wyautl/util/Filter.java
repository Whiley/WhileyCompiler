package wyautl.util;

import java.io.*;
import java.util.*;
import wyjvm.io.*;
import wyautl.io.*;
import wyautl.lang.*;

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
			
			GenericReader<Automata> reader;
			GenericWriter<Automata> writer;
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
				HashSet<Automata> visited = new HashSet<Automata>();
				while(true) {
					Automata automata = reader.read();					
					nread++;
					
					if(extract) {						
						automata = Automatas.extract(automata,0);
					}
					
					if(minimise) {
						automata = Automatas.minimise(automata);
					}
					
					if(canonicalise) {
						Automatas.canonicalise(automata,null);
					}
					
					if(!reduce || !visited.contains(automata)) {					
						nwritten++;
						writer.write(automata);
						if(reduce) { visited.add(automata); }
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
