package wyautl.util;

import java.io.*;
import java.util.*;

import wyautl.io.*;
import wyautl.lang.*;
import wyautl.lang.DefaultInterpretation.Value;
import wyjvm.io.BinaryInputStream;

public class Tester {
	
	public static ArrayList<Value> readModel(GenericReader<Automata> reader, boolean verbose)
			throws IOException {
		
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
	
	public static ArrayList<Automata> readAutomatas(GenericReader<Automata> reader, boolean verbose)
			throws IOException {
		
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
	
	public static boolean isSubsumed(Automata a1, Automata a2) {
		DefaultSubsumption relation = new DefaultSubsumption(a1, a2);
		Automatas.computeFixpoint(relation);
		return relation.isRelated(0, 0);
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
				System.out.println("Possible minimisation unsoundness: "
						+ automata + " => " + minimised);
			}
			if(!isSubsumed(minimised,automata)) {
				System.out.println("Possible subsumption unsoundness: "
						+ automata + " :> " + minimised);
			} else 	if(!isSubsumed(automata,minimised)) {
				System.out.println("Possible subsumption unsoundness: "
						+ automata + " <: " + minimised);
			}
			
			count++;
			if(verbose) {				
				System.err.print("\rChecked " + count + " / " + size + " automatas.");				
			}			
		}
	}
	
	public static void canonicaliseTest(Interpretation interpretation,
			ArrayList<Value> model, ArrayList<Automata> automatas, boolean verbose) {
		int count = 0;
		int size = automatas.size();
		for (int i=0;i!=size;++i) {	
			Automata ai = automatas.get(i);
			
			if(nonAccepting(ai)) {
				continue;
			}
			
			BitSet oldAccepts = accepts(interpretation, ai, model);			
			for (int j=i+1;j!=size;++j) {	
				Automata aj = automatas.get(j);
				BitSet newAccepts = accepts(interpretation, aj, model);
				if(oldAccepts.equals(newAccepts) && !ai.equals(aj)) {
					System.out.println("Possible canonicalisation unsoundness: "
							+ ai + " != " + aj + " (" + newAccepts.cardinality() + ")");
					printAccepts(newAccepts,model);
				}
			}
			count++;
			if(verbose) {				
				System.err.print("\rChecked " + count + " / " + size + " automatas.");				
			}
		}
	}	
	
	public static boolean nonAccepting(Automata a) {
		for(int i=0;i!=a.size();++i) {
			if(a.states[i].children.length == 0) {
				return false;
			}
		}
		return true;
	}
	
	public static void printAccepts(BitSet accepts, ArrayList<Value> model) {
		System.out.print("{");
		boolean firstTime=true;
		for (int i = accepts.nextSetBit(0); i >= 0; i = accepts.nextSetBit(i+1)) {
			if(!firstTime) {
				System.out.print(",");
			}
			firstTime=false;
			System.out.print(model.get(i));
		}
		System.out.println("}");
	}
	
	public static void main(String[] args) {		
		boolean verbose = false;
		boolean minimiseTest = false;
		boolean canonicalTest = false;
				
		int index = 0;
		try {
			while(index < args.length && args[index].charAt(0) == '-') {
				if(args[index].equals("-v") || args[index].equals("-verbose")) {
					verbose = true;
				} else if(args[index].equals("-m") || args[index].equals("-minimise")) {
					minimiseTest=true;
				}  else if(args[index].equals("-c") || args[index].equals("-canonicalise")) {
					canonicalTest=true;
				} 
				index = index + 1;
			} 
			
			if((index+2) > args.length) {
				System.out.println("usage: Tester [options] model.file automata.file");
				System.exit(1);
			}
						
							
			ArrayList<Value> model = readModel(
					new BinaryAutomataReader(new BinaryInputStream(
							new FileInputStream(args[index]))), verbose);
			ArrayList<Automata> automatas = readAutomatas(
					new BinaryAutomataReader(new BinaryInputStream(
							new FileInputStream(args[index + 1]))), verbose);
			
			if(minimiseTest) {
				minimiseTest(new DefaultInterpretation(),model,automatas,verbose);
			}
			if(canonicalTest) {
				canonicaliseTest(new DefaultInterpretation(),model,automatas,verbose);
			}
			
		} catch(IOException ex) {
			System.out.println("Exception: " + ex);
		}
	}
}
