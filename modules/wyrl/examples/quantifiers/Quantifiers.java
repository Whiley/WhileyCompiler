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

public final class Quantifiers {
	// term Var(^string)
	public final static int K_Var = 0;
	public final static int Var(Automaton automaton, String r0) {
		int r1 = automaton.add(new Automaton.Strung(r0));
		return automaton.add(new Automaton.Term(K_Var, r1));
	}

	// term True
	public final static int K_True = 1;
	public final static Automaton.Term True = new Automaton.Term(K_True);

	// term False
	public final static int K_False = 2;
	public final static Automaton.Term False = new Automaton.Term(K_False);

	// term Fn(^[^string,^Expr...])
	public final static int K_Fn = 3;
	public final static int Fn(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fn, r1));
	}
	public final static int Fn(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fn, r1));
	}

	// term $4<Not($2<^BExpr>)>
	public final static int K_Not = 4;
	public final static int Not(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Not, r0));
	}

	// Not(Bool b)
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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r1 = state[1]; // b
			Automaton.Term r2 = True;
			Object r3 = (Object) automaton.get(r1);
			boolean r4 = r3.equals(r2);    // b eq True
			if(r4) {
				Automaton.Term r5 = False;
				int r6 = automaton.add(r5);
				if(r0 != r6) {
					automaton.rewrite(r0, r6);
					return true;
				}
			}
			Automaton.Term r7 = True;
			int r8 = automaton.add(r7);
			if(r0 != r8) {
				automaton.rewrite(r0, r8);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not(Not(any x))
	private final static class Reduction_1 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_1(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Not) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				if(s1.kind == K_Not) {
					Automaton.Term t1 = (Automaton.Term) s1;
					int r2 = t1.contents;
					int[] state = {r0, r1, r2};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // x
			if(r0 != r2) {
				automaton.rewrite(r0, r2);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 1; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not(And({$12<BExpr> xs...}))
	private final static class Reduction_2 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_2(Pattern.Term pattern) { super(pattern); }

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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
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
				automaton.rewrite(r0, r10);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not(Or({$12<BExpr> xs...}))
	private final static class Reduction_3 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_3(Pattern.Term pattern) { super(pattern); }

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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
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
				automaton.rewrite(r0, r10);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $7<And($5<^{$2<^BExpr>...}>)>
	public final static int K_And = 5;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// And({$12<BExpr> x})
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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // x
			int r3 = state[3];
			if(r0 != r2) {
				automaton.rewrite(r0, r2);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// And({Bool b, $12<BExpr> xs...})
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
						if(Runtime.accepts(type0,automaton,automaton.get(r2), SCHEMA)) {
							int[] state = {r0, r1, r2, r3, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // b
			int r3 = state[3];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r4 = new Automaton.Set(s1children);
			Automaton.Term r5 = False;
			Object r6 = (Object) automaton.get(r2);
			boolean r7 = r6.equals(r5);    // b eq False
			if(r7) {
				Automaton.Term r8 = False;
				int r9 = automaton.add(r8);
				if(r0 != r9) {
					automaton.rewrite(r0, r9);
					return true;
				}
			}
			Automaton.Int r10 = r4.lengthOf(); // |xs|
			Automaton.Int r11 = new Automaton.Int(0); // 0
			boolean r12 = r10.equals(r11); // |xs| eq 0
			if(r12) {
				Automaton.Term r13 = True;
				int r14 = automaton.add(r13);
				if(r0 != r14) {
					automaton.rewrite(r0, r14);
					return true;
				}
			}
			int r15 = automaton.add(r4);
			Automaton.Term r16 = new Automaton.Term(K_And, r15);
			int r17 = automaton.add(r16);
			if(r0 != r17) {
				automaton.rewrite(r0, r17);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// And({And({$12<BExpr> xs...}), $12<BExpr> ys...})
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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
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
				automaton.rewrite(r0, r10);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// And({Not($12<BExpr> x), $12<BExpr> y, $12<BExpr> ys...})
	private final static class Reduction_7 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_7(Pattern.Term pattern) { super(pattern); }

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
						if(s2.kind == K_Not) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							for(int r6=0;r6!=c1.size();++r6) {
								if(r6 == r3) { continue; }
								int r5 = c1.get(r6);
								int[] state = {r0, r1, r2, r3, r4, r5, r6, 0};
								activations.add(new Activation(this,null,state));
							}
						}
					}
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r3 = state[3];
			int r4 = state[4]; // x
			int r5 = state[5]; // y
			int r6 = state[6];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r7 = new Automaton.Set(s1children);
			boolean r8 = r4 == r5;         // x eq y
			if(r8) {
				Automaton.Term r9 = False;
				int r10 = automaton.add(r9);
				if(r0 != r10) {
					automaton.rewrite(r0, r10);
					return true;
				}
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// And({Or({$12<BExpr> xs...}), $12<BExpr> ys...})
	private final static class Reduction_8 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_8(Pattern.Term pattern) { super(pattern); }

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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
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
				automaton.rewrite(r0, r15);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $7<Or($5<^{$2<^BExpr>...}>)>
	public final static int K_Or = 6;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// Or({$12<BExpr> x})
	private final static class Reduction_9 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_9(Pattern.Term pattern) { super(pattern); }

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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // x
			int r3 = state[3];
			if(r0 != r2) {
				automaton.rewrite(r0, r2);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Or({Bool b, $12<BExpr> xs...})
	private final static class Reduction_10 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_10(Pattern.Term pattern) { super(pattern); }

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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // b
			int r3 = state[3];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r4 = new Automaton.Set(s1children);
			Automaton.Term r5 = True;
			Object r6 = (Object) automaton.get(r2);
			boolean r7 = r6.equals(r5);    // b eq True
			if(r7) {
				Automaton.Term r8 = True;
				int r9 = automaton.add(r8);
				if(r0 != r9) {
					automaton.rewrite(r0, r9);
					return true;
				}
			}
			Automaton.Int r10 = r4.lengthOf(); // |xs|
			Automaton.Int r11 = new Automaton.Int(0); // 0
			boolean r12 = r10.equals(r11); // |xs| eq 0
			if(r12) {
				Automaton.Term r13 = False;
				int r14 = automaton.add(r13);
				if(r0 != r14) {
					automaton.rewrite(r0, r14);
					return true;
				}
			}
			int r15 = automaton.add(r4);
			Automaton.Term r16 = new Automaton.Term(K_Or, r15);
			int r17 = automaton.add(r16);
			if(r0 != r17) {
				automaton.rewrite(r0, r17);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 2; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Or({Not($12<BExpr> x), $12<BExpr> y, $12<BExpr> ys...})
	private final static class Reduction_11 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_11(Pattern.Term pattern) { super(pattern); }

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
						if(s2.kind == K_Not) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							for(int r6=0;r6!=c1.size();++r6) {
								if(r6 == r3) { continue; }
								int r5 = c1.get(r6);
								int[] state = {r0, r1, r2, r3, r4, r5, r6, 0};
								activations.add(new Activation(this,null,state));
							}
						}
					}
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r3 = state[3];
			int r4 = state[4]; // x
			int r5 = state[5]; // y
			int r6 = state[6];
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r6) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r7 = new Automaton.Set(s1children);
			boolean r8 = r4 == r5;         // x eq y
			if(r8) {
				Automaton.Term r9 = True;
				int r10 = automaton.add(r9);
				if(r0 != r10) {
					automaton.rewrite(r0, r10);
					return true;
				}
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Or({Or({$12<BExpr> xs...}), $12<BExpr> ys...})
	private final static class Reduction_12 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_12(Pattern.Term pattern) { super(pattern); }

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

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
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
				automaton.rewrite(r0, r10);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 3; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $13<ForAll($11<^[^{^Var(^string)...},$7<^BExpr>]>)>
	public final static int K_ForAll = 7;
	public final static int ForAll(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_ForAll, r1));
	}
	public final static int ForAll(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_ForAll, r1));
	}

	// ForAll([{Var(^string) qs...}, $12<BExpr> be])
	private final static class Reduction_13 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_13(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_ForAll) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				Automaton.State s2 = automaton.get(r2);
				Automaton.Collection c2 = (Automaton.Collection) s2;
				int r4 = l1.get(1);
				int[] state = {r0, r1, r2, 0, r4};
				activations.add(new Activation(this,null,state));
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			Automaton.Collection s2 = (Automaton.Collection) automaton.get(state[2]);
			int[] s2children = new int[s2.size() - 0];
			for(int s2i=0, s2j=0; s2i != s2.size();++s2i) {
				s2children[s2j++] = s2.get(s2i);
			}
			Automaton.Set r3 = new Automaton.Set(s2children);
			int r4 = state[4]; // be
			boolean r5 = Runtime.accepts(type4, automaton, r4, SCHEMA); // be is ^Bool
			Automaton.Int r6 = r3.lengthOf(); // |qs|
			Automaton.Int r7 = new Automaton.Int(0); // 0
			boolean r8 = r6.equals(r7);    // |qs| eq 0
			boolean r9 = r5 || r8;         // be is ^Bool || |qs| eq 0
			if(r9) {
				if(r0 != r4) {
					automaton.rewrite(r0, r4);
					return true;
				}
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not(ForAll([{Var(^string)...} vars, $12<BExpr> be]))
	private final static class Reduction_14 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_14(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Not) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				if(s1.kind == K_ForAll) {
					Automaton.Term t1 = (Automaton.Term) s1;
					int r2 = t1.contents;
					Automaton.State s2 = automaton.get(r2);
					Automaton.List l2 = (Automaton.List) s2;
					int r3 = l2.get(0);
					Automaton.State s3 = automaton.get(r3);
					Automaton.Collection c3 = (Automaton.Collection) s3;
					int r5 = l2.get(1);
					int[] state = {r0, r1, r2, r3, 0, r5};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r3 = state[3]; // vars
			int r5 = state[5]; // be
			Automaton.Term r6 = new Automaton.Term(K_Not, r5);
			int r7 = automaton.add(r6);
			Automaton.List r8 = new Automaton.List(r3, r7); // [varsNot(be)]
			int r9 = automaton.add(r8);
			Automaton.Term r10 = new Automaton.Term(K_Exists, r9);
			int r11 = automaton.add(r10);
			if(r0 != r11) {
				automaton.rewrite(r0, r11);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 4; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// ForAll([{Var(^string)...} xs, ForAll([{Var(^string)...} ys, $12<BExpr> e])])
	private final static class Reduction_15 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_15(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_ForAll) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				Automaton.State s2 = automaton.get(r2);
				Automaton.Collection c2 = (Automaton.Collection) s2;
				int r4 = l1.get(1);
				Automaton.State s4 = automaton.get(r4);
				if(s4.kind == K_ForAll) {
					Automaton.Term t4 = (Automaton.Term) s4;
					int r5 = t4.contents;
					Automaton.State s5 = automaton.get(r5);
					Automaton.List l5 = (Automaton.List) s5;
					int r6 = l5.get(0);
					Automaton.State s6 = automaton.get(r6);
					Automaton.Collection c6 = (Automaton.Collection) s6;
					int r8 = l5.get(1);
					int[] state = {r0, r1, r2, 0, r4, r5, r6, 0, r8};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // xs
			int r6 = state[6]; // ys
			int r8 = state[8]; // e
			Automaton.Set r9 = (Automaton.Set) automaton.get(r2);
			Automaton.Set r10 = (Automaton.Set) automaton.get(r6);
			Automaton.Set r11 = r9.append(r10); // xs append ys
			int r12 = automaton.add(r11);
			Automaton.List r13 = new Automaton.List(r12, r8); // [xs append yse]
			int r14 = automaton.add(r13);
			Automaton.Term r15 = new Automaton.Term(K_ForAll, r14);
			int r16 = automaton.add(r15);
			if(r0 != r16) {
				automaton.rewrite(r0, r16);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 6; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// And({$12<BExpr> e1, ForAll([{Var(^string), Var(^string)...} vs, $12<BExpr> e2]) qf, $12<BExpr> es...})
	private final static class Inference_0 extends AbstractRewriteRule implements InferenceRule {

		public Inference_0(Pattern.Term pattern) { super(pattern); }

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
						for(int r5=0;r5!=c1.size();++r5) {
							if(r5 == r3) { continue; }
							int r4 = c1.get(r5);
							Automaton.State s4 = automaton.get(r4);
							if(s4.kind == K_ForAll) {
								Automaton.Term t4 = (Automaton.Term) s4;
								int r6 = t4.contents;
								Automaton.State s6 = automaton.get(r6);
								Automaton.List l6 = (Automaton.List) s6;
								int r7 = l6.get(0);
								Automaton.State s7 = automaton.get(r7);
								Automaton.Collection c7 = (Automaton.Collection) s7;
								if(c7.size() >= 1) {
									for(int r9=0;r9!=c7.size();++r9) {
										int r8 = c7.get(r9);
										int r11 = l6.get(1);
										int[] state = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, 0, r11, 0};
										activations.add(new Activation(this,null,state));
									}
								}
							}
						}
					}
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // e1
			int r3 = state[3];
			int r4 = state[4]; // qf
			int r5 = state[5];
			int r7 = state[7]; // vs
			int r9 = state[9];
			int r11 = state[11]; // e2
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 2];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3 || s1i == r5) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r12 = new Automaton.Set(s1children);
			Automaton.List r13 = new Automaton.List(r2, r7, r11); // [e1vse2]
			Automaton.Set r14 = Quantifiers$native.instantiate(automaton, r13);
			Automaton.Int r15 = r14.lengthOf(); // |instantiations|
			Automaton.Int r16 = new Automaton.Int(0); // 0
			boolean r17 = r15.compareTo(r16)>0; // |instantiations| gt 0
			if(r17) {
				Automaton.Set r18 = new Automaton.Set(r2, r4); // {e1qf}
				Automaton.Set r19 = r12.append(r14); // es append instantiations
				Automaton.Set r20 = r18.append(r19); // {e1qf} append es append instantiations
				int r21 = automaton.add(r20);
				Automaton.Term r22 = new Automaton.Term(K_And, r21);
				int r23 = automaton.add(r22);
				if(r0 != r23) {
					automaton.rewrite(r0, r23);
					return true;
				}
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// term $13<Exists($11<^[^{^Var(^string)...},$7<^BExpr>]>)>
	public final static int K_Exists = 8;
	public final static int Exists(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Exists, r1));
	}
	public final static int Exists(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Exists, r1));
	}

	// Exists([{Var(^string) qs...}, $12<BExpr> be])
	private final static class Reduction_16 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_16(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Exists) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				Automaton.State s2 = automaton.get(r2);
				Automaton.Collection c2 = (Automaton.Collection) s2;
				int r4 = l1.get(1);
				int[] state = {r0, r1, r2, 0, r4};
				activations.add(new Activation(this,null,state));
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			Automaton.Collection s2 = (Automaton.Collection) automaton.get(state[2]);
			int[] s2children = new int[s2.size() - 0];
			for(int s2i=0, s2j=0; s2i != s2.size();++s2i) {
				s2children[s2j++] = s2.get(s2i);
			}
			Automaton.Set r3 = new Automaton.Set(s2children);
			int r4 = state[4]; // be
			boolean r5 = Runtime.accepts(type4, automaton, r4, SCHEMA); // be is ^Bool
			Automaton.Int r6 = r3.lengthOf(); // |qs|
			Automaton.Int r7 = new Automaton.Int(0); // 0
			boolean r8 = r6.equals(r7);    // |qs| eq 0
			boolean r9 = r5 || r8;         // be is ^Bool || |qs| eq 0
			if(r9) {
				if(r0 != r4) {
					automaton.rewrite(r0, r4);
					return true;
				}
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 0; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Not(Exists([{Var(^string)...} vars, $12<BExpr> be]))
	private final static class Reduction_17 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_17(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Not) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				if(s1.kind == K_Exists) {
					Automaton.Term t1 = (Automaton.Term) s1;
					int r2 = t1.contents;
					Automaton.State s2 = automaton.get(r2);
					Automaton.List l2 = (Automaton.List) s2;
					int r3 = l2.get(0);
					Automaton.State s3 = automaton.get(r3);
					Automaton.Collection c3 = (Automaton.Collection) s3;
					int r5 = l2.get(1);
					int[] state = {r0, r1, r2, r3, 0, r5};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r3 = state[3]; // vars
			int r5 = state[5]; // be
			Automaton.Term r6 = new Automaton.Term(K_Not, r5);
			int r7 = automaton.add(r6);
			Automaton.List r8 = new Automaton.List(r3, r7); // [varsNot(be)]
			int r9 = automaton.add(r8);
			Automaton.Term r10 = new Automaton.Term(K_ForAll, r9);
			int r11 = automaton.add(r10);
			if(r0 != r11) {
				automaton.rewrite(r0, r11);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 4; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// Exists([{Var(^string)...} xs, Exists([{Var(^string)...} ys, $12<BExpr> e])])
	private final static class Reduction_18 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_18(Pattern.Term pattern) { super(pattern); }

		public final void probe(Automaton automaton, int root, List<Activation> activations) {
			int r0 = root;
			Automaton.State s0 = automaton.get(r0);
			if(s0.kind == K_Exists) {
				Automaton.Term t0 = (Automaton.Term) s0;
				int r1 = t0.contents;
				Automaton.State s1 = automaton.get(r1);
				Automaton.List l1 = (Automaton.List) s1;
				int r2 = l1.get(0);
				Automaton.State s2 = automaton.get(r2);
				Automaton.Collection c2 = (Automaton.Collection) s2;
				int r4 = l1.get(1);
				Automaton.State s4 = automaton.get(r4);
				if(s4.kind == K_Exists) {
					Automaton.Term t4 = (Automaton.Term) s4;
					int r5 = t4.contents;
					Automaton.State s5 = automaton.get(r5);
					Automaton.List l5 = (Automaton.List) s5;
					int r6 = l5.get(0);
					Automaton.State s6 = automaton.get(r6);
					Automaton.Collection c6 = (Automaton.Collection) s6;
					int r8 = l5.get(1);
					int[] state = {r0, r1, r2, 0, r4, r5, r6, 0, r8};
					activations.add(new Activation(this,null,state));
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r2 = state[2]; // xs
			int r6 = state[6]; // ys
			int r8 = state[8]; // e
			Automaton.Set r9 = (Automaton.Set) automaton.get(r2);
			Automaton.Set r10 = (Automaton.Set) automaton.get(r6);
			Automaton.Set r11 = r9.append(r10); // xs append ys
			int r12 = automaton.add(r11);
			Automaton.List r13 = new Automaton.List(r12, r8); // [xs append yse]
			int r14 = automaton.add(r13);
			Automaton.Term r15 = new Automaton.Term(K_Exists, r14);
			int r16 = automaton.add(r15);
			if(r0 != r16) {
				automaton.rewrite(r0, r16);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 6; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// And({Exists([{Var(^string)...} vs, $12<BExpr> e]), $12<BExpr> es...})
	private final static class Reduction_19 extends AbstractRewriteRule implements ReductionRule {

		public Reduction_19(Pattern.Term pattern) { super(pattern); }

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
						if(s2.kind == K_Exists) {
							Automaton.Term t2 = (Automaton.Term) s2;
							int r4 = t2.contents;
							Automaton.State s4 = automaton.get(r4);
							Automaton.List l4 = (Automaton.List) s4;
							int r5 = l4.get(0);
							Automaton.State s5 = automaton.get(r5);
							Automaton.Collection c5 = (Automaton.Collection) s5;
							int r7 = l4.get(1);
							int[] state = {r0, r1, r2, r3, r4, r5, 0, r7, 0};
							activations.add(new Activation(this,null,state));
						}
					}
				}
			}
		}

		public final boolean apply(Automaton automaton, Object _state) {
			int nStates = automaton.nStates();
			int[] state = (int[]) _state;
			int r0 = state[0];
			int r3 = state[3];
			int r5 = state[5]; // vs
			int r7 = state[7]; // e
			Automaton.Collection s1 = (Automaton.Collection) automaton.get(state[1]);
			int[] s1children = new int[s1.size() - 1];
			for(int s1i=0, s1j=0; s1i != s1.size();++s1i) {
				if(s1i == r3) { continue; }
				s1children[s1j++] = s1.get(s1i);
			}
			Automaton.Set r8 = new Automaton.Set(s1children);
			Automaton.Set r9 = r8.appendFront(r7); // e append es
			int r10 = automaton.add(r9);
			Automaton.Term r11 = new Automaton.Term(K_And, r10);
			int r12 = automaton.add(r11);
			Automaton.List r13 = new Automaton.List(r5, r12); // [vsAnd(e append es)]
			int r14 = automaton.add(r13);
			Automaton.Term r15 = new Automaton.Term(K_Exists, r14);
			int r16 = automaton.add(r15);
			if(r0 != r16) {
				automaton.rewrite(r0, r16);
				return true;
			}
			automaton.resize(nStates);
			return false;
		}

		public final int minimum() { return 5; }
		public final int maximum() { return Integer.MAX_VALUE; }
	}
	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// Var(^string)
		Schema.Term("Var",Schema.String),
		// True
		Schema.Term("True"),
		// False
		Schema.Term("False"),
		// Fn(^[^string,^Expr...])
		Schema.Term("Fn",Schema.List(true,Schema.String)),
		// $4<Not($2<^BExpr>)>
		Schema.Term("Not",Schema.Or(Schema.Any, Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Var",Schema.String), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Any)), Schema.Term("Exists",Schema.Any))),
		// $7<And($5<^{$2<^BExpr>...}>)>
		Schema.Term("And",Schema.Set(true)),
		// $7<Or($5<^{$2<^BExpr>...}>)>
		Schema.Term("Or",Schema.Set(true)),
		// $13<ForAll($11<^[^{^Var(^string)...},$7<^BExpr>]>)>
		Schema.Term("ForAll",Schema.List(true,Schema.Set(true),Schema.Or(Schema.Any, Schema.Term("Var",Schema.String), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Not",Schema.Any), Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("Exists",Schema.Any)))),
		// $13<Exists($11<^[^{^Var(^string)...},$7<^BExpr>]>)>
		Schema.Term("Exists",Schema.List(true,Schema.Set(true),Schema.Or(Schema.Any, Schema.Term("Var",Schema.String), Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("Not",Schema.Any), Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Fn",Schema.List(true,Schema.Any)), Schema.Term("ForAll",Schema.Any))))
	});

	// =========================================================================
	// Types
	// =========================================================================

	// Bool
	private static Type type0 = Runtime.Type("QFZFjx5Q3G_RpKq3vk1EJOJNgCMOIs2Az3HE7hGHYcYHhgJko2");
	// any
	private static Type type1 = Runtime.Type("Fs0");
	// $12<BExpr>
	private static Type type2 = Runtime.Type("uG_F4W6RmGZFjx5QosoQoGIFiG58E86CL4aRZNZQZOoQm43QgGLGs_qRoCMV0PfWbTxxNZPgPglHYc2Iho3GJ8MS_ClIgV4K545QnKq3PlmLWZ4MQop7vsKAUdXIYkIPgcq7dhmAgdHQYoYQgwq7ys5AtdH5YwnTgk6G4W6RmCWUgwb975Igr78595tgNc1Yl77pNkXYW9xC1al9jZOcXal7Q5dCH6R5toOcXbl78xOk1el79xOkXeGso2");
	// Var(^string)
	private static Type type3 = Runtime.Type("3CL4aReZl7ug2Aw3x$");
	// ^Bool
	private static Type type4 = Runtime.Type("RFZFjx5Qeo3GJ8MS_C0Ego2K545QnKq3zk1HWsIHQco7ugJAB4vw");

	// =========================================================================
	// Patterns
	// =========================================================================

	private final static Pattern.Term pattern0 = new Pattern.Term("Not",
		new Pattern.Leaf(type0),
		"b");
	private final static Pattern.Term pattern1 = new Pattern.Term("Not",
		new Pattern.Term("Not",
			new Pattern.Leaf(type1),
			"x"),
		null);
	private final static Pattern.Term pattern2 = new Pattern.Term("Not",
		new Pattern.Term("And",
			new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type2), "xs")}),
			null),
		null);
	private final static Pattern.Term pattern3 = new Pattern.Term("Not",
		new Pattern.Term("Or",
			new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type2), "xs")}),
			null),
		null);
	private final static Pattern.Term pattern4 = new Pattern.Term("And",
		new Pattern.Set(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "x")}),
		null);
	private final static Pattern.Term pattern5 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type0), "b"), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern6 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("And",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern7 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Not",
				new Pattern.Leaf(type2),
				"x"),null), 
			new Pair(new Pattern.Leaf(type2), "y"), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern8 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Or",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern9 = new Pattern.Term("Or",
		new Pattern.Set(false, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "x")}),
		null);
	private final static Pattern.Term pattern10 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type0), "b"), 
			new Pair(new Pattern.Leaf(type2), "xs")}),
		null);
	private final static Pattern.Term pattern11 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Not",
				new Pattern.Leaf(type2),
				"x"),null), 
			new Pair(new Pattern.Leaf(type2), "y"), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern12 = new Pattern.Term("Or",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Or",
				new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type2), "xs")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "ys")}),
		null);
	private final static Pattern.Term pattern13 = new Pattern.Term("ForAll",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type3), "qs")}),null), 
			new Pair(new Pattern.Leaf(type2), "be")}),
		null);
	private final static Pattern.Term pattern14 = new Pattern.Term("Not",
		new Pattern.Term("ForAll",
			new Pattern.List(false, new Pair[]{
				new Pair(new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type3),null)}), "vars"), 
				new Pair(new Pattern.Leaf(type2), "be")}),
			null),
		null);
	private final static Pattern.Term pattern15 = new Pattern.Term("ForAll",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type3),null)}), "xs"), 
			new Pair(new Pattern.Term("ForAll",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Set(true, new Pair[]{
						new Pair(new Pattern.Leaf(type3),null)}), "ys"), 
					new Pair(new Pattern.Leaf(type2), "e")}),
				null),null)}),
		null);
	private final static Pattern.Term pattern16 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Leaf(type2), "e1"), 
			new Pair(new Pattern.Term("ForAll",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Set(true, new Pair[]{
						new Pair(new Pattern.Leaf(type3),null), 
						new Pair(new Pattern.Leaf(type3),null)}), "vs"), 
					new Pair(new Pattern.Leaf(type2), "e2")}),
				null), "qf"), 
			new Pair(new Pattern.Leaf(type2), "es")}),
		null);
	private final static Pattern.Term pattern17 = new Pattern.Term("Exists",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type3), "qs")}),null), 
			new Pair(new Pattern.Leaf(type2), "be")}),
		null);
	private final static Pattern.Term pattern18 = new Pattern.Term("Not",
		new Pattern.Term("Exists",
			new Pattern.List(false, new Pair[]{
				new Pair(new Pattern.Set(true, new Pair[]{
					new Pair(new Pattern.Leaf(type3),null)}), "vars"), 
				new Pair(new Pattern.Leaf(type2), "be")}),
			null),
		null);
	private final static Pattern.Term pattern19 = new Pattern.Term("Exists",
		new Pattern.List(false, new Pair[]{
			new Pair(new Pattern.Set(true, new Pair[]{
				new Pair(new Pattern.Leaf(type3),null)}), "xs"), 
			new Pair(new Pattern.Term("Exists",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Set(true, new Pair[]{
						new Pair(new Pattern.Leaf(type3),null)}), "ys"), 
					new Pair(new Pattern.Leaf(type2), "e")}),
				null),null)}),
		null);
	private final static Pattern.Term pattern20 = new Pattern.Term("And",
		new Pattern.Set(true, new Pair[]{
			new Pair(new Pattern.Term("Exists",
				new Pattern.List(false, new Pair[]{
					new Pair(new Pattern.Set(true, new Pair[]{
						new Pair(new Pattern.Leaf(type3),null)}), "vs"), 
					new Pair(new Pattern.Leaf(type2), "e")}),
				null),null), 
			new Pair(new Pattern.Leaf(type2), "es")}),
		null);
	// =========================================================================
	// rules
	// =========================================================================

	public static final InferenceRule[] inferences = new InferenceRule[]{
		new Inference_0(pattern16)
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
		new Reduction_16(pattern17),
		new Reduction_17(pattern18),
		new Reduction_18(pattern19),
		new Reduction_19(pattern20)
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
			Rewriter rw = new SimpleRewriter(inferences,reductions,SCHEMA);
			rw.apply(automaton);
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
