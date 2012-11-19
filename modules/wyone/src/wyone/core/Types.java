package wyone.core;

import java.io.*;
import java.util.*;
import java.math.BigInteger;
import wyautl.util.BigRational;
import wyautl.io.*;
import wyautl.core.*;
import wyone.io.*;
import wyone.core.*;
import static wyone.util.Runtime.*;

public final class Types {
	// term Not(^($4<Any|Void|Bool|Int|Real|String|Not(^($4))|Ref(^($4))|Meta(^($4))|Or(^({^($4)...}))|And(^({^($4)...}))|Set(^([^(bool),^({^($4)...})]))|Bag(^([^(bool),^({|^($4)...|})]))|List(^([^(bool),^([^($4)...])]))>))
	public final static int K_Not = 0;
	public final static int Not(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Not, r0));
	}

	// term And(^({^($9<Any|Void|Bool|Int|Real|String|Not(^($9))|Ref(^($9))|Meta(^($9))|Or(^({^($9)...}))|And(^({^($9)...}))|Set(^([^(bool),^({^($9)...})]))|Bag(^([^(bool),^({|^($9)...|})]))|List(^([^(bool),^([^($9)...])]))>)...}))
	public final static int K_And = 1;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// term Or(^({^($9<Any|Void|Bool|Int|Real|String|Not(^($9))|Ref(^($9))|Meta(^($9))|Or(^({^($9)...}))|And(^({^($9)...}))|Set(^([^(bool),^({^($9)...})]))|Bag(^([^(bool),^({|^($9)...|})]))|List(^([^(bool),^([^($9)...])]))>)...}))
	public final static int K_Or = 2;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// term Any
	public final static int K_Any = 3;
	public final static Automaton.Term Any = new Automaton.Term(K_Any);

	// term Void
	public final static int K_Void = 4;
	public final static Automaton.Term Void = new Automaton.Term(K_Void);

	// term Bool
	public final static int K_Bool = 5;
	public final static Automaton.Term Bool = new Automaton.Term(K_Bool);

	// term Int
	public final static int K_Int = 6;
	public final static Automaton.Term Int = new Automaton.Term(K_Int);

	// term Real
	public final static int K_Real = 7;
	public final static Automaton.Term Real = new Automaton.Term(K_Real);

	// term String
	public final static int K_String = 8;
	public final static Automaton.Term String = new Automaton.Term(K_String);

	// term Ref(^($4<Any|Void|Bool|Int|Real|String|Not(^($4))|Ref(^($4))|Meta(^($4))|Or(^({^($4)...}))|And(^({^($4)...}))|Set(^([^(bool),^({^($4)...})]))|Bag(^([^(bool),^({|^($4)...|})]))|List(^([^(bool),^([^($4)...])]))>))
	public final static int K_Ref = 9;
	public final static int Ref(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Ref, r0));
	}

	// term Meta(^($4<Any|Void|Bool|Int|Real|String|Not(^($4))|Ref(^($4))|Meta(^($4))|Or(^({^($4)...}))|And(^({^($4)...}))|Set(^([^(bool),^({^($4)...})]))|Bag(^([^(bool),^({|^($4)...|})]))|List(^([^(bool),^([^($4)...])]))>))
	public final static int K_Meta = 10;
	public final static int Meta(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Meta, r0));
	}

	// term Term(^([^(string),^($10<Any|Void|Bool|Int|Real|String|Not(^($10))|Ref(^($10))|Meta(^($10))|Or(^({^($10)...}))|And(^({^($10)...}))|Set(^([^(bool),^({^($10)...})]))|Bag(^([^(bool),^({|^($10)...|})]))|List(^([^(bool),^([^($10)...])]))>)]))
	public final static int K_Term = 11;
	public final static int Term(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}
	public final static int Term(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}

	// term Nominal(^([^(string),^($10<Any|Void|Bool|Int|Real|String|Not(^($10))|Ref(^($10))|Meta(^($10))|Or(^({^($10)...}))|And(^({^($10)...}))|Set(^([^(bool),^({^($10)...})]))|Bag(^([^(bool),^({|^($10)...|})]))|List(^([^(bool),^([^($10)...])]))>)]))
	public final static int K_Nominal = 12;
	public final static int Nominal(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}
	public final static int Nominal(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}

	// term Fun(^([^($10<Any|Void|Bool|Int|Real|String|Not(^($10))|Ref(^($10))|Meta(^($10))|Or(^({^($10)...}))|And(^({^($10)...}))|Set(^([^(bool),^({^($10)...})]))|Bag(^([^(bool),^({|^($10)...|})]))|List(^([^(bool),^([^($10)...])]))>),^($10)]))
	public final static int K_Fun = 13;
	public final static int Fun(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}
	public final static int Fun(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}

	// term Set(^([^(bool),^({^($15<Any|Void|Bool|Int|Real|String|Not(^($15))|Ref(^($15))|Meta(^($15))|Or(^({^($15)...}))|And(^({^($15)...}))|Set(^([^(bool),^({^($15)...})]))|Bag(^([^(bool),^({|^($15)...|})]))|List(^([^(bool),^([^($15)...])]))>)...})]))
	public final static int K_Set = 14;
	public final static int Set(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}
	public final static int Set(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}

	// term Bag(^([^(bool),^({|^($15<Any|Void|Bool|Int|Real|String|Not(^($15))|Ref(^($15))|Meta(^($15))|Or(^({^($15)...}))|And(^({^($15)...}))|Set(^([^(bool),^({^($15)...})]))|Bag(^([^(bool),^({|^($15)...|})]))|List(^([^(bool),^([^($15)...])]))>)...|})]))
	public final static int K_Bag = 15;
	public final static int Bag(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}
	public final static int Bag(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}

	// term List(^([^(bool),^([^($15<Any|Void|Bool|Int|Real|String|Not(^($15))|Ref(^($15))|Meta(^($15))|Or(^({^($15)...}))|And(^({^($15)...}))|Set(^([^(bool),^({^($15)...})]))|Bag(^([^(bool),^({|^($15)...|})]))|List(^([^(bool),^([^($15)...])]))>)...])]))
	public final static int K_List = 16;
	public final static int List(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}
	public final static int List(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}

	public static boolean reduce(Automaton automaton) {
		boolean result = false;
		boolean changed = true;
		while(changed) {
			changed = false;
			for(int i=0;i<automaton.nStates();++i) {
				if(numSteps++ > MAX_STEPS) { return result; } // bail out
				if(automaton.get(i) == null) { continue; }
			}
			result |= changed;
		}
		return result;
	}
	public static boolean infer(Automaton automaton) {
		reset();
		boolean result = false;
		boolean changed = true;
		reduce(automaton);
		while(changed) {
			changed = false;
			for(int i=0;i<automaton.nStates();++i) {
				if(numSteps > MAX_STEPS) { return result; } // bail out
				if(automaton.get(i) == null) { continue; }
			}
			result |= changed;
		}
		return result;
	}
	// =========================================================================
	// Type Tests
	// =========================================================================

	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// Not(^($4<Any|Void|Bool|Int|Real|String|Not(^($4))|Ref(^($4))|Meta(^($4))|Or(^({^($4)...}))|And(^({^($4)...}))|Set(^([^(bool),^({^($4)...})]))|Bag(^([^(bool),^({|^($4)...|})]))|List(^([^(bool),^([^($4)...])]))>))
		Schema.Term("Not",Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any))))),
		// And(^({^($9<Any|Void|Bool|Int|Real|String|Not(^($9))|Ref(^($9))|Meta(^($9))|Or(^({^($9)...}))|And(^({^($9)...}))|Set(^([^(bool),^({^($9)...})]))|Bag(^([^(bool),^({|^($9)...|})]))|List(^([^(bool),^([^($9)...])]))>)...}))
		Schema.Term("And",Schema.Set(true,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any)))))),
		// Or(^({^($9<Any|Void|Bool|Int|Real|String|Not(^($9))|Ref(^($9))|Meta(^($9))|Or(^({^($9)...}))|And(^({^($9)...}))|Set(^([^(bool),^({^($9)...})]))|Bag(^([^(bool),^({|^($9)...|})]))|List(^([^(bool),^([^($9)...])]))>)...}))
		Schema.Term("Or",Schema.Set(true,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any)))))),
		// Any
		Schema.Term("Any"),
		// Void
		Schema.Term("Void"),
		// Bool
		Schema.Term("Bool"),
		// Int
		Schema.Term("Int"),
		// Real
		Schema.Term("Real"),
		// String
		Schema.Term("String"),
		// Ref(^($4<Any|Void|Bool|Int|Real|String|Not(^($4))|Ref(^($4))|Meta(^($4))|Or(^({^($4)...}))|And(^({^($4)...}))|Set(^([^(bool),^({^($4)...})]))|Bag(^([^(bool),^({|^($4)...|})]))|List(^([^(bool),^([^($4)...])]))>))
		Schema.Term("Ref",Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any))))),
		// Meta(^($4<Any|Void|Bool|Int|Real|String|Not(^($4))|Ref(^($4))|Meta(^($4))|Or(^({^($4)...}))|And(^({^($4)...}))|Set(^([^(bool),^({^($4)...})]))|Bag(^([^(bool),^({|^($4)...|})]))|List(^([^(bool),^([^($4)...])]))>))
		Schema.Term("Meta",Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any))))),
		// Term(^([^(string),^($10<Any|Void|Bool|Int|Real|String|Not(^($10))|Ref(^($10))|Meta(^($10))|Or(^({^($10)...}))|And(^({^($10)...}))|Set(^([^(bool),^({^($10)...})]))|Bag(^([^(bool),^({|^($10)...|})]))|List(^([^(bool),^([^($10)...])]))>)]))
		Schema.Term("Term",Schema.List(true,Schema.String,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any)))))),
		// Nominal(^([^(string),^($10<Any|Void|Bool|Int|Real|String|Not(^($10))|Ref(^($10))|Meta(^($10))|Or(^({^($10)...}))|And(^({^($10)...}))|Set(^([^(bool),^({^($10)...})]))|Bag(^([^(bool),^({|^($10)...|})]))|List(^([^(bool),^([^($10)...])]))>)]))
		Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any)))))),
		// Fun(^([^($10<Any|Void|Bool|Int|Real|String|Not(^($10))|Ref(^($10))|Meta(^($10))|Or(^({^($10)...}))|And(^({^($10)...}))|Set(^([^(bool),^({^($10)...})]))|Bag(^([^(bool),^({|^($10)...|})]))|List(^([^(bool),^([^($10)...])]))>),^($10)]))
		Schema.Term("Fun",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any)))),Schema.Any)),
		// Set(^([^(bool),^({^($15<Any|Void|Bool|Int|Real|String|Not(^($15))|Ref(^($15))|Meta(^($15))|Or(^({^($15)...}))|And(^({^($15)...}))|Set(^([^(bool),^({^($15)...})]))|Bag(^([^(bool),^({|^($15)...|})]))|List(^([^(bool),^([^($15)...])]))>)...})]))
		Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any))))))),
		// Bag(^([^(bool),^({|^($15<Any|Void|Bool|Int|Real|String|Not(^($15))|Ref(^($15))|Meta(^($15))|Or(^({^($15)...}))|And(^({^($15)...}))|Set(^([^(bool),^({^($15)...})]))|Bag(^([^(bool),^({|^($15)...|})]))|List(^([^(bool),^([^($15)...])]))>)...|})]))
		Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any))))))),
		// List(^([^(bool),^([^($15<Any|Void|Bool|Int|Real|String|Not(^($15))|Ref(^($15))|Meta(^($15))|Or(^({^($15)...}))|And(^({^($15)...}))|Set(^([^(bool),^({^($15)...})]))|Bag(^([^(bool),^({|^($15)...|})]))|List(^([^(bool),^([^($15)...])]))>)...])]))
		Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Or(Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String")), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Set(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Any))), Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Any)))))))
	});
	public static long MAX_STEPS = 50000;
	public static long numSteps = 0;
	public static long numReductions = 0;
	public static long numInferences = 0;
	public static long numMisinferences = 0;
	public static void reset() {
		numSteps = 0;
		numReductions = 0;
		numInferences = 0;
		numMisinferences = 0;
	}
	// =========================================================================
	// Main Method
	// =========================================================================

	public static void main(String[] args) throws IOException {
		try {
			PrettyAutomataReader reader = new PrettyAutomataReader(System.in,SCHEMA);
			PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);
			Automaton automaton = reader.read();
			System.out.print("PARSED: ");
			writer.write(automaton);
			System.out.println();
			infer(automaton);
			System.out.print("REWROTE: ");
			writer.write(automaton);
			writer.flush();
			System.out.println();
			System.out.println("(Reductions=" + numReductions + ", Inferences=" + numInferences + ", Misinferences=" + numMisinferences + ", steps = " + numSteps + ")");
		} catch(PrettyAutomataReader.SyntaxError ex) {
			System.err.println(ex.getMessage());
		}
	}
}
