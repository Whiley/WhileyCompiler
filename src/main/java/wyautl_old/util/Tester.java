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
import wyautl_old.lang.DefaultInterpretation.Term;
import wyfs.io.BinaryInputStream;

public class Tester {

	public static ArrayList<Term> readModel(GenericReader<Automaton> reader, boolean verbose)
			throws IOException {

		ArrayList<Term> model = new ArrayList<Term>();
		try {
			int count = 0;
			while (true) {
				Automaton automaton = reader.read();
				Term value = DefaultInterpretation.construct(automaton);
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

		ArrayList<Automaton> automata = new ArrayList<Automaton>();
		try {
			int count = 0;
			while (true) {
				automata.add(reader.read());
				if(verbose) {
					System.err.print("\rRead " + count + " automata.");
				}
				count++;
			}
		} catch (EOFException eof) {

		}
		if(verbose) {
			System.err.println();
		}
		return automata;
	}

	public static BitSet accepts(Interpretation interpretation, Automaton automaton, ArrayList<Term> model) {
		BitSet accepted = new BitSet(model.size());
		for(int i=0;i!=model.size();++i) {
			Term value = model.get(i);
			if(interpretation.accepts(automaton,value)) {
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
	 * The purpose of this test is to check that minimised automata accept the
	 * same set of values as non-minimised ones.
	 *
	 * @param model
	 * @param automata
	 */
	public static void minimiseTest(Interpretation interpretation,
			ArrayList<Term> model, ArrayList<Automaton> automata, boolean verbose) {
		int count = 0;
		int size = automata.size();
		for (Automaton automaton : automata) {
			Automaton minimised = Automata.minimise(automaton);
			BitSet oldAccepts = accepts(interpretation, automaton, model);
			BitSet newAccepts = accepts(interpretation, minimised, model);
			if (!oldAccepts.equals(newAccepts)) {
				System.out.println("Possible minimisation unsoundness: "
						+ automaton + " => " + minimised);
			}
			if(!isSubsumed(minimised,automaton)) {
				System.out.println("Possible subsumption unsoundness: "
						+ automaton + " :> " + minimised);
			} else 	if(!isSubsumed(automaton,minimised)) {
				System.out.println("Possible subsumption unsoundness: "
						+ automaton + " <: " + minimised);
			}

			count++;
			if(verbose) {
				System.err.print("\rChecked " + count + " / " + size + " automata.");
			}
		}
	}

	public static void canonicaliseTest(Interpretation interpretation,
			ArrayList<Term> model, ArrayList<Automaton> automata, boolean verbose) {
		int count = 0;
		int size = automata.size();
		for (int i=0;i!=size;++i) {
			Automaton ai = automata.get(i);

			if(nonAccepting(ai)) {
				continue;
			}

			BitSet oldAccepts = accepts(interpretation, ai, model);
			for (int j=i+1;j!=size;++j) {
				Automaton aj = automata.get(j);
				BitSet newAccepts = accepts(interpretation, aj, model);
				if(oldAccepts.equals(newAccepts) && !ai.equals(aj)) {
					System.out.println("Possible canonicalisation unsoundness: "
							+ ai + " != " + aj + " (" + newAccepts.cardinality() + ")");
					printAccepts(newAccepts,model);
				}
			}
			count++;
			if(verbose) {
				System.err.print("\rChecked " + count + " / " + size + " automata.");
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
				System.out.println("usage: Tester [options] model.file automaton.file");
				System.exit(1);
			}


			ArrayList<Term> model = readModel(
					new BinaryAutomataReader(new BinaryInputStream(
							new FileInputStream(args[index]))), verbose);
			ArrayList<Automaton> automata = readAutomatas(
					new BinaryAutomataReader(new BinaryInputStream(
							new FileInputStream(args[index + 1]))), verbose);

			if(minimiseTest) {
				minimiseTest(new DefaultInterpretation(),model,automata,verbose);
			}
			if(canonicalTest) {
				canonicaliseTest(new DefaultInterpretation(),model,automata,verbose);
			}

		} catch(IOException ex) {
			System.out.println("Exception: " + ex);
		}
	}
}
