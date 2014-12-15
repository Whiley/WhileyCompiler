package wyrl.core;

import java.io.*;
import java.util.*;
import java.math.BigInteger;
import wyautl.util.BigRational;
import wyautl.io.*;
import wyautl.core.*;
import wyautl.rw.*;
import wyrl.core.*;
import wyrl.util.Runtime;
import wyrl.util.Pair;
import wyrl.util.AbstractRewriteRule;

public final class Types {
	// term $4<Not($2<^Type<$4|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>)>
	public final static int K_Not = 0;
	public final static int Not(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Not, r0));
	}

	// 
	private final static class Reduction_0 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_0(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Not) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				if(Runtime.accepts(type0,automaton,automaton.get(r1), SCHEMA)) {
					int[] state = {r0, r1};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Term r2 = Void;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_1 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_1(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Not) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				if(Runtime.accepts(type1,automaton,automaton.get(r1), SCHEMA)) {
					int[] state = {r0, r1};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Term r2 = Any;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_2 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_2(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Not) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				if(s1.kind == K_Or) {
					Automaton.Term t1 = (Automaton.Term) s1;
					int r2 = t1.contents;
					Automaton.State s2 = automaton.get(r2);
					Automaton.Collection c2 = (Automaton.Collection) s2;
					int[] state = {r0, r1, r2, 0};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Collection s2 = (Automaton.Collection) automaton.get(state[2]);
			int[] s2children = new int[s2.size() - 0];
			for(int s2i=0, s2j=0; s2i != s2.size();++s2i) {
				s2children[s2j++] = s2.get(s2i);
			}
			Automaton.Set r3 = new Automaton.Set(s2children);
			Automaton.List t4 = new Automaton.List();
			for(int i5=0;i5<r3.size();i5++) {
				int r5 = (int) r3.get(i5);
				Automaton.Term r6 = new Automaton.Term(K_Not, r5);
				int r7 = automaton.add(r6);
				t4.add(r7);
			}
			Automaton.Set r4 = new Automaton.Set(t4.toArray());
			int r8 = automaton.add(r4);
			Automaton.Term r9 = new Automaton.Term(K_And, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_3 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_3(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Not) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				if(s1.kind == K_And) {
					Automaton.Term t1 = (Automaton.Term) s1;
					int r2 = t1.contents;
					Automaton.State s2 = automaton.get(r2);
					Automaton.Collection c2 = (Automaton.Collection) s2;
					int[] state = {r0, r1, r2, 0};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Collection s2 = (Automaton.Collection) automaton.get(state[2]);
			int[] s2children = new int[s2.size() - 0];
			for(int s2i=0, s2j=0; s2i != s2.size();++s2i) {
				s2children[s2j++] = s2.get(s2i);
			}
			Automaton.Set r3 = new Automaton.Set(s2children);
			Automaton.List t4 = new Automaton.List();
			for(int i5=0;i5<r3.size();i5++) {
				int r5 = (int) r3.get(i5);
				Automaton.Term r6 = new Automaton.Term(K_Not, r5);
				int r7 = automaton.add(r6);
				t4.add(r7);
			}
			Automaton.Set r4 = new Automaton.Set(t4.toArray());
			int r8 = automaton.add(r4);
			Automaton.Term r9 = new Automaton.Term(K_Or, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $7<And($5<^{$2<^Type<$7|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or($5)|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>...}>)>
	public final static int K_And = 1;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// 
	private final static class Reduction_4 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_4(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() == 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						int[] state = {r0, r1, r2, r3};
						activations.add(new Activation(this,null,state));
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // t
			int r3 = state[3];
			if(r0 != r2) {
				return automaton.rewrite(r0, r2);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_5 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_5(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_And) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.Collection c4 = (Automaton.Collection) s4;
							int[] state = {r0, r1, r2, r3, r4, 0, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			Automaton.Collection s4 = (Automaton.Collection) automaton.get(state[4]);
			int[] s4children = new int[s4.size() - 0];
			for(int s4i=0, s4j=0; s4i != s4.size();++s4i) {
				s4children[s4j++] = s4.get(s4i);
			}
			Automaton.Set r5 = new Automaton.Set(s4children);
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r6 = new Automaton.Set(s1children);
			Automaton.Set r7 = r5.append(r6); // xs append ys
			int r8 = automaton.add(r7);
			Automaton.Term r9 = new Automaton.Term(K_And, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_6 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_6(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Or) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.Collection c4 = (Automaton.Collection) s4;
							int[] state = {r0, r1, r2, r3, r4, 0, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			Automaton.Collection s4 = (Automaton.Collection) automaton.get(state[4]);
			int[] s4children = new int[s4.size() - 0];
			for(int s4i=0, s4j=0; s4i != s4.size();++s4i) {
				s4children[s4j++] = s4.get(s4i);
			}
			Automaton.Set r5 = new Automaton.Set(s4children);
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r6 = new Automaton.Set(s1children);
			Automaton.List t7 = new Automaton.List();
			for(int i8=0;i8<r5.size();i8++) {
				int r8 = (int) r5.get(i8);
				Automaton.Set r9 = r6.appendFront(r8); // x append ys
				int r10 = automaton.add(r9);
				Automaton.Term r11 = new Automaton.Term(K_And, r10);
				int r12 = automaton.add(r11);
				t7.add(r12);
			}
			Automaton.Set r7 = new Automaton.Set(t7.toArray());
			int r13 = automaton.add(r7);
			Automaton.Term r14 = new Automaton.Term(K_Or, r13);
			int r15 = automaton.add(r14);
			if(r0 != r15) {
				return automaton.rewrite(r0, r15);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $7<Or($5<^{$2<^Type<$7|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|And($5)|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>...}>)>
	public final static int K_Or = 2;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// 
	private final static class Reduction_7 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_7(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Or) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() == 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						int[] state = {r0, r1, r2, r3};
						activations.add(new Activation(this,null,state));
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // t
			int r3 = state[3];
			if(r0 != r2) {
				return automaton.rewrite(r0, r2);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_8 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_8(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Or) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Or) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.Collection c4 = (Automaton.Collection) s4;
							int[] state = {r0, r1, r2, r3, r4, 0, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			Automaton.Collection s4 = (Automaton.Collection) automaton.get(state[4]);
			int[] s4children = new int[s4.size() - 0];
			for(int s4i=0, s4j=0; s4i != s4.size();++s4i) {
				s4children[s4j++] = s4.get(s4i);
			}
			Automaton.Set r5 = new Automaton.Set(s4children);
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r6 = new Automaton.Set(s1children);
			Automaton.Set r7 = r5.append(r6); // xs append ys
			int r8 = automaton.add(r7);
			Automaton.Term r9 = new Automaton.Term(K_Or, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
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

	// 
	private final static class Reduction_9 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_9(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type1,automaton,automaton.get(r2), SCHEMA)) {
							int[] state = {r0, r1, r2, r3, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r4 = new Automaton.Set(s1children);
			Automaton.Term r5 = Void;
			int r6 = automaton.add(r5);
			if(r0 != r6) {
				return automaton.rewrite(r0, r6);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_10 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_10(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type0,automaton,automaton.get(r2), SCHEMA)) {
							int[] state = {r0, r1, r2, r3, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r4 = new Automaton.Set(s1children);
			int r5 = automaton.add(r4);
			Automaton.Term r6 = new Automaton.Term(K_And, r5);
			int r7 = automaton.add(r6);
			if(r0 != r7) {
				return automaton.rewrite(r0, r7);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_11 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_11(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type3,automaton,automaton.get(r2), SCHEMA)) {
							for(int r5=0;r5!=c1.size();++r5) {
								if(r5 == r3) { continue; }
								int r4 = c1.get(r5);
								if(Runtime.accepts(type3,automaton,automaton.get(r4), SCHEMA)) {
									int[] state = {r0, r1, r2, r3, r4, r5, 0};
									activations.add(new Activation(this,null,state));
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // a1
			int r3 = state[3];
			int r4 = state[4]; // a2
			int r5 = state[5];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r5) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r6 = new Automaton.Set(s1children);
			boolean r7 = r2 != r4;         // a1 neq a2
			boolean r8 = false;            // a1 neq a2 && !a2 is ^Any
			if(r7) {
				boolean r9 = Runtime.accepts(type4, automaton, r4, SCHEMA); // a2 is ^Any
				boolean r10 = !r9;             // !a2 is ^Any
				r8 = r10;
			}
			if(r8) {
				Automaton.Term r11 = Void;
				int r12 = automaton.add(r11);
				if(r0 != r12) {
					return automaton.rewrite(r0, r12);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_12 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_12(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type3,automaton,automaton.get(r2), SCHEMA)) {
							for(int r5=0;r5!=c1.size();++r5) {
								if(r5 == r3) { continue; }
								int r4 = c1.get(r5);
								Automaton.State s4 = automaton.get(r4);
								if(s4.kind == K_Not) {
									Automaton.Term t4 = (Automaton.Term) s4;
									int r6 = t4.contents;
									if(Runtime.accepts(type3,automaton,automaton.get(r6), SCHEMA)) {
										int[] state = {r0, r1, r2, r3, r4, r5, r6, 0};
										activations.add(new Activation(this,null,state));
									}
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // a1
			int r3 = state[3];
			int r5 = state[5];
			int r6 = state[6]; // a2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r5) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r7 = new Automaton.Set(s1children);
			boolean r8 = r2 == r6;         // a1 eq a2
			if(r8) {
				Automaton.Term r9 = Void;
				int r10 = automaton.add(r9);
				if(r0 != r10) {
					return automaton.rewrite(r0, r10);
				}
			}
			Automaton.Term r11 = Any;
			Object r12 = (Object) automaton.get(r6);
			boolean r13 = !r12.equals(r11); // a2 neq Any
			if(r13) {
				Automaton.Set r14 = r7.appendFront(r2); // a1 append ts
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_And, r15);
				int r17 = automaton.add(r16);
				if(r0 != r17) {
					return automaton.rewrite(r0, r17);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_13 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_13(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Or) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type0,automaton,automaton.get(r2), SCHEMA)) {
							int[] state = {r0, r1, r2, r3, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r4 = new Automaton.Set(s1children);
			Automaton.Term r5 = Any;
			int r6 = automaton.add(r5);
			if(r0 != r6) {
				return automaton.rewrite(r0, r6);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_14 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_14(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Or) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type1,automaton,automaton.get(r2), SCHEMA)) {
							int[] state = {r0, r1, r2, r3, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r4 = new Automaton.Set(s1children);
			int r5 = automaton.add(r4);
			Automaton.Term r6 = new Automaton.Term(K_Or, r5);
			int r7 = automaton.add(r6);
			if(r0 != r7) {
				return automaton.rewrite(r0, r7);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $4<Ref($2<^Type<$4|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>)>
	public final static int K_Ref = 9;
	public final static int Ref(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Ref, r0));
	}

	// 
	private final static class Reduction_15 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_15(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Ref) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				if(Runtime.accepts(type1,automaton,automaton.get(r1), SCHEMA)) {
					int[] state = {r0, r1};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Term r2 = Void;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_16 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_16(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Ref) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							for(int r6=0;r6!=c1.size();++r6) {
								if(r6 == r3) { continue; }
								int r5 = c1.get(r6);
								Automaton.State s5 = automaton.get(r5);
								if(s5.kind == K_Ref) {
									Automaton.Term t5 = (Automaton.Term) s5;
									int r7 = t5.contents;
									int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, 0};
									activations.add(new Activation(this,null,state));
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			int r4 = state[4]; // t1
			int r6 = state[6];
			int r7 = state[7]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r8 = new Automaton.Set(s1children);
			Automaton.Set r9 = new Automaton.Set(r4, r7); // {t1t2}
			int r10 = automaton.add(r9);
			Automaton.Term r11 = new Automaton.Term(K_And, r10);
			int r12 = automaton.add(r11);
			Automaton.Term r13 = new Automaton.Term(K_Ref, r12);
			int r14 = automaton.add(r13);
			Automaton.Set r15 = r8.appendFront(r14); // Ref(And({t1t2})) append ts
			int r16 = automaton.add(r15);
			Automaton.Term r17 = new Automaton.Term(K_And, r16);
			int r18 = automaton.add(r17);
			if(r0 != r18) {
				return automaton.rewrite(r0, r18);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 5; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_17 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_17(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Or) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Ref) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							if(Runtime.accepts(type0,automaton,automaton.get(r4), SCHEMA)) {
								for(int r6=0;r6!=c1.size();++r6) {
									if(r6 == r3) { continue; }
									int r5 = c1.get(r6);
									Automaton.State s5 = automaton.get(r5);
									if(s5.kind == K_Ref) {
										Automaton.Term t5 = (Automaton.Term) s5;
										int r7 = t5.contents;
										int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, 0};
										activations.add(new Activation(this,null,state));
									}
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // t
			int r3 = state[3];
			int r6 = state[6];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r8 = new Automaton.Set(s1children);
			Automaton.Set r9 = r8.appendFront(r2); // t append ts
			int r10 = automaton.add(r9);
			Automaton.Term r11 = new Automaton.Term(K_Or, r10);
			int r12 = automaton.add(r11);
			if(r0 != r12) {
				return automaton.rewrite(r0, r12);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 5; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_18 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_18(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Ref) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							for(int r6=0;r6!=c1.size();++r6) {
								if(r6 == r3) { continue; }
								int r5 = c1.get(r6);
								Automaton.State s5 = automaton.get(r5);
								if(s5.kind == K_Not) {
									Automaton.Term t5 = (Automaton.Term) s5;
									int r7 = t5.contents;
									Automaton.State s7 = automaton.get(r7);
									if(s7.kind == K_Ref) {
										Automaton.Term t7 = (Automaton.Term) s7;
										int r8 = t7.contents;
										int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, r8, 0};
										activations.add(new Activation(this,null,state));
									}
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			int r4 = state[4]; // t1
			int r6 = state[6];
			int r8 = state[8]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r9 = new Automaton.Set(s1children);
			Automaton.Term r10 = new Automaton.Term(K_Not, r8);
			int r11 = automaton.add(r10);
			Automaton.Set r12 = new Automaton.Set(r4, r11); // {t1Not(t2)}
			int r13 = automaton.add(r12);
			Automaton.Term r14 = new Automaton.Term(K_And, r13);
			int r15 = automaton.add(r14);
			Automaton.Term r16 = new Automaton.Term(K_Ref, r15);
			int r17 = automaton.add(r16);
			Automaton.Set r18 = r9.appendFront(r17); // Ref(And({t1Not(t2)})) append ts
			int r19 = automaton.add(r18);
			Automaton.Term r20 = new Automaton.Term(K_And, r19);
			int r21 = automaton.add(r20);
			if(r0 != r21) {
				return automaton.rewrite(r0, r21);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 6; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $4<Meta($2<^Type<$4|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>)>
	public final static int K_Meta = 10;
	public final static int Meta(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Meta, r0));
	}

	// 
	private final static class Reduction_19 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_19(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Meta) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				if(Runtime.accepts(type1,automaton,automaton.get(r1), SCHEMA)) {
					int[] state = {r0, r1};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Term r2 = Void;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_20 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_20(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Meta) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							for(int r6=0;r6!=c1.size();++r6) {
								if(r6 == r3) { continue; }
								int r5 = c1.get(r6);
								Automaton.State s5 = automaton.get(r5);
								if(s5.kind == K_Meta) {
									Automaton.Term t5 = (Automaton.Term) s5;
									int r7 = t5.contents;
									int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, 0};
									activations.add(new Activation(this,null,state));
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			int r4 = state[4]; // t1
			int r6 = state[6];
			int r7 = state[7]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r8 = new Automaton.Set(s1children);
			Automaton.Set r9 = new Automaton.Set(r4, r7); // {t1t2}
			int r10 = automaton.add(r9);
			Automaton.Term r11 = new Automaton.Term(K_And, r10);
			int r12 = automaton.add(r11);
			Automaton.Term r13 = new Automaton.Term(K_Meta, r12);
			int r14 = automaton.add(r13);
			Automaton.Set r15 = r8.appendFront(r14); // Meta(And({t1t2})) append ts
			int r16 = automaton.add(r15);
			Automaton.Term r17 = new Automaton.Term(K_And, r16);
			int r18 = automaton.add(r17);
			if(r0 != r18) {
				return automaton.rewrite(r0, r18);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 5; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_21 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_21(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Or) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Meta) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							if(Runtime.accepts(type0,automaton,automaton.get(r4), SCHEMA)) {
								for(int r6=0;r6!=c1.size();++r6) {
									if(r6 == r3) { continue; }
									int r5 = c1.get(r6);
									Automaton.State s5 = automaton.get(r5);
									if(s5.kind == K_Meta) {
										Automaton.Term t5 = (Automaton.Term) s5;
										int r7 = t5.contents;
										int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, 0};
										activations.add(new Activation(this,null,state));
									}
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // t
			int r3 = state[3];
			int r6 = state[6];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r8 = new Automaton.Set(s1children);
			Automaton.Set r9 = r8.appendFront(r2); // t append ts
			int r10 = automaton.add(r9);
			Automaton.Term r11 = new Automaton.Term(K_Or, r10);
			int r12 = automaton.add(r11);
			if(r0 != r12) {
				return automaton.rewrite(r0, r12);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 5; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_22 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_22(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Meta) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							for(int r6=0;r6!=c1.size();++r6) {
								if(r6 == r3) { continue; }
								int r5 = c1.get(r6);
								Automaton.State s5 = automaton.get(r5);
								if(s5.kind == K_Not) {
									Automaton.Term t5 = (Automaton.Term) s5;
									int r7 = t5.contents;
									Automaton.State s7 = automaton.get(r7);
									if(s7.kind == K_Meta) {
										Automaton.Term t7 = (Automaton.Term) s7;
										int r8 = t7.contents;
										int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, r8, 0};
										activations.add(new Activation(this,null,state));
									}
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			int r4 = state[4]; // t1
			int r6 = state[6];
			int r8 = state[8]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r9 = new Automaton.Set(s1children);
			Automaton.Term r10 = new Automaton.Term(K_Not, r8);
			int r11 = automaton.add(r10);
			Automaton.Set r12 = new Automaton.Set(r4, r11); // {t1Not(t2)}
			int r13 = automaton.add(r12);
			Automaton.Term r14 = new Automaton.Term(K_And, r13);
			int r15 = automaton.add(r14);
			Automaton.Term r16 = new Automaton.Term(K_Meta, r15);
			int r17 = automaton.add(r16);
			Automaton.Set r18 = r9.appendFront(r17); // Meta(And({t1Not(t2)})) append ts
			int r19 = automaton.add(r18);
			Automaton.Term r20 = new Automaton.Term(K_And, r19);
			int r21 = automaton.add(r20);
			if(r0 != r21) {
				return automaton.rewrite(r0, r21);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 6; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $9<Term(^[^string,$3<^Type<$9|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$3...})|And(^{$3...})|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Set(^[$3,^{|$3...|}[$3...]])|Bag(^[$3,^{|$3...|}[$3...]])|List(^[$3,^[$3...]])>>...])>
	public final static int K_Term = 11;
	public final static int Term(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}
	public final static int Term(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}

	// 
	private final static class Reduction_23 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_23(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_Term) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							if(l4.size() == 2) {
								int r5 = l4.get(0);
								int r6 = l4.get(1);
								for(int r8=0;r8!=c1.size();++r8) {
									if(r8 == r3) { continue; }
									int r7 = c1.get(r8);
									Automaton.State s7 = automaton.get(r7);
									if(s7.kind == K_Term) {
										Automaton.Term t7 = (Automaton.Term) s7;
										int r9 = t7.contents;
										Automaton.State s9 = automaton.get(r9);
										Automaton.List l9 = (Automaton.List) s9;
										if(l9.size() == 2) {
											int r10 = l9.get(0);
											int r11 = l9.get(1);
											int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, 0};
											activations.add(new Activation(this,null,state));
										}
									}
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			int r5 = state[5]; // s1
			int r6 = state[6]; // t1
			int r8 = state[8];
			int r10 = state[10]; // s2
			int r11 = state[11]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r8) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r12 = new Automaton.Set(s1children);
			boolean r13 = r5 == r10;       // s1 eq s2
			if(r13) {
				Automaton.Set r14 = new Automaton.Set(r6, r11); // {t1t2}
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_And, r15);
				int r17 = automaton.add(r16);
				Automaton.List r18 = new Automaton.List(r5, r17); // [s1And({t1t2})]
				int r19 = automaton.add(r18);
				Automaton.Term r20 = new Automaton.Term(K_Term, r19);
				int r21 = automaton.add(r20);
				Automaton.Set r22 = r12.appendFront(r21); // Term([s1And({t1t2})]) append ts
				int r23 = automaton.add(r22);
				Automaton.Term r24 = new Automaton.Term(K_And, r23);
				int r25 = automaton.add(r24);
				if(r0 != r25) {
					return automaton.rewrite(r0, r25);
				}
			}
			Automaton.Term r26 = Void;
			int r27 = automaton.add(r26);
			if(r0 != r27) {
				return automaton.rewrite(r0, r27);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 9; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $9<Nominal(^[^string,$3<^Type<$9|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$3...})|And(^{$3...})|Not($3)|Ref($3)|Meta($3)|Term(^[^string,$3...])|Set(^[$3,^{|$3...|}[$3...]])|Bag(^[$3,^{|$3...|}[$3...]])|List(^[$3,^[$3...]])>>])>
	public final static int K_Nominal = 12;
	public final static int Nominal(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}
	public final static int Nominal(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}

	// 
	private final static class Reduction_24 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_24(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Nominal) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				int r3 = l1.get(1);
				int[] state = {r0, r1, r2, r3};
				activations.add(new Activation(this,null,state));
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3]; // t
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term Fun(^[$2<^Type<Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>,$2])
	public final static int K_Fun = 13;
	public final static int Fun(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}
	public final static int Fun(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}

	// term $11<Set($9<^[$2<^Type<$11|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Bag($9)|List(^[$2,^[$2...]])>>,^{|$2...|}[$2...]]>)>
	public final static int K_Set = 14;
	public final static int Set(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}
	public final static int Set(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}

	// 
	private final static class Reduction_25 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_25(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Set) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				int r3 = l1.get(1);
				Automaton.State s3 = automaton.get(r3);
				Automaton.Collection c3 = (Automaton.Collection) s3;
				if(c3.size() >= 1) {
					for(int r5=0;r5!=c3.size();++r5) {
						int r4 = c3.get(r5);
						if(Runtime.accepts(type1,automaton,automaton.get(r4), SCHEMA)) {
							int[] state = {r0, r1, r2, r3, r4, r5, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // t
			int r5 = state[5];
			Automaton.Collection s3 = (Automaton.Collection) automaton.get(state[3]);
			int[] s3children = new int[s3.size() - 1];
			for(int s3i=0, s3j=0; s3i != s3.size();++s3i) {
				if(s3i == r5) { continue; }
				s3children[s3j++] = s3.get(s3i);
			}
			Automaton.Bag r6 = new Automaton.Bag(s3children);
			int r7 = automaton.add(r6);
			Automaton.List r8 = new Automaton.List(r2, r7); // [tts]
			int r9 = automaton.add(r8);
			Automaton.Term r10 = new Automaton.Term(K_Set, r9);
			int r11 = automaton.add(r10);
			if(r0 != r11) {
				return automaton.rewrite(r0, r11);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 4; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $11<Bag($9<^[$2<^Type<$11|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set($9)|List(^[$2,^[$2...]])>>,^{|$2...|}[$2...]]>)>
	public final static int K_Bag = 15;
	public final static int Bag(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}
	public final static int Bag(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}

	// 
	private final static class Reduction_26 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_26(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Bag) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				int r3 = l1.get(1);
				Automaton.State s3 = automaton.get(r3);
				Automaton.Collection c3 = (Automaton.Collection) s3;
				if(c3.size() >= 1) {
					for(int r5=0;r5!=c3.size();++r5) {
						int r4 = c3.get(r5);
						if(Runtime.accepts(type1,automaton,automaton.get(r4), SCHEMA)) {
							int[] state = {r0, r1, r2, r3, r4, r5, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // t
			int r5 = state[5];
			Automaton.Collection s3 = (Automaton.Collection) automaton.get(state[3]);
			int[] s3children = new int[s3.size() - 1];
			for(int s3i=0, s3j=0; s3i != s3.size();++s3i) {
				if(s3i == r5) { continue; }
				s3children[s3j++] = s3.get(s3i);
			}
			Automaton.Bag r6 = new Automaton.Bag(s3children);
			int r7 = automaton.add(r6);
			Automaton.List r8 = new Automaton.List(r2, r7); // [tts]
			int r9 = automaton.add(r8);
			Automaton.Term r10 = new Automaton.Term(K_Bag, r9);
			int r11 = automaton.add(r10);
			if(r0 != r11) {
				return automaton.rewrite(r0, r11);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 4; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $11<List(^[$2<^Type<$11|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])>>,^[$2...]])>
	public final static int K_List = 16;
	public final static int List(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}
	public final static int List(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}

	// 
	private final static class Reduction_27 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_27(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_List) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				int r3 = l1.get(1);
				Automaton.State s3 = automaton.get(r3);
				Automaton.List l3 = (Automaton.List) s3;
				int[] state = {r0, r1, r2, r3, 0};
				activations.add(new Activation(this,null,state));
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // t
			Automaton.List r4 = ((Automaton.List) automaton.get(state[3])).sublist(0);
			Automaton.Term r5 = Void;
			int r6 = automaton.add(r5);
			boolean r7 = r4.contains(r6);  // Void in t1s
			if(r7) {
				Automaton.Term r8 = Void;
				int r9 = automaton.add(r8);
				if(r0 != r9) {
					return automaton.rewrite(r0, r9);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_28 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_28(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type6,automaton,automaton.get(r2), SCHEMA)) {
							for(int r5=0;r5!=c1.size();++r5) {
								if(r5 == r3) { continue; }
								int r4 = c1.get(r5);
								int[] state = {r0, r1, r2, r3, r4, r5, 0};
								activations.add(new Activation(this,null,state));
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // l
			int r3 = state[3];
			int r4 = state[4]; // t
			int r5 = state[5];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r5) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r6 = new Automaton.Set(s1children);
			boolean r7 = Runtime.accepts(type7, automaton, r4, SCHEMA); // t is ^Proton<Any|Void|Bool|Int|Real|String>
			boolean r8 = Runtime.accepts(type8, automaton, r4, SCHEMA); // t is ^SetOrBag<$18<Set($16<^[$9<^Type<$18|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$9...})|And(^{$9...})|Not($9)|Ref($9)|Meta($9)|Nominal(^[^string,$9])|Term(^[^string,$9...])|Bag($16)|List(^[$9,^[$9...]])>>,^{|$9...|}[$9...]]>)>|Bag($16)>
			boolean r9 = r7 || r8;         // t is ^Proton<Any|Void|Bool|Int|Real|String> || t is ^SetOrBag<$18<Set($16<^[$9<^Type<$18|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$9...})|And(^{$9...})|Not($9)|Ref($9)|Meta($9)|Nominal(^[^string,$9])|Term(^[^string,$9...])|Bag($16)|List(^[$9,^[$9...]])>>,^{|$9...|}[$9...]]>)>|Bag($16)>
			if(r9) {
				Automaton.Term r10 = Void;
				int r11 = automaton.add(r10);
				if(r0 != r11) {
					return automaton.rewrite(r0, r11);
				}
			}
			boolean r12 = Runtime.accepts(type9, automaton, r4, SCHEMA); // t is ^Not(^Proton<Any|Void|Bool|Int|Real|String>)
			boolean r13 = Runtime.accepts(type10, automaton, r4, SCHEMA); // t is ^Not(^SetOrBag<$22<Set($20<^[$13<^Type<$22|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$13...})|And(^{$13...})|Not($13)|Ref($13)|Meta($13)|Nominal(^[^string,$13])|Term(^[^string,$13...])|Bag($20)|List(^[$13,^[$13...]])>>,^{|$13...|}[$13...]]>)>|Bag($20)>)
			boolean r14 = r12 || r13;      // t is ^Not(^Proton<Any|Void|Bool|Int|Real|String>) || t is ^Not(^SetOrBag<$22<Set($20<^[$13<^Type<$22|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$13...})|And(^{$13...})|Not($13)|Ref($13)|Meta($13)|Nominal(^[^string,$13])|Term(^[^string,$13...])|Bag($20)|List(^[$13,^[$13...]])>>,^{|$13...|}[$13...]]>)>|Bag($20)>)
			if(r14) {
				Automaton.Set r15 = r6.appendFront(r2); // l append ts
				int r16 = automaton.add(r15);
				Automaton.Term r17 = new Automaton.Term(K_And, r16);
				int r18 = automaton.add(r17);
				if(r0 != r18) {
					return automaton.rewrite(r0, r18);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// 
	private final static class Reduction_29 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_29(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_And) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_List) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							int r5 = l4.get(0);
							if(Runtime.accepts(type11,automaton,automaton.get(r5), SCHEMA)) {
								int r6 = l4.get(1);
								Automaton.State s6 = automaton.get(r6);
								Automaton.List l6 = (Automaton.List) s6;
								for(int r9=0;r9!=c1.size();++r9) {
									if(r9 == r3) { continue; }
									int r8 = c1.get(r9);
									Automaton.State s8 = automaton.get(r8);
									if(s8.kind == K_List) {
										Automaton.Term t8 = (Automaton.Term) s8;
										int r10 = t8.contents;
										Automaton.State s10 = automaton.get(r10);
										Automaton.List l10 = (Automaton.List) s10;
										int r11 = l10.get(0);
										if(Runtime.accepts(type11,automaton,automaton.get(r11), SCHEMA)) {
											int r12 = l10.get(1);
											Automaton.State s12 = automaton.get(r12);
											Automaton.List l12 = (Automaton.List) s12;
											int[] state = {r0, r1, r2, r3, r4, r5, r6, 0, r8, r9, r10, r11, r12, 0, 0};
											activations.add(new Activation(this,null,state));
										}
									}
								}
							}
						}
					}
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			int r5 = state[5]; // ub1
			Automaton.List r7 = ((Automaton.List) automaton.get(state[6])).sublist(0);
			int r9 = state[9];
			int r11 = state[11]; // ub2
			Automaton.List r13 = ((Automaton.List) automaton.get(state[12])).sublist(0);
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r9) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r14 = new Automaton.Set(s1children);
			boolean r15 = ((Automaton.Bool)automaton.get(r5)).value;
			boolean r16 = ((Automaton.Bool)automaton.get(r11)).value;
			boolean r17 = r15 || r16;      // ub1 || ub2
			boolean r18 = !r17;            // !ub1 || ub2
			boolean r19 = false;           // !ub1 || ub2 && |t1s| neq |t2s|
			if(r18) {
				Automaton.Int r20 = r7.lengthOf(); // |t1s|
				Automaton.Int r21 = r13.lengthOf(); // |t2s|
				boolean r22 = !r20.equals(r21); // |t1s| neq |t2s|
				r19 = r22;
			}
			if(r19) {
				Automaton.Term r23 = Void;
				int r24 = automaton.add(r23);
				if(r0 != r24) {
					return automaton.rewrite(r0, r24);
				}
			}
			boolean r25 = ((Automaton.Bool)automaton.get(r5)).value;
			boolean r26 = false;           // ub1 && !ub2 && |t1s| gt |t2s| add 1
			if(r25) {
				boolean r27 = ((Automaton.Bool)automaton.get(r11)).value;
				boolean r28 = !r27;            // !ub2
				boolean r29 = false;           // !ub2 && |t1s| gt |t2s| add 1
				if(r28) {
					Automaton.Int r30 = r7.lengthOf(); // |t1s|
					Automaton.Int r31 = r13.lengthOf(); // |t2s|
					Automaton.Int r32 = new Automaton.Int(1); // 1
					Automaton.Int r33 = r31.add(r32); // |t2s| add 1
					boolean r34 = r30.compareTo(r33)>0; // |t1s| gt |t2s| add 1
					r29 = r34;
				}
				r26 = r29;
			}
			if(r26) {
				Automaton.Term r35 = Void;
				int r36 = automaton.add(r35);
				if(r0 != r36) {
					return automaton.rewrite(r0, r36);
				}
			}
			boolean r37 = ((Automaton.Bool)automaton.get(r5)).value;
			boolean r38 = !r37;            // !ub1
			boolean r39 = false;           // !ub1 && ub2 && |t2s| gt |t1s| add 1
			if(r38) {
				boolean r40 = ((Automaton.Bool)automaton.get(r11)).value;
				boolean r41 = false;           // ub2 && |t2s| gt |t1s| add 1
				if(r40) {
					Automaton.Int r42 = r13.lengthOf(); // |t2s|
					Automaton.Int r43 = r7.lengthOf(); // |t1s|
					Automaton.Int r44 = new Automaton.Int(1); // 1
					Automaton.Int r45 = r43.add(r44); // |t1s| add 1
					boolean r46 = r42.compareTo(r45)>0; // |t2s| gt |t1s| add 1
					r41 = r46;
				}
				r39 = r41;
			}
			if(r39) {
				Automaton.Term r47 = Void;
				int r48 = automaton.add(r47);
				if(r0 != r48) {
					return automaton.rewrite(r0, r48);
				}
			}
			boolean r49 = ((Automaton.Bool)automaton.get(r5)).value;
			boolean r50 = ((Automaton.Bool)automaton.get(r11)).value;
			boolean r51 = r49 || r50;      // ub1 || ub2
			boolean r52 = !r51;            // !ub1 || ub2
			if(r52) {
				Automaton.Int r54 = new Automaton.Int(0); // 0
				Automaton.Int r55 = r7.lengthOf(); // |t1s|
				Automaton.List r56 = Runtime.rangeOf(automaton,r54,r55); // 0 range |t1s|
				Automaton.List t53 = new Automaton.List();
				for(int i57=0;i57<r56.size();i57++) {
					Automaton.Int r57 = (Automaton.Int) automaton.get(r56.get(i57));;
					int r58 = r7.indexOf(r57);     // t1s[i]
					int r59 = r13.indexOf(r57);    // t2s[i]
					Automaton.Set r60 = new Automaton.Set(r58, r59); // {t1s[i]t2s[i]}
					int r61 = automaton.add(r60);
					Automaton.Term r62 = new Automaton.Term(K_And, r61);
					int r63 = automaton.add(r62);
					t53.add(r63);
				}
				Automaton.List r53 = t53;
				boolean r64 = false;           // false
				int r65 = automaton.add(r64 ? Automaton.TRUE : Automaton.FALSE);
				int r66 = automaton.add(r53);
				Automaton.List r67 = new Automaton.List(r65, r66); // [falset3s]
				int r68 = automaton.add(r67);
				Automaton.Term r69 = new Automaton.Term(K_List, r68);
				int r70 = automaton.add(r69);
				Automaton.Set r71 = r14.appendFront(r70); // List([falset3s]) append ts
				int r72 = automaton.add(r71);
				Automaton.Term r73 = new Automaton.Term(K_And, r72);
				int r74 = automaton.add(r73);
				if(r0 != r74) {
					return automaton.rewrite(r0, r74);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return ""; }
		public final int rank() { return 0; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// $4<Not($2<^Type<$4|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>)>
		Schema.Term("Not",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $7<And($5<^{$2<^Type<$7|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or($5)|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>...}>)>
		Schema.Term("And",Schema.Set(true)),
		// $7<Or($5<^{$2<^Type<$7|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|And($5)|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>...}>)>
		Schema.Term("Or",Schema.Set(true)),
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
		// $4<Ref($2<^Type<$4|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>)>
		Schema.Term("Ref",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $4<Meta($2<^Type<$4|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>)>
		Schema.Term("Meta",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $9<Term(^[^string,$3<^Type<$9|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$3...})|And(^{$3...})|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Set(^[$3,^{|$3...|}[$3...]])|Bag(^[$3,^{|$3...|}[$3...]])|List(^[$3,^[$3...]])>>...])>
		Schema.Term("Term",Schema.List(true,Schema.String)),
		// $9<Nominal(^[^string,$3<^Type<$9|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$3...})|And(^{$3...})|Not($3)|Ref($3)|Meta($3)|Term(^[^string,$3...])|Set(^[$3,^{|$3...|}[$3...]])|Bag(^[$3,^{|$3...|}[$3...]])|List(^[$3,^[$3...]])>>])>
		Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))))),
		// Fun(^[$2<^Type<Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])|List(^[$2,^[$2...]])>>,$2])
		Schema.Term("Fun",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Any)),
		// $11<Set($9<^[$2<^Type<$11|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Bag($9)|List(^[$2,^[$2...]])>>,^{|$2...|}[$2...]]>)>
		Schema.Term("Set",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Bag(true))),
		// $11<Bag($9<^[$2<^Type<$11|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set($9)|List(^[$2,^[$2...]])>>,^{|$2...|}[$2...]]>)>
		Schema.Term("Bag",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Bag(true))),
		// $11<List(^[$2<^Type<$11|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])>>,^[$2...]])>
		Schema.Term("List",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any)),Schema.List(true)))
	});

	// =========================================================================
	// Types
	// =========================================================================

	// Any
	private static Type type0 = Runtime.Type("2C0tLTIc2Av3w$");
	// Void
	private static Type type1 = Runtime.Type("2GLxLPZCWDggIk2");
	// $15<Type<Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{^$15...})|And(^{^$15...})|Not(^$15)|Ref(^$15)|Meta(^$15)|Nominal(^[^string,^$15])|Term(^[^string,^$15...])|Set(^[^$15,^{|^$15...|}[^$15...]])|Bag(^[^$15,^{|^$15...|}[^$15...]])|List(^[^$15,^[^$15...]])>>
	private static Type type2 = Runtime.Type("w53GKTkK5G0GrQhGmImGIFiG5CDx5So7KOaGJI_GMNotoQh_aQWl5GJKaRhGnJ_G6C14qO3lJPnGMgGPS5U5d5f5h5w596P6R6h6Qso7uwJANG5Jmx5Sjta9E5YsnLgkK7SxNgWMYgnMhV5C0tLTIc5AfGZKj_5OIo5AiGZFjx5QIV6AtGIHiGr3vl1U38KOWlq3ylmUZC4Sm_aQbCGX0A95WlqQup6XlXl5B5YcKYGAD5eZp7N5fwHaW9P5Ykna0AR5Yona0AT5Ys2a0Ac5Yw2a0Ae5YV3a0Ag5eZl7i5N5YolfGDs5eZQBHHu5ggQBWfl7N5x5tsQcmjl79WRkHm0DO5egRB1a0nl7SoRZYnW9E6Yg3q0AO6Yk3q0AQ6YVOkHDS6esSB1alrl7SVTZIuW9e6Yoou0Ag6GL");
	// Proton<Any|Void|Bool|Int|Real|String>
	private static Type type3 = Runtime.Type("jF5Jmx5Sjt5C0tLTIg2AwFZKj_5OIs2AzFZFjx5QIZ3A9GIHiGr3BlHI38KOWlq3El1LZC4Sm_aQbCWLggKNxVoHD_4MQop7usKAU4cx");
	// ^Any
	private static Type type4 = Runtime.Type("3C0tLTIc2Avc1EGE");
	// string
	private static Type type5 = Runtime.Type("FZ0");
	// $11<List(^[$2<^Type<$11|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$2...})|And(^{$2...})|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Term(^[^string,$2...])|Set(^[$2,^{|$2...|}[$2...]])|Bag(^[$2,^{|$2...|}[$2...]])>>,^[$2...]])>
	private static Type type6 = Runtime.Type("w53lJPnG6GJ_6R_dmPYk2AtoY9yB1EzBH67_IHeco7ug3ABGJFoxLQZwZRo3ZQZGYIjG6CHKaO3pJOo45SDxLQdtLNgG3K_8MQoBKOoGYFWSLgGIvtPVQcQkQsQoRgSgToTg0PYgIPhc5OF8rQoxaQeZPBHLhlXQWwLel5sCXItpXTo3ZQtC0Ugo6GLxLPZClUgVNFZFjx5QIcNkmX0C8t5SIoNkXY0GHKLNgC0a0AO5ZC4Sm_aQbCla0AR5WtMX0YlYWaGbl5T5YkqbGAc5Ykn9jcPcmel7ElPkHfl7NlPkmfl7Ol2At5Yc4EggQBmLwkHjW9OBmj0EYo0mGD86ecRB1MA6gkRBljl7wsRZnnW9N6YoKq0AP6scPc1rl7woSBH6T6twSc1ul7T_TkXul7U_Tk1vGo3");
	// ^Proton<Any|Void|Bool|Int|Real|String>
	private static Type type7 = Runtime.Type("sF5Jmx5Sjta9dGIFi_r3wkHE3OpQdGq3zk1H38oQjlq39lmHoZZQoCGIgs3GHKLNgC0LgZ4OIGbRdtqOIg4AR4aE8loIPpp5TCXDUp1PlDE");
	// ^SetOrBag<$18<Set($16<^[$9<^Type<$18|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$9...})|And(^{$9...})|Not($9)|Ref($9)|Meta($9)|Nominal(^[^string,$9])|Term(^[^string,$9...])|Bag($16)|List(^[$9,^[$9...]])>>,^{|$9...|}[$9...]]>)>|Bag($16)>
	private static Type type8 = Runtime.Type("9635IK5SE8bFWSa98GnJ_G6C14qOWk_ul5yBXDzo1H3GKTkKa9tCmHfV2Ieoo7Ato7SwJDNdHLYkYLgg4G0GrQhGmImGIFiG5CDx5So7KOaGJI_GMNotoQh_aQWl5GJKaRhG3IdC6Sl5R_NgQoQwQZRgRcSVTcTgUgWQYcoQhV6OF8rQoxaQewPB1Pvl1UWorfl5yCHMzp1X0C0tLTIcNkmX0GLxLPZCGY0AD538oQjlq3N5gZOFIHiGr3Q5gkOFZJ_45QIsOkmb0OIGbRdtqOIZPkXeGNB5E5P5S5c5f5QkPBXTh5hsPw1IeVQBXMt5gcQBmMt5gkQB1PAlXjl7dh3A76YcqHgcRcH5YkRgo7SoRZYnW9E6Yg5q0AO6IkRBmHQ6tkScHrl7gtSkmrl7xc4Ad6Yg3AtgTc1vl7ApTBH6i6twTc1yl7h_UkXyGg2");
	// ^Not(^Proton<Any|Void|Bool|Int|Real|String>)
	private static Type type9 = Runtime.Type("wFYIjG6OF8rQoxaQeoq7uk2AxcXEo3ZQtC0HgZ3GLxLPZClHgk3G1xqQgCWIgw3C8t5SIZ4APGZJ_45QIk4ASGqJo8MPiSq3Ul1PWdJINh_MdhWPYgnPhkLwn2");
	// ^Not(^SetOrBag<$22<Set($20<^[$13<^Type<$22|Atom<Not(^Proton<Any|Void|Bool|Int|Real|String>)|Proton<Any|Void|Bool|Int|Real|String>>|Or(^{$13...})|And(^{$13...})|Not($13)|Ref($13)|Meta($13)|Nominal(^[^string,$13])|Term(^[^string,$13...])|Bag($20)|List(^[$13,^[$13...]])>>,^{|$13...|}[$13...]]>)>|Bag($20)>)
	private static Type type10 = Runtime.Type("C6osoQoGNBKOoxZR14qOeoo7uk2AxcXEoBKOoGYFWSL7cpTgWHYgnHhk3GJ_6R_d1UYwo9sV_9OCmIPCH6Q_2Meop77t4AUGJFoxLQZwZRo3ZQZGYJ_O5GCK5SWGbIjpLPi45Q3GKOmp5GB_qRo4QV5YWj0mWm0nWnGrluGvWzl5tCXIupmTZ0_RjGrQidXil7us6Az41XWil585YZaXGAA5o3ZQtCGY0AD53OpQdGq3N5gZOFZFjx5QIgOk1b0C8t5SIsOkmb0GHKLNgCGe0Ae5ZC4Sm_aQbC0f0Ah5WxNcOoOVPgPsPglfl7xWQoHilANdmil7elQkHjl7flQkmjl7uw3A86YkqIggRBHQElHnW9OBmnlIYo0qGDO6ecSBXQQ6gkSBlnl7EtSZnrW9c6YwLu0Ae6YZZMgkTBmIgZYvW9j6Yw3yl7SZUZYyW9v6YV6z0Ax6lEE");
	// bool
	private static Type type11 = Runtime.Type("Fk0");

	// =========================================================================
	// Patterns
	// =========================================================================

	private final static Pattern.Term pattern0 = new Pattern.Term("Not",
		new Pattern.Leaf(type0),
		null);
	private final static Pattern.Term pattern1 = new Pattern.Term("Not",
		new Pattern.Leaf(type1),
		null);
	private final static Pattern.Term pattern2 = new Pattern.Term("Not",
		new Pattern.Term("Or",
			new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type2), "es")}),
			null),
		null);
	private final static Pattern.Term pattern3 = new Pattern.Term("Not",
		new Pattern.Term("And",
			new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type2), "es")}),
			null),
		null);
	private final static Pattern.Term pattern4 = new Pattern.Term("And",
		new Pattern.Set(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "t")}),
		null);
	private final static Pattern.Term pattern5 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("And",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern6 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Or",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern7 = new Pattern.Term("Or",
		new Pattern.Set(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "t")}),
		null);
	private final static Pattern.Term pattern8 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Or",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern9 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type1),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern10 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type0),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern11 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type3), "a1"), 
			new Pair(new Pattern.Leaf(type3), "a2"), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern12 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type3), "a1"), 
			new Pair(new Pattern.Term("Not",
				new Pattern.Leaf(type3),
				"a2"),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern13 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type0),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern14 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type1),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern15 = new Pattern.Term("Ref",
		new Pattern.Leaf(type1),
		null);
	private final static Pattern.Term pattern16 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Ref",
				new Pattern.Leaf(type2),
				"t1"),null), 
			new Pair(new Pattern.Term("Ref",
				new Pattern.Leaf(type2),
				"t2"),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern17 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Ref",
				new Pattern.Leaf(type0),
				null), "t"), 
			new Pair(new Pattern.Term("Ref",
				new Pattern.Leaf(type2),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern18 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Ref",
				new Pattern.Leaf(type2),
				"t1"),null), 
			new Pair(new Pattern.Term("Not",
				new Pattern.Term("Ref",
					new Pattern.Leaf(type2),
					"t2"),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern19 = new Pattern.Term("Meta",
		new Pattern.Leaf(type1),
		null);
	private final static Pattern.Term pattern20 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Meta",
				new Pattern.Leaf(type2),
				"t1"),null), 
			new Pair(new Pattern.Term("Meta",
				new Pattern.Leaf(type2),
				"t2"),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern21 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Meta",
				new Pattern.Leaf(type0),
				null), "t"), 
			new Pair(new Pattern.Term("Meta",
				new Pattern.Leaf(type2),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern22 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Meta",
				new Pattern.Leaf(type2),
				"t1"),null), 
			new Pair(new Pattern.Term("Not",
				new Pattern.Term("Meta",
					new Pattern.Leaf(type2),
					"t2"),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern23 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Term",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type5), "s1"), 
					new Pair(new Pattern.Leaf(type2), "t1")}),
				null),null), 
			new Pair(new Pattern.Term("Term",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type5), "s2"), 
					new Pair(new Pattern.Leaf(type2), "t2")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern24 = new Pattern.Term("Nominal",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Leaf(type5),null), 
			new Pair(new Pattern.Leaf(type2), "t")}),
		null);
	private final static Pattern.Term pattern25 = new Pattern.Term("Set",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "t"), 
			new Pair(new Pattern.Bag(true, new Pair[]{
				new Pair(new Pattern.Leaf(type1),null), 
				new Pair(new Pattern.Leaf(type2), "ts")}),null)}),
		null);
	private final static Pattern.Term pattern26 = new Pattern.Term("Bag",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "t"), 
			new Pair(new Pattern.Bag(true, new Pair[]{
				new Pair(new Pattern.Leaf(type1),null), 
				new Pair(new Pattern.Leaf(type2), "ts")}),null)}),
		null);
	private final static Pattern.Term pattern27 = new Pattern.Term("List",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "t"), 
			new Pair(new Pattern.List(true, new Pair[]{
				new Pair(new Pattern.Leaf(type2), "t1s")}),null)}),
		null);
	private final static Pattern.Term pattern28 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type6), "l"), 
			new Pair(new Pattern.Leaf(type2), "t"), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern29 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("List",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type11), "ub1"), 
					new Pair(new Pattern.List(true, new Pair[]{
						new Pair(new Pattern.Leaf(type2), "t1s")}),null)}),
				null),null), 
			new Pair(new Pattern.Term("List",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type11), "ub2"), 
					new Pair(new Pattern.List(true, new Pair[]{
						new Pair(new Pattern.Leaf(type2), "t2s")}),null)}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	// =========================================================================
	// rules
	// =========================================================================

	public static final InferenceRule[] inferences = new InferenceRule[]{

	};
	public static final ReductionRule[] reductions = new ReductionRule[]{
		new Reduction_0(pattern0),
		new Reduction_1(pattern1),
		new Reduction_2(pattern2),
		new Reduction_3(pattern3),
		new Reduction_4(pattern4),
		new Reduction_5(pattern5),
		new Reduction_6(pattern6),
		new Reduction_7(pattern7),
		new Reduction_8(pattern8),
		new Reduction_9(pattern9),
		new Reduction_10(pattern10),
		new Reduction_11(pattern11),
		new Reduction_12(pattern12),
		new Reduction_13(pattern13),
		new Reduction_14(pattern14),
		new Reduction_15(pattern15),
		new Reduction_16(pattern16),
		new Reduction_17(pattern17),
		new Reduction_18(pattern18),
		new Reduction_19(pattern19),
		new Reduction_20(pattern20),
		new Reduction_21(pattern21),
		new Reduction_22(pattern22),
		new Reduction_23(pattern23),
		new Reduction_24(pattern24),
		new Reduction_25(pattern25),
		new Reduction_26(pattern26),
		new Reduction_27(pattern27),
		new Reduction_28(pattern28),
		new Reduction_29(pattern29)
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
			IterativeRewriter.Strategy<InferenceRule> inferenceStrategy = new SimpleRewriteStrategy<InferenceRule>(automaton, inferences);
			IterativeRewriter.Strategy<ReductionRule> reductionStrategy = new SimpleRewriteStrategy<ReductionRule>(automaton, reductions);
			IterativeRewriter rw = new IterativeRewriter(automaton,inferenceStrategy, reductionStrategy, SCHEMA);
			rw.apply();
			System.out.print("REWROTE: ");
			print(automaton);
			System.out.println("\n\n=> (" + rw.getStats() + ")\n");
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
