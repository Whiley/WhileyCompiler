package wyautl.util;

import java.io.*;
import java.util.*;

import wyautl.io.*;
import wyautl.lang.*;
import wyautl.lang.DefaultInterpretation.Value;
import wyjvm.io.BinaryInputStream;

public class Tester {
	
	public static ArrayList<Value> readModel(boolean binaryIn, String filename, boolean verbose)
			throws IOException {
		GenericReader<Automata> reader;
		if (binaryIn) {
			BinaryInputStream bis = new BinaryInputStream(new FileInputStream(
					filename));
			reader = new BinaryAutomataReader(bis);
		} else {
			reader = null; // TODO
		}

		ArrayList<Value> model = new ArrayList<Value>();
		try {
			int count = 0;
			while (true) {
				Automata automata = reader.read();
				Value value = DefaultInterpretation.construct(automata);
				model.add(value);
				if(verbose) {				
					System.err.print("\rRead " + count + " values.");				
				}
				count++;
			}
		} catch (EOFException eof) {

		}
		if(verbose) {				
			System.err.println();				
		}
		return model;
	}
	
	public static ArrayList<Automata> readAutomatas(boolean binaryIn, String filename, boolean verbose)
			throws IOException {
		GenericReader<Automata> reader;
		if (binaryIn) {
			BinaryInputStream bis = new BinaryInputStream(new FileInputStream(
					filename));
			reader = new BinaryAutomataReader(bis);
		} else {
			reader = null; // TODO
		}

		ArrayList<Automata> automatas = new ArrayList<Automata>();
		try {
			int count = 0;
			while (true) {			
				automatas.add(reader.read());
				if(verbose) {				
					System.err.print("\rRead " + count + " automatas.");				
				}
				count++;
			}
		} catch (EOFException eof) {

		}
		if(verbose) {				
			System.err.println();				
		}
		return automatas;
	}
	
	public static BitSet accepts(Interpretation interpretation, Automata automata, ArrayList<Value> model) {
		BitSet accepted = new BitSet(model.size());
		for(int i=0;i!=model.size();++i) {
			Value value = model.get(i);
			if(interpretation.accepts(automata,value)) {
				accepted.set(i);
			}
		}
		return accepted;
	}
	
	/**
	 * The purpose of this test is to check that minimised automatas accept the
	 * same set of values as non-minimised ones.
	 * 
	 * @param model
	 * @param automatas
	 */
	public static void minimiseTest(Interpretation interpretation,
			ArrayList<Value> model, ArrayList<Automata> automatas, boolean verbose) {
		int count = 0;
		int size = automatas.size();
		for (Automata automata : automatas) {			
			Automata minimised = Automatas.minimise(automata);			
			BitSet oldAccepts = accepts(interpretation, automata, model);
			BitSet newAccepts = accepts(interpretation, minimised, model);
			if (!oldAccepts.equals(newAccepts)) {
				System.out.println("Possible Minimisation Unsoundness: "
						+ automata + " => " + minimised);
			}
			// TODO: add subsumption
			count++;
			if(verbose) {				
				System.err.print("\rChecked " + count + " / " + size + " automatas.");				
			}			
		}
	}
	
	public static void main(String[] args) {
		boolean binaryIn = false;
		boolean verbose = false;
				
		int index = 0;
		try {
			while(index < args.length && args[index].charAt(0) == '-') {
				if(args[index].equals("-b")) {
					binaryIn=true;					
				} else if(args[index].equals("-v") || args[index].equals("-verbose")) {
					verbose = true;
				} 
				index = index + 1;
			} 
			
			if((index+2) > args.length) {
				System.out.println("usage: Tester [options] model.file automata.file");
				System.exit(1);
			}
			
			ArrayList<Value> model = readModel(binaryIn,args[index],verbose);
			ArrayList<Automata> automatas = readAutomatas(binaryIn,args[index+1],verbose);
			
			minimiseTest(new DefaultInterpretation(),model,automatas,verbose);
			
		} catch(IOException ex) {
			System.out.println("Exception: " + ex);
		}
	}
}
