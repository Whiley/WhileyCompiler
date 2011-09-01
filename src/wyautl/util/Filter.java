package wyautl.util;

import java.io.*;

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
				
				while(true) {
					Automata automata = reader.read();
					nread++;
					
					nwritten++;
					writer.write(automata);
				}				
			} catch(EOFException e) {
				
			}
			System.out.println("Reader " + nread + " automata.");
			System.out.println("Wrote " + nwritten + " automata.");
		} catch(IOException ex) {
			System.out.println("Exception: " + ex);
		}	
	}
}
