package wycs.core;

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
	// term $4<NotT($2<^Type<$4|Atom<NotT($16<^Proton<TupleT(^[$16...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$16...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|OrT(^{$2...})|AndT(^{$2...})|SetT(^[^bool,$2])|TupleT(^[$2...])|FunctionT(^[$2,$2,$2...])>>)>
	public final static int K_NotT = 0;
	public final static int NotT(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_NotT, r0));
	}

	// Not_1
	private final static class Reduction_0 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_0(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_NotT) {
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
			Automaton.Term r2 = VoidT;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "Not_1"; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not_2
	private final static class Reduction_1 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_1(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_NotT) {
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
			Automaton.Term r2 = AnyT;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "Not_2"; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not_3
	private final static class Reduction_2 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_2(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_NotT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				if(s1.kind == K_OrT) {
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
				Automaton.Term r6 = new Automaton.Term(K_NotT, r5);
				int r7 = automaton.add(r6);
				t4.add(r7);
			}
			Automaton.Set r4 = new Automaton.Set(t4.toArray());
			int r8 = automaton.add(r4);
			Automaton.Term r9 = new Automaton.Term(K_AndT, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "Not_3"; }
		public final int rank() { return 1; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not_4
	private final static class Reduction_3 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_3(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_NotT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				if(s1.kind == K_AndT) {
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
				Automaton.Term r6 = new Automaton.Term(K_NotT, r5);
				int r7 = automaton.add(r6);
				t4.add(r7);
			}
			Automaton.Set r4 = new Automaton.Set(t4.toArray());
			int r8 = automaton.add(r4);
			Automaton.Term r9 = new Automaton.Term(K_OrT, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "Not_4"; }
		public final int rank() { return 2; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $7<AndT($5<^{$2<^Type<$7|Atom<NotT($19<^Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|OrT($5)|SetT(^[^bool,$2])|TupleT(^[$2...])|FunctionT(^[$2,$2,$2...])>>...}>)>
	public final static int K_AndT = 1;
	public final static int AndT(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_AndT, r1));
	}
	public final static int AndT(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_AndT, r1));
	}

	// AndType_1
	private final static class Reduction_4 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_4(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() == 0) {
					int[] state = {r0, r1};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Term r2 = VoidT;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AndType_1"; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AndType_2
	private final static class Reduction_5 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_5(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
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
		public final String name() { return "AndType_2"; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AndType_3
	private final static class Reduction_6 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_6(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_AndT) {
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
			Automaton.Term r9 = new Automaton.Term(K_AndT, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AndType_3"; }
		public final int rank() { return 1; }

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AndType_4
	private final static class Reduction_7 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_7(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_OrT) {
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
				Automaton.Term r11 = new Automaton.Term(K_AndT, r10);
				int r12 = automaton.add(r11);
				t7.add(r12);
			}
			Automaton.Set r7 = new Automaton.Set(t7.toArray());
			int r13 = automaton.add(r7);
			Automaton.Term r14 = new Automaton.Term(K_OrT, r13);
			int r15 = automaton.add(r14);
			if(r0 != r15) {
				return automaton.rewrite(r0, r15);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AndType_4"; }
		public final int rank() { return 3; }

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $7<OrT($5<^{$2<^Type<$7|Atom<NotT($19<^Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|AndT($5)|SetT(^[^bool,$2])|TupleT(^[$2...])|FunctionT(^[$2,$2,$2...])>>...}>)>
	public final static int K_OrT = 2;
	public final static int OrT(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_OrT, r1));
	}
	public final static int OrT(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_OrT, r1));
	}

	// OrType_1
	private final static class Reduction_8 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_8(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_OrT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() == 0) {
					int[] state = {r0, r1};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.Term r2 = VoidT;
			int r3 = automaton.add(r2);
			if(r0 != r3) {
				return automaton.rewrite(r0, r3);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "OrType_1"; }
		public final int rank() { return 0; }

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// OrType_2
	private final static class Reduction_9 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_9(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_OrT) {
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
		public final String name() { return "OrType_2"; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// OrType_3
	private final static class Reduction_10 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_10(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_OrT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 1) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_OrT) {
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
			Automaton.Term r9 = new Automaton.Term(K_OrT, r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				return automaton.rewrite(r0, r10);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "OrType_3"; }
		public final int rank() { return 1; }

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $7<TupleT(^[$2<^Type<$7|Atom<NotT($19<^Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|OrT(^{$2...})|AndT(^{$2...})|SetT(^[^bool,$2])|FunctionT(^[$2,$2,$2...])>>...])>
	public final static int K_TupleT = 3;
	public final static int TupleT(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_TupleT, r1));
	}
	public final static int TupleT(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_TupleT, r1));
	}

	// TupleType_1
	private final static class Reduction_11 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_11(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_TupleT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int[] state = {r0, r1, 0};
				activations.add(new Activation(this,null,state));
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			Automaton.List r2 = ((Automaton.List) automaton.get(state[1])).sublist(0);
			Automaton.Term r3 = VoidT;
			int r4 = automaton.add(r3);
			boolean r5 = r2.contains(r4);  // VoidT in ts
			if(r5) {
				Automaton.Term r6 = VoidT;
				int r7 = automaton.add(r6);
				if(r0 != r7) {
					return automaton.rewrite(r0, r7);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "TupleType_1"; }
		public final int rank() { return 0; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// TupleType_2
	private final static class Reduction_12 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_12(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_TupleT) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							for(int r7=0;r7!=c1.size();++r7) {
								if(r7 == r3) { continue; }
								int r6 = c1.get(r7);
								Automaton.State s6 = automaton.get(r6);
								if(s6.kind == K_TupleT) {
									Automaton.Term t6 = (Automaton.Term) s6;
									int r8 = t6.contents;
									Automaton.State s8 = automaton.get(r8);
									Automaton.List l8 = (Automaton.List) s8;
									int[] state = {r0, r1, r2, r3, r4, 0, r6, r7, r8, 0, 0};
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
			Automaton.List r5 = ((Automaton.List) automaton.get(state[4])).sublist(0);
			int r7 = state[7];
			Automaton.List r9 = ((Automaton.List) automaton.get(state[8])).sublist(0);
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r7) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r10 = new Automaton.Set(s1children);
			Automaton.Int r11 = r5.lengthOf(); // |t1s|
			Automaton.Int r12 = r9.lengthOf(); // |t2s|
			boolean r13 = !r11.equals(r12); // |t1s| neq |t2s|
			if(r13) {
				Automaton.Term r14 = VoidT;
				int r15 = automaton.add(r14);
				if(r0 != r15) {
					return automaton.rewrite(r0, r15);
				}
			}
			Automaton.Int r17 = new Automaton.Int(0); // 0
			Automaton.Int r18 = r5.lengthOf(); // |t1s|
			Automaton.List r19 = Runtime.rangeOf(automaton,r17,r18); // 0 range |t1s|
			Automaton.List t16 = new Automaton.List();
			for(int i20=0;i20<r19.size();i20++) {
				Automaton.Int r20 = (Automaton.Int) automaton.get(r19.get(i20));;
				int r21 = r5.indexOf(r20);     // t1s[i]
				int r22 = r9.indexOf(r20);     // t2s[i]
				Automaton.Set r23 = new Automaton.Set(r21, r22); // {t1s[i]t2s[i]}
				int r24 = automaton.add(r23);
				Automaton.Term r25 = new Automaton.Term(K_AndT, r24);
				int r26 = automaton.add(r25);
				t16.add(r26);
			}
			Automaton.List r16 = t16;
			int r27 = automaton.add(r16);
			Automaton.Term r28 = new Automaton.Term(K_TupleT, r27);
			int r29 = automaton.add(r28);
			Automaton.Set r30 = r10.appendFront(r29); // TupleT(r) append ts
			int r31 = automaton.add(r30);
			Automaton.Term r32 = new Automaton.Term(K_AndT, r31);
			int r33 = automaton.add(r32);
			if(r0 != r33) {
				return automaton.rewrite(r0, r33);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "TupleType_2"; }
		public final int rank() { return 2; }

		public final int minimum() { return 5; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// TupleType_3
	private final static class Reduction_13 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_13(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_TupleT) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							for(int r7=0;r7!=c1.size();++r7) {
								if(r7 == r3) { continue; }
								int r6 = c1.get(r7);
								Automaton.State s6 = automaton.get(r6);
								if(s6.kind == K_NotT) {
									Automaton.Term t6 = (Automaton.Term) s6;
									int r8 = t6.contents;
									Automaton.State s8 = automaton.get(r8);
									if(s8.kind == K_TupleT) {
										Automaton.Term t8 = (Automaton.Term) s8;
										int r9 = t8.contents;
										Automaton.State s9 = automaton.get(r9);
										Automaton.List l9 = (Automaton.List) s9;
										int[] state = {r0, r1, r2, r3, r4, 0, r6, r7, r8, r9, 0, 0};
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
			int r2 = state[2]; // t1
			int r3 = state[3];
			Automaton.List r5 = ((Automaton.List) automaton.get(state[4])).sublist(0);
			int r7 = state[7];
			Automaton.List r10 = ((Automaton.List) automaton.get(state[9])).sublist(0);
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r7) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r11 = new Automaton.Set(s1children);
			Automaton.Int r12 = r5.lengthOf(); // |t1s|
			Automaton.Int r13 = r10.lengthOf(); // |t2s|
			boolean r14 = !r12.equals(r13); // |t1s| neq |t2s|
			if(r14) {
				Automaton.Set r15 = r11.appendFront(r2); // t1 append ts
				int r16 = automaton.add(r15);
				Automaton.Term r17 = new Automaton.Term(K_AndT, r16);
				int r18 = automaton.add(r17);
				if(r0 != r18) {
					return automaton.rewrite(r0, r18);
				}
			}
			Automaton.Int r19 = r5.lengthOf(); // |t1s|
			Automaton.Int r20 = new Automaton.Int(0); // 0
			boolean r21 = r19.equals(r20); // |t1s| eq 0
			if(r21) {
				Automaton.Term r22 = VoidT;
				int r23 = automaton.add(r22);
				if(r0 != r23) {
					return automaton.rewrite(r0, r23);
				}
			}
			Automaton.Int r25 = new Automaton.Int(0); // 0
			Automaton.Int r26 = r5.lengthOf(); // |t1s|
			Automaton.List r27 = Runtime.rangeOf(automaton,r25,r26); // 0 range |t1s|
			Automaton.List t24 = new Automaton.List();
			for(int i28=0;i28<r27.size();i28++) {
				Automaton.Int r28 = (Automaton.Int) automaton.get(r27.get(i28));;
				int r29 = r5.indexOf(r28);     // t1s[i]
				int r30 = r10.indexOf(r28);    // t2s[i]
				Automaton.Term r31 = new Automaton.Term(K_NotT, r30);
				int r32 = automaton.add(r31);
				Automaton.Set r33 = new Automaton.Set(r29, r32); // {t1s[i]NotT(t2s[i])}
				int r34 = automaton.add(r33);
				Automaton.Term r35 = new Automaton.Term(K_AndT, r34);
				int r36 = automaton.add(r35);
				t24.add(r36);
			}
			Automaton.List r24 = t24;
			int r37 = automaton.add(r24);
			Automaton.Term r38 = new Automaton.Term(K_TupleT, r37);
			int r39 = automaton.add(r38);
			Automaton.Set r40 = r11.appendFront(r39); // TupleT(r) append ts
			int r41 = automaton.add(r40);
			Automaton.Term r42 = new Automaton.Term(K_AndT, r41);
			int r43 = automaton.add(r42);
			if(r0 != r43) {
				return automaton.rewrite(r0, r43);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "TupleType_3"; }
		public final int rank() { return 2; }

		public final int minimum() { return 6; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $9<SetT(^[^bool,$3<^Type<$9|Atom<NotT($21<^Proton<TupleT(^[$21...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$21...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($3)|OrT(^{$3...})|AndT(^{$3...})|TupleT(^[$3...])|FunctionT(^[$3,$3,$3...])>>])>
	public final static int K_SetT = 4;
	public final static int SetT(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_SetT, r1));
	}
	public final static int SetT(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_SetT, r1));
	}

	// SetType_1
	private final static class Reduction_14 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_14(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_SetT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				int r3 = l1.get(1);
				if(Runtime.accepts(type1,automaton,automaton.get(r3), SCHEMA)) {
					int[] state = {r0, r1, r2, r3};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // b
			boolean r4 = ((Automaton.Bool)automaton.get(r2)).value;
			boolean r5 = !r4;              // !b
			if(r5) {
				Automaton.Term r6 = VoidT;
				int r7 = automaton.add(r6);
				if(r0 != r7) {
					return automaton.rewrite(r0, r7);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "SetType_1"; }
		public final int rank() { return 0; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// SetType_2
	private final static class Reduction_15 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_15(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_SetT) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							int r5 = l4.get(0);
							int r6 = l4.get(1);
							for(int r8=0;r8!=c1.size();++r8) {
								if(r8 == r3) { continue; }
								int r7 = c1.get(r8);
								Automaton.State s7 = automaton.get(r7);
								if(s7.kind == K_SetT) {
									Automaton.Term t7 = (Automaton.Term) s7;
									int r9 = t7.contents;
									Automaton.State s9 = automaton.get(r9);
									Automaton.List l9 = (Automaton.List) s9;
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

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r3 = state[3];
			int r5 = state[5]; // b1
			int r6 = state[6]; // t1
			int r8 = state[8];
			int r10 = state[10]; // b2
			int r11 = state[11]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r8) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r12 = new Automaton.Set(s1children);
			boolean r13 = ((Automaton.Bool)automaton.get(r5)).value;
			boolean r14 = false;           // b1 && b2
			if(r13) {
				boolean r15 = ((Automaton.Bool)automaton.get(r10)).value;
				r14 = r15;
			}
			int r16 = automaton.add(r14 ? Automaton.TRUE : Automaton.FALSE);
			Automaton.Set r17 = new Automaton.Set(r6, r11); // {t1t2}
			int r18 = automaton.add(r17);
			Automaton.Term r19 = new Automaton.Term(K_AndT, r18);
			int r20 = automaton.add(r19);
			Automaton.List r21 = new Automaton.List(r16, r20); // [b1 && b2AndT({t1t2})]
			int r22 = automaton.add(r21);
			Automaton.Term r23 = new Automaton.Term(K_SetT, r22);
			int r24 = automaton.add(r23);
			Automaton.Set r25 = r12.appendFront(r24); // SetT([b1 && b2AndT({t1t2})]) append ts
			int r26 = automaton.add(r25);
			Automaton.Term r27 = new Automaton.Term(K_AndT, r26);
			int r28 = automaton.add(r27);
			if(r0 != r28) {
				return automaton.rewrite(r0, r28);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "SetType_2"; }
		public final int rank() { return 2; }

		public final int minimum() { return 9; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// SetType_3
	private final static class Reduction_16 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_16(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_SetT) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							int r5 = l4.get(0);
							int r6 = l4.get(1);
							for(int r8=0;r8!=c1.size();++r8) {
								if(r8 == r3) { continue; }
								int r7 = c1.get(r8);
								Automaton.State s7 = automaton.get(r7);
								if(s7.kind == K_NotT) {
									Automaton.Term t7 = (Automaton.Term) s7;
									int r9 = t7.contents;
									Automaton.State s9 = automaton.get(r9);
									if(s9.kind == K_SetT) {
										Automaton.Term t9 = (Automaton.Term) s9;
										int r10 = t9.contents;
										Automaton.State s10 = automaton.get(r10);
										Automaton.List l10 = (Automaton.List) s10;
										int r11 = l10.get(0);
										int r12 = l10.get(1);
										int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, 0};
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
			int r5 = state[5]; // b1
			int r6 = state[6]; // t1
			int r8 = state[8];
			int r11 = state[11]; // b2
			int r12 = state[12]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r8) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r13 = new Automaton.Set(s1children);
			boolean r14 = ((Automaton.Bool)automaton.get(r5)).value;
			boolean r15 = false;           // b1 && !b2
			if(r14) {
				boolean r16 = ((Automaton.Bool)automaton.get(r11)).value;
				boolean r17 = !r16;            // !b2
				r15 = r17;
			}
			int r18 = automaton.add(r15 ? Automaton.TRUE : Automaton.FALSE);
			Automaton.Term r19 = new Automaton.Term(K_NotT, r12);
			int r20 = automaton.add(r19);
			Automaton.Set r21 = new Automaton.Set(r6, r20); // {t1NotT(t2)}
			int r22 = automaton.add(r21);
			Automaton.Term r23 = new Automaton.Term(K_AndT, r22);
			int r24 = automaton.add(r23);
			Automaton.List r25 = new Automaton.List(r18, r24); // [b1 && !b2AndT({t1NotT(t2)})]
			int r26 = automaton.add(r25);
			Automaton.Term r27 = new Automaton.Term(K_SetT, r26);
			int r28 = automaton.add(r27);
			Automaton.Set r29 = r13.appendFront(r28); // SetT([b1 && !b2AndT({t1NotT(t2)})]) append ts
			int r30 = automaton.add(r29);
			Automaton.Term r31 = new Automaton.Term(K_AndT, r30);
			int r32 = automaton.add(r31);
			if(r0 != r32) {
				return automaton.rewrite(r0, r32);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "SetType_3"; }
		public final int rank() { return 2; }

		public final int minimum() { return 10; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// SetType_4
	private final static class Reduction_17 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_17(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_SetT) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							int r5 = l4.get(0);
							int r6 = l4.get(1);
							for(int r8=0;r8!=c1.size();++r8) {
								if(r8 == r3) { continue; }
								int r7 = c1.get(r8);
								if(Runtime.accepts(type4,automaton,automaton.get(r7), SCHEMA)) {
									int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, r8, 0};
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
			int r2 = state[2]; // s
			int r3 = state[3];
			int r7 = state[7]; // p
			int r8 = state[8];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r8) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r9 = new Automaton.Set(s1children);
			boolean r10 = Runtime.accepts(type5, automaton, r7, SCHEMA); // p is ^AnyT
			boolean r11 = !r10;            // !p is ^AnyT
			if(r11) {
				Automaton.Term r12 = VoidT;
				int r13 = automaton.add(r12);
				if(r0 != r13) {
					return automaton.rewrite(r0, r13);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "SetType_4"; }
		public final int rank() { return 1; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// SetType_5
	private final static class Reduction_18 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_18(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_OrT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						Automaton.State s2 = automaton.get(r2);
						if(s2.kind == K_SetT) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							int r5 = l4.get(0);
							int r6 = l4.get(1);
							for(int r8=0;r8!=c1.size();++r8) {
								if(r8 == r3) { continue; }
								int r7 = c1.get(r8);
								Automaton.State s7 = automaton.get(r7);
								if(s7.kind == K_SetT) {
									Automaton.Term t7 = (Automaton.Term) s7;
									int r9 = t7.contents;
									Automaton.State s9 = automaton.get(r9);
									Automaton.List l9 = (Automaton.List) s9;
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

		public final int apply(Automaton automaton, int[] state) {
			int nStates = automaton.nStates();
			int r0 = state[0];
			int r2 = state[2]; // s1
			int r3 = state[3];
			int r5 = state[5]; // b1
			int r6 = state[6]; // t1
			int r7 = state[7]; // s2
			int r8 = state[8];
			int r10 = state[10]; // b2
			int r11 = state[11]; // t2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r8) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r12 = new Automaton.Set(s1children);
			boolean r13 = r6 == r11;       // t1 eq t2
			boolean r14 = false;           // t1 eq t2 && b1 && !b2
			if(r13) {
				boolean r15 = ((Automaton.Bool)automaton.get(r5)).value;
				boolean r16 = false;           // b1 && !b2
				if(r15) {
					boolean r17 = ((Automaton.Bool)automaton.get(r10)).value;
					boolean r18 = !r17;            // !b2
					r16 = r18;
				}
				r14 = r16;
			}
			if(r14) {
				Automaton.Set r19 = new Automaton.Set(r2); // {s1}
				Automaton.Set r20 = r19.append(r12); // {s1} append ts
				int r21 = automaton.add(r20);
				Automaton.Term r22 = new Automaton.Term(K_OrT, r21);
				int r23 = automaton.add(r22);
				if(r0 != r23) {
					return automaton.rewrite(r0, r23);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "SetType_5"; }
		public final int rank() { return 2; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term AnyT
	public final static int K_AnyT = 5;
	public final static Automaton.Term AnyT = new Automaton.Term(K_AnyT);

	// term VoidT
	public final static int K_VoidT = 6;
	public final static Automaton.Term VoidT = new Automaton.Term(K_VoidT);

	// term NullT
	public final static int K_NullT = 7;
	public final static Automaton.Term NullT = new Automaton.Term(K_NullT);

	// term BoolT
	public final static int K_BoolT = 8;
	public final static Automaton.Term BoolT = new Automaton.Term(K_BoolT);

	// term IntT
	public final static int K_IntT = 9;
	public final static Automaton.Term IntT = new Automaton.Term(K_IntT);

	// term RealT
	public final static int K_RealT = 10;
	public final static Automaton.Term RealT = new Automaton.Term(K_RealT);

	// term StringT
	public final static int K_StringT = 11;
	public final static Automaton.Term StringT = new Automaton.Term(K_StringT);

	// term VarT(^string)
	public final static int K_VarT = 12;
	public final static int VarT(Automaton automaton, String r0) {
		int r1 = automaton.add(new Automaton.Strung(r0));
		return automaton.add(new Automaton.Term(K_VarT, r1));
	}

	// AtomType_1
	private final static class Reduction_19 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_19(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
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
			Automaton.Term r5 = VoidT;
			int r6 = automaton.add(r5);
			if(r0 != r6) {
				return automaton.rewrite(r0, r6);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AtomType_1"; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AtomType_2
	private final static class Reduction_20 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_20(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
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
			Automaton.Term r6 = new Automaton.Term(K_AndT, r5);
			int r7 = automaton.add(r6);
			if(r0 != r7) {
				return automaton.rewrite(r0, r7);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AtomType_2"; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AtomType_3
	private final static class Reduction_21 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_21(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.Collection c1 = (Automaton.Collection) s1;
				if(c1.size() >= 2) {
					for(int r3=0;r3!=c1.size();++r3) {
						int r2 = c1.get(r3);
						if(Runtime.accepts(type4,automaton,automaton.get(r2), SCHEMA)) {
							for(int r5=0;r5!=c1.size();++r5) {
								if(r5 == r3) { continue; }
								int r4 = c1.get(r5);
								if(Runtime.accepts(type4,automaton,automaton.get(r4), SCHEMA)) {
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
			boolean r8 = false;            // a1 neq a2 && !a2 is ^AnyT
			if(r7) {
				boolean r9 = Runtime.accepts(type5, automaton, r4, SCHEMA); // a2 is ^AnyT
				boolean r10 = !r9;             // !a2 is ^AnyT
				r8 = r10;
			}
			if(r8) {
				Automaton.Term r11 = VoidT;
				int r12 = automaton.add(r11);
				if(r0 != r12) {
					return automaton.rewrite(r0, r12);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AtomType_3"; }
		public final int rank() { return 1; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AtomType_4
	private final static class Reduction_22 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_22(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_AndT) {
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
								Automaton.State s4 = automaton.get(r4);
								if(s4.kind == K_NotT) {
									Automaton.Term t4 = (Automaton.Term) s4;
									int r6 = t4.contents;
									if(Runtime.accepts(type4,automaton,automaton.get(r6), SCHEMA)) {
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
				Automaton.Term r9 = VoidT;
				int r10 = automaton.add(r9);
				if(r0 != r10) {
					return automaton.rewrite(r0, r10);
				}
			}
			Automaton.Term r11 = AnyT;
			Object r12 = (Object) automaton.get(r6);
			boolean r13 = !r12.equals(r11); // a2 neq AnyT
			if(r13) {
				Automaton.Set r14 = r7.appendFront(r2); // a1 append ts
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_AndT, r15);
				int r17 = automaton.add(r16);
				if(r0 != r17) {
					return automaton.rewrite(r0, r17);
				}
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AtomType_4"; }
		public final int rank() { return 1; }

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AtomType_5
	private final static class Reduction_23 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_23(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_OrT) {
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
			Automaton.Term r5 = AnyT;
			int r6 = automaton.add(r5);
			if(r0 != r6) {
				return automaton.rewrite(r0, r6);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AtomType_5"; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// AtomType_5
	private final static class Reduction_24 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_24(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_OrT) {
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
			Automaton.Term r6 = new Automaton.Term(K_OrT, r5);
			int r7 = automaton.add(r6);
			if(r0 != r7) {
				return automaton.rewrite(r0, r7);
			}
			automaton.resize(nStates);
			return Automaton.K_VOID;
		}
		public final String name() { return "AtomType_5"; }
		public final int rank() { return 0; }

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $8<FunctionT(^[$2<^Type<$8|Atom<NotT($20<^Proton<TupleT(^[$20...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$20...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|OrT(^{$2...})|AndT(^{$2...})|SetT(^[^bool,$2])|TupleT(^[$2...])>>,$2,$2...])>
	public final static int K_FunctionT = 13;
	public final static int FunctionT(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_FunctionT, r1));
	}
	public final static int FunctionT(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_FunctionT, r1));
	}

	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// $4<NotT($2<^Type<$4|Atom<NotT($16<^Proton<TupleT(^[$16...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$16...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|OrT(^{$2...})|AndT(^{$2...})|SetT(^[^bool,$2])|TupleT(^[$2...])|FunctionT(^[$2,$2,$2...])>>)>
		Schema.Term("NotT",Schema.Or(Schema.Any, Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("TupleT",Schema.List(true)), Schema.Or(Schema.Term("AnyT"), Schema.Term("NullT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("VarT",Schema.String)))), Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any)))),
		// $7<AndT($5<^{$2<^Type<$7|Atom<NotT($19<^Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|OrT($5)|SetT(^[^bool,$2])|TupleT(^[$2...])|FunctionT(^[$2,$2,$2...])>>...}>)>
		Schema.Term("AndT",Schema.Set(true)),
		// $7<OrT($5<^{$2<^Type<$7|Atom<NotT($19<^Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|AndT($5)|SetT(^[^bool,$2])|TupleT(^[$2...])|FunctionT(^[$2,$2,$2...])>>...}>)>
		Schema.Term("OrT",Schema.Set(true)),
		// $7<TupleT(^[$2<^Type<$7|Atom<NotT($19<^Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$19...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|OrT(^{$2...})|AndT(^{$2...})|SetT(^[^bool,$2])|FunctionT(^[$2,$2,$2...])>>...])>
		Schema.Term("TupleT",Schema.List(true)),
		// $9<SetT(^[^bool,$3<^Type<$9|Atom<NotT($21<^Proton<TupleT(^[$21...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$21...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($3)|OrT(^{$3...})|AndT(^{$3...})|TupleT(^[$3...])|FunctionT(^[$3,$3,$3...])>>])>
		Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Or(Schema.Any, Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("TupleT",Schema.List(true)), Schema.Or(Schema.Term("AnyT"), Schema.Term("NullT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("VarT",Schema.String)))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("TupleT",Schema.List(true)), Schema.Term("FunctionT",Schema.List(true,Schema.Any,Schema.Any))))),
		// AnyT
		Schema.Term("AnyT"),
		// VoidT
		Schema.Term("VoidT"),
		// NullT
		Schema.Term("NullT"),
		// BoolT
		Schema.Term("BoolT"),
		// IntT
		Schema.Term("IntT"),
		// RealT
		Schema.Term("RealT"),
		// StringT
		Schema.Term("StringT"),
		// VarT(^string)
		Schema.Term("VarT",Schema.String),
		// $8<FunctionT(^[$2<^Type<$8|Atom<NotT($20<^Proton<TupleT(^[$20...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$20...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT($2)|OrT(^{$2...})|AndT(^{$2...})|SetT(^[^bool,$2])|TupleT(^[$2...])>>,$2,$2...])>
		Schema.Term("FunctionT",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("NotT",Schema.Or(Schema.Term("TupleT",Schema.List(true)), Schema.Or(Schema.Term("AnyT"), Schema.Term("NullT"), Schema.Term("VoidT"), Schema.Term("BoolT"), Schema.Term("IntT"), Schema.Term("RealT"), Schema.Term("StringT"), Schema.Term("VarT",Schema.String)))), Schema.Any), Schema.Term("NotT",Schema.Any), Schema.Term("OrT",Schema.Set(true)), Schema.Term("AndT",Schema.Any), Schema.Term("SetT",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("TupleT",Schema.List(true))),Schema.Any))
	});

	// =========================================================================
	// Types
	// =========================================================================

	// AnyT
	private static Type type0 = Runtime.Type("2G0tLTJCWDggIk2");
	// VoidT
	private static Type type1 = Runtime.Type("2KLxLPZGp3ukmD0E");
	// $11<Type<Atom<NotT($13<^Proton<TupleT(^[$13...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>)|Proton<TupleT(^[$13...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>|NotT(^$11)|OrT(^{^$11...})|AndT(^{^$11...})|SetT(^[^bool,^$11])|TupleT(^[^$11...])|FunctionT(^[^$11,^$11,^$11...])>>
	private static Type type2 = Runtime.Type("j53GKTkK5G0GrQhGZIjG6KowZRJGJFiG5K3CKOoG4OJK6RgK5KJ55KbQYGMPjt5Klppf0jWjGnWq0ul59CXDAp1IZ0_RjGrQidmQYknIgVK7Oxq5PCmDQp1MJ4KSW8rPYw3Atw_9cC1HdlXPWg5fl5gCXIhpXQ34ZQtGp3slHTJtJSgl5KIg6AwG_Kj_5OJCWUgw6K1xqQgGp385gcNFJHiG6KIkNkHY0KHKLNgGp3E5gVOFrJo8MPiS5KIcOkma0GL4aRJdH5YoOsOkmbGVWTxWNgNsNZOkOVPgGel7TdPomeW9CC1Eh5gsPBHfl9jVQcHil7xcQkmil7ycQkHjW9RBmjGfl7SVRZImW996Ywnm0AB6YoPkHDD6ewRB1HN6gZSBHfGfl7h5Q6tkScHrl78tSkmrGo3");
	// bool
	private static Type type3 = Runtime.Type("Fk0");
	// $12<Proton<TupleT(^[^$12...])|Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>>>
	private static Type type4 = Runtime.Type("PG5Jmx5Sjt5KGKMNmh5OJK6RgK5Keso7xkHDycmEYk2HgZJ79hNglHYc2Iho3G0tLTJClIgV4KDK6QgGp3PlmLJOpQdG5KIo4ATG_Fjx5QJC0PgZ5G8t5SJClPgk5KHKLNgGp3ilmQoC4Sm_aQbGp3tlXT3OKNmG_9OB1UxlXU05OlpMep5Tvxr575YgIXGA95WI");
	// ^AnyT
	private static Type type5 = Runtime.Type("3G0tLTJCWDggY9w3x$");
	// Quark<AnyT|NullT|VoidT|BoolT|IntT|RealT|StringT|VarT(^string)>
	private static Type type6 = Runtime.Type("yFKJp4aRfGJFi_6KIg2AwF_Ipl5QJCWEgw2KLxLPZGp38lXHJ8oQjl5KIk3ACGJHiG6KIw3ANG_J_45QJCWLgg4SIGbRdtqOJCGMgs4GL4aRJdH5YVLPgcLVGE7hZIOlpMfh0QYcIQhsLw5");

	// =========================================================================
	// Patterns
	// =========================================================================

	private final static Pattern.Term pattern0 = new Pattern.Term("NotT",
		new Pattern.Leaf(type0),
		null);
	private final static Pattern.Term pattern1 = new Pattern.Term("NotT",
		new Pattern.Leaf(type1),
		null);
	private final static Pattern.Term pattern2 = new Pattern.Term("NotT",
		new Pattern.Term("OrT",
			new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type2), "es")}),
			null),
		null);
	private final static Pattern.Term pattern3 = new Pattern.Term("NotT",
		new Pattern.Term("AndT",
			new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type2), "es")}),
			null),
		null);
	private final static Pattern.Term pattern4 = new Pattern.Term("AndT",
		new Pattern.Set(false, new Pair[]{}),
		null);
	private final static Pattern.Term pattern5 = new Pattern.Term("AndT",
		new Pattern.Set(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "t")}),
		null);
	private final static Pattern.Term pattern6 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("AndT",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern7 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("OrT",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern8 = new Pattern.Term("OrT",
		new Pattern.Set(false, new Pair[]{}),
		null);
	private final static Pattern.Term pattern9 = new Pattern.Term("OrT",
		new Pattern.Set(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "t")}),
		null);
	private final static Pattern.Term pattern10 = new Pattern.Term("OrT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("OrT",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern11 = new Pattern.Term("TupleT",
		new Pattern.List(true, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern12 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("TupleT",
				new Pattern.List(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "t1s")}),
				null),null), 
			new Pair(new Pattern.Term("TupleT",
				new Pattern.List(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "t2s")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern13 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("TupleT",
				new Pattern.List(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "t1s")}),
				null), "t1"), 
			new Pair(new Pattern.Term("NotT",
				new Pattern.Term("TupleT",
					new Pattern.List(true, new Pair[]{
						new Pair(new Pattern.Leaf(type2), "t2s")}),
					null),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern14 = new Pattern.Term("SetT",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Leaf(type3), "b"), 
			new Pair(new Pattern.Leaf(type1),null)}),
		null);
	private final static Pattern.Term pattern15 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("SetT",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3), "b1"), 
					new Pair(new Pattern.Leaf(type2), "t1")}),
				null),null), 
			new Pair(new Pattern.Term("SetT",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3), "b2"), 
					new Pair(new Pattern.Leaf(type2), "t2")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern16 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("SetT",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3), "b1"), 
					new Pair(new Pattern.Leaf(type2), "t1")}),
				null),null), 
			new Pair(new Pattern.Term("NotT",
				new Pattern.Term("SetT",
					new Pattern.List(false, new Pair[]{
						new Pair(new Pattern.Leaf(type3), "b2"), 
						new Pair(new Pattern.Leaf(type2), "t2")}),
					null),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern17 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("SetT",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3),null), 
					new Pair(new Pattern.Leaf(type2),null)}),
				null), "s"), 
			new Pair(new Pattern.Leaf(type4), "p"), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern18 = new Pattern.Term("OrT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("SetT",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3), "b1"), 
					new Pair(new Pattern.Leaf(type2), "t1")}),
				null), "s1"), 
			new Pair(new Pattern.Term("SetT",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Leaf(type3), "b2"), 
					new Pair(new Pattern.Leaf(type2), "t2")}),
				null), "s2"), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern19 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type1),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern20 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type0),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern21 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type4), "a1"), 
			new Pair(new Pattern.Leaf(type4), "a2"), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern22 = new Pattern.Term("AndT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type6), "a1"), 
			new Pair(new Pattern.Term("NotT",
				new Pattern.Leaf(type4),
				"a2"),null), 
			new Pair(new Pattern.Leaf(type2), "ts")}),
		null);
	private final static Pattern.Term pattern23 = new Pattern.Term("OrT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type0),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern24 = new Pattern.Term("OrT",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type1),null), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
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
		new Reduction_24(pattern24)
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
