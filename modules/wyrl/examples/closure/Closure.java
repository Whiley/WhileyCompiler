import java.io.*;
import java.util.*;
import java.math.BigInteger;
import wyautl.util.BigRational;
import wyautl.io.*;
import wyautl.core.*;
import wyautl.rw.*;
import wyrl.core.*;
import wyrl.util.Runtime;
import wyrl.util.AbstractRewriteRule;
import wyrl.util.Pair;

public final class Closure {
	// term True
	public final static int K_True = 0;
	public final static Automaton.Term True = new Automaton.Term(K_True);

	// term False
	public final static int K_False = 1;
	public final static Automaton.Term False = new Automaton.Term(K_False);

	// term Num(^int)
	public final static int K_Num = 2;
	public final static int Num(Automaton automaton, long r0) {
		int r1 = automaton.add(new Automaton.Int(r0));
		return automaton.add(new Automaton.Term(K_Num, r1));
	}
	public final static int Num(Automaton automaton, BigInteger r0) {
		int r1 = automaton.add(new Automaton.Int(r0));
		return automaton.add(new Automaton.Term(K_Num, r1));
	}

	// term Var(^string)
	public final static int K_Var = 3;
	public final static int Var(Automaton automaton, String r0) {
		int r1 = automaton.add(new Automaton.Strung(r0));
		return automaton.add(new Automaton.Term(K_Var, r1));
	}

	// term And(^{^BExpr...})
	public final static int K_And = 4;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// And({BExpr x})
	private final static class Reduction_0 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_0(Pattern pattern) { super(pattern,SCHEMA); }

		public boolean apply(Automaton automaton, Object _state) {
			Object[] state = (Object[]) _state;
			int r0 = (Integer) state[0];
			int r1 = (Integer) state[1]; // x
			if(r0 != r1) {
				automaton.rewrite(r0, r1);
				return true;
			}
			return false;
		}
	}
	// And({Bool b, BExpr xs...})
	private final static class Reduction_1 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_1(Pattern pattern) { super(pattern,SCHEMA); }

		public boolean apply(Automaton automaton, Object _state) {
			Object[] state = (Object[]) _state;
			int r0 = (Integer) state[0];
			int r1 = (Integer) state[1]; // b
			Automaton.Set r2 = (Automaton.Set) state[2]; // xs
			Automaton.Term r3 = False;
			Object r4 = (Object) automaton.get(r1);
			boolean r5 = r4.equals(r3);    // b eq False
			if(r5) {
				Automaton.Term r6 = False;
				int r7 = automaton.add(r6);
				if(r0 != r7) {
					automaton.rewrite(r0, r7);
					return true;
				}
			}
			Automaton.Int r8 = r2.lengthOf(); // |xs|
			Automaton.Int r9 = new Automaton.Int(0); // 0
			boolean r10 = r8.equals(r9);   // |xs| eq 0
			if(r10) {
				Automaton.Term r11 = True;
				int r12 = automaton.add(r11);
				if(r0 != r12) {
					automaton.rewrite(r0, r12);
					return true;
				}
			}
			int r13 = automaton.add(r2);
			Automaton.Term r14 = new Automaton.Term(K_And,r13);
			int r15 = automaton.add(r14);
			if(r0 != r15) {
				automaton.rewrite(r0, r15);
				return true;
			}
			return false;
		}
	}
	// term LessThan(^[^Expr,^Expr])
	public final static int K_LessThan = 5;
	public final static int LessThan(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_LessThan, r1));
	}
	public final static int LessThan(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_LessThan, r1));
	}

	// LessThan([Num(int x), Num(int y)])
	private final static class Reduction_2 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_2(Pattern pattern) { super(pattern,SCHEMA); }

		public boolean apply(Automaton automaton, Object _state) {
			Object[] state = (Object[]) _state;
			int r0 = (Integer) state[0];
			int r1 = (Integer) state[1]; // x
			int r2 = (Integer) state[2]; // y
			Automaton.Int r3 = (Automaton.Int) automaton.get(r1);
			Automaton.Int r4 = (Automaton.Int) automaton.get(r2);
			boolean r5 = r3.compareTo(r4)<0; // x lt y
			if(r5) {
				Automaton.Term r6 = True;
				int r7 = automaton.add(r6);
				if(r0 != r7) {
					automaton.rewrite(r0, r7);
					return true;
				}
			}
			Automaton.Term r8 = False;
			int r9 = automaton.add(r8);
			if(r0 != r9) {
				automaton.rewrite(r0, r9);
				return true;
			}
			return false;
		}
	}
	// LessThan([Expr e1, Expr e2])
	private final static class Reduction_3 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_3(Pattern pattern) { super(pattern,SCHEMA); }

		public boolean apply(Automaton automaton, Object _state) {
			Object[] state = (Object[]) _state;
			int r0 = (Integer) state[0];
			int r1 = (Integer) state[1]; // e1
			int r2 = (Integer) state[2]; // e2
			boolean r3 = r1 == r2;         // e1 eq e2
			if(r3) {
				Automaton.Term r4 = False;
				int r5 = automaton.add(r4);
				if(r0 != r5) {
					automaton.rewrite(r0, r5);
					return true;
				}
			}
			return false;
		}
	}
	// And({LessThan([Expr e1, Expr e2]) l1, LessThan([Expr e3, Expr e4]) l2, BExpr bs...})
	private final static class Inference_0 extends AbstractRewriteRule implements InferenceRule {

		public Inference_0(Pattern pattern) { super(pattern,SCHEMA); }

		public boolean apply(Automaton automaton, Object _state) {
			Object[] state = (Object[]) _state;
			int r0 = (Integer) state[0];
			int r1 = (Integer) state[1]; // e1
			int r2 = (Integer) state[2]; // e2
			int r3 = (Integer) state[3]; // l1
			int r4 = (Integer) state[4]; // e3
			int r5 = (Integer) state[5]; // e4
			int r6 = (Integer) state[6]; // l2
			Automaton.Set r7 = (Automaton.Set) state[7]; // bs
			boolean r8 = r2 == r4;         // e2 eq e3
			if(r8) {
				Automaton.List r9 = new Automaton.List(r1, r5); // [e1e4]
				int r10 = automaton.add(r9);
				Automaton.Term r11 = new Automaton.Term(K_LessThan,r10);
				int r12 = automaton.add(r11);
				Automaton.Set r13 = r7.append(r12); // bs append LessThan([e1e4])
				int r14 = automaton.add(r13);
				Automaton.Term r15 = new Automaton.Term(K_And,r14);
				int r16 = automaton.add(r15);
				if(r0 != r16) {
					automaton.rewrite(r0, r16);
					return true;
				}
			}
			return false;
		}
	}
	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// True
		Schema.Term("True"),
		// False
		Schema.Term("False"),
		// Num(^int)
		Schema.Term("Num",Schema.Int),
		// Var(^string)
		Schema.Term("Var",Schema.String),
		// And(^{^BExpr...})
		Schema.Term("And",Schema.Set(true)),
		// LessThan(^[^Expr,^Expr])
		Schema.Term("LessThan",Schema.List(true,Schema.Or(Schema.Term("Var",Schema.String), Schema.Term("Num",Schema.Int)),Schema.Any))
	});

	// =========================================================================
	// Types
	// =========================================================================

	// BExpr
	private static Type type0 = Runtime.Type("CpmDYkIEJ8JGs0bRQsI7zV3A8pXHYg3IYoZI35BKqRnG4PWta9EGZFjx5QQVKDO4XLQCH6RlHMgsp7Uxp3cCGPec5K545QnK5GJ8MS_pmPYkLQ3K3Tk8r5i4mQslHTgcr7vlr7xt6CDKMQeg0CL4aReZGc2");
	// Bool
	private static Type type1 = Runtime.Type("QomDYkIE38oQjlq5y3mE7lHHgco3AC0IJOJNgCMO3G_RpKLcn2");
	// int
	private static Type type2 = Runtime.Type("Fg0");
	// Expr
	private static Type type3 = Runtime.Type("SomDYkIE3K3Tk8r5y3mE7lHHgco7Alo7Ct3CDKMQeg0CL4aReZGcn2");

	// =========================================================================
	// Patterns
	// =========================================================================

	private final static Pattern pattern0 = new Pattern.Term("And",
		new Pattern.Set(false, new Pair[]{
			new Pair(new Pattern.Leaf(type0), "x")}),
		null);
	private final static Pattern pattern1 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type1), "b"), 
			new Pair(new Pattern.Leaf(type0), "xs")}),
		null);
	private final static Pattern pattern2 = new Pattern.Term("LessThan",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Term("Num",
				new Pattern.Leaf(type2),
				"x"),null), 
			new Pair(new Pattern.Term("Num",
				new Pattern.Leaf(type2),
				"y"),null)}),
		null);
	private final static Pattern pattern3 = new Pattern.Term("LessThan",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Leaf(type3), "e1"), 
			new Pair(new Pattern.Leaf(type3), "e2")}),
		null);
	private final static Pattern pattern4 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("LessThan",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3), "e1"), 
					new Pair(new Pattern.Leaf(type3), "e2")}),
				null), "l1"), 
			new Pair(new Pattern.Term("LessThan",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3), "e3"), 
					new Pair(new Pattern.Leaf(type3), "e4")}),
				null), "l2"), 
			new Pair(new Pattern.Leaf(type0), "bs")}),
		null);
	// =========================================================================
	// rules
	// =========================================================================

	public static final InferenceRule[] inferences = new InferenceRule[]{
		new Inference_0(pattern4)
	};
	public static final ReductionRule[] reductions = new ReductionRule[]{
		new Reduction_0(pattern0)		,
new Reduction_1(pattern1)		,
new Reduction_2(pattern2)		,
new Reduction_3(pattern3)
	};


	// =========================================================================
	// Main Method
	// =========================================================================

	public static void main(String[] args) throws IOException {
		try {
			PrettyAutomataReader reader = new PrettyAutomataReader(System.in,SCHEMA);
			PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);
			Automaton automaton = reader.read();
			System.out.print("PARSED: ");
			print(automaton);
			new SimpleRewriter(inferences,reductions).apply(automaton);
			System.out.print("REWROTE: ");
			print(automaton);
		} catch(PrettyAutomataReader.SyntaxError ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	static void print(Automaton automaton) {
		try {
			PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);
			writer.write(automaton);
			writer.flush();
			System.out.println();
		} catch(IOException e) { System.err.println("I/O error printing automaton"); }
	}
}
