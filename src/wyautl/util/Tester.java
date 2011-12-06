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

package wyautl.util;

import java.io.*;
import java.util.*;

import wyautl.io.*;
import wyautl.lang.*;
import wyautl.lang.DefaultInterpretation.Term;
import wyjvm.io.BinaryInputStream;

public class Tester {
	
	public static ArrayList<Term> readModel(GenericReader<Automaton> reader, boolean verbose)
			throws IOException {
		
		ArrayList<Term> model = new ArrayList<Term>();
		try {
			int count = 0;
			while (true) {
				Automaton automata = reader.read();
				Term value = DefaultInterpretation.construct(automata);
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
	
	public static ArrayList<Automaton> readAutomatas(GenericReader<Automaton> reader, boolean verbose)
			throws IOException {
		
		ArrayList<Automaton> automatas = new ArrayList<Automaton>();
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
	
	public static BitSet accepts(Interpretation interpretation, Automaton automata, ArrayList<Term> model) {
		BitSet accepted = new BitSet(model.size());
		for(int i=0;i!=model.size();++i) {
			Term value = model.get(i);
			if(interpretation.accepts(automata,value)) {
				accepted.set(i);
			}
		}
		return accepted;
	}
	
	public static boolean isSubsumed(Automaton a1, Automaton a2) {
		DefaultSubsumption relation = new DefaultSubsumption(a1, a2);
		Automata.computeFixpoint(relation);
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
			ArrayList<Term> model, ArrayList<Automaton> automatas, boolean verbose) {
		int count = 0;
		int size = automatas.size();
		for (Automaton automata : automatas) {			
			Automaton minimised = Automata.minimise(automata);			
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
			ArrayList<Term> model, ArrayList<Automaton> automatas, boolean verbose) {
		int count = 0;
		int size = automatas.size();
		for (int i=0;i!=size;++i) {	
			Automaton ai = automatas.get(i);
			
			if(nonAccepting(ai)) {
				continue;
			}
			
			BitSet oldAccepts = accepts(interpretation, ai, model);			
			for (int j=i+1;j!=size;++j) {	
				Automaton aj = automatas.get(j);
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
	
	public static boolean nonAccepting(Automaton a) {
		for(int i=0;i!=a.size();++i) {
			if(a.states[i].children.length == 0) {
				return false;
			}
		}
		return true;
	}
	
	public static void printAccepts(BitSet accepts, ArrayList<Term> model) {
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
						
							
			ArrayList<Term> model = readModel(
					new BinaryAutomataReader(new BinaryInputStream(
							new FileInputStream(args[index]))), verbose);
			ArrayList<Automaton> automatas = readAutomatas(
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
