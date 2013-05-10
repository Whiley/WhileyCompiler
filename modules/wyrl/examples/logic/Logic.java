import java.io.*;
import java.util.*;
import java.math.BigInteger;
import wyautl.util.BigRational;
import wyautl.io.*;
import wyautl.core.*;
import wyrl.io.*;
import wyrl.core.*;
import wyrl.util.Runtime;
import static wyrl.util.Runtime.*;

public final class Logic {
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

	// term $3<Not($1<^BExpr>)>
	public final static int K_Not = 3;
	public final static int Not(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Not, r0));
	}

	// Not(Bool b)
	public static boolean reduce_0(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
	
		Automaton.Term r3 = True;
		Object r4 = (Object) automaton.get(r2);
		boolean r5 = r4.equals(r3);    // b eq True
		if(r5) {
			Automaton.Term r6 = False;
			int r7 = automaton.add(r6);
			if(r0 != r7) {
				automaton.rewrite(r0, r7);
				numReductions++;
				return true;
			}
		}
		Automaton.Term r8 = True;
		int r9 = automaton.add(r8);
		if(r0 != r9) {
			automaton.rewrite(r0, r9);
			numReductions++;
			return true;
		}
		return false;
	}

	// Not(Not(any x))
	public static boolean reduce_1(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
	
		if(r0 != r4) {
			automaton.rewrite(r0, r4);
			numReductions++;
			return true;
		}
		return false;
	}

	// Not(And({$4<BExpr> xs...}))
	public static boolean reduce_2(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
		Automaton.Set r5 = (Automaton.Set) automaton.get(r4);
		int j6 = 0;
		int[] t6 = new int[r5.size()-0];
		for(int i6=0;i6!=r5.size();++i6) {
			int r6 = r5.get(i6);
			if(!typeof_3(r6,automaton)) { continue; }
			
			t6[j6++] = r6;
		}
		Automaton.Set r7 = new Automaton.Set(t6);
	
		Automaton.List t8 = new Automaton.List();
		for(int i9=0;i9<r7.size();i9++) {
			int r9 = (int) r7.get(i9);
			Automaton.Term r10 = new Automaton.Term(K_Not,r9);
			int r11 = automaton.add(r10);
			t8.add(r11);
		}
		Automaton.Set r8 = new Automaton.Set(t8.toArray());
		int r12 = automaton.add(r8);
		Automaton.Term r13 = new Automaton.Term(K_Or,r12);
		int r14 = automaton.add(r13);
		if(r0 != r14) {
			automaton.rewrite(r0, r14);
			numReductions++;
			return true;
		}
		return false;
	}

	// Not(Or({$4<BExpr> xs...}))
	public static boolean reduce_4(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
		Automaton.Set r5 = (Automaton.Set) automaton.get(r4);
		int j6 = 0;
		int[] t6 = new int[r5.size()-0];
		for(int i6=0;i6!=r5.size();++i6) {
			int r6 = r5.get(i6);
			if(!typeof_3(r6,automaton)) { continue; }
			
			t6[j6++] = r6;
		}
		Automaton.Set r7 = new Automaton.Set(t6);
	
		Automaton.List t8 = new Automaton.List();
		for(int i9=0;i9<r7.size();i9++) {
			int r9 = (int) r7.get(i9);
			Automaton.Term r10 = new Automaton.Term(K_Not,r9);
			int r11 = automaton.add(r10);
			t8.add(r11);
		}
		Automaton.Set r8 = new Automaton.Set(t8.toArray());
		int r12 = automaton.add(r8);
		Automaton.Term r13 = new Automaton.Term(K_And,r12);
		int r14 = automaton.add(r13);
		if(r0 != r14) {
			automaton.rewrite(r0, r14);
			numReductions++;
			return true;
		}
		return false;
	}

	// term $6<And($4<^{$1<^BExpr>...}>)>
	public final static int K_And = 4;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// And({$4<BExpr> x})
	public static boolean reduce_5(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_3(r4,automaton)) { continue; }
			
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Bool b, $4<BExpr> xs...})
	public static boolean reduce_6(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_7(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_3(r5,automaton)) { continue; }
				
				t5[j5++] = r5;
			}
			Automaton.Set r6 = new Automaton.Set(t5);
	
			Automaton.Term r7 = False;
			Object r8 = (Object) automaton.get(r4);
			boolean r9 = r8.equals(r7);    // b eq False
			if(r9) {
				Automaton.Term r10 = False;
				int r11 = automaton.add(r10);
				if(r0 != r11) {
					automaton.rewrite(r0, r11);
					numReductions++;
					return true;
				}
			}
			Automaton.Int r12 = r6.lengthOf(); // |xs|
			Automaton.Int r13 = new Automaton.Int(0); // 0
			boolean r14 = r12.equals(r13); // |xs| eq 0
			if(r14) {
				Automaton.Term r15 = True;
				int r16 = automaton.add(r15);
				if(r0 != r16) {
					automaton.rewrite(r0, r16);
					numReductions++;
					return true;
				}
			}
			int r17 = automaton.add(r6);
			Automaton.Term r18 = new Automaton.Term(K_And,r17);
			int r19 = automaton.add(r18);
			if(r0 != r19) {
				automaton.rewrite(r0, r19);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({And({$4<BExpr> xs...}), $4<BExpr> ys...})
	public static boolean reduce_8(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_9(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_3(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_3(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Set r11 = new Automaton.Set(t10);
	
			Automaton.Set r12 = r9.append(r11); // xs append ys
			int r13 = automaton.add(r12);
			Automaton.Term r14 = new Automaton.Term(K_And,r13);
			int r15 = automaton.add(r14);
			if(r0 != r15) {
				automaton.rewrite(r0, r15);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Not($4<BExpr> x), $4<BExpr> y, $4<BExpr> ys...})
	public static boolean reduce_10(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_11(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_3(r7,automaton)) { continue; }
				
				int j8 = 0;
				int[] t8 = new int[r3.size()-2];
				for(int i8=0;i8!=r3.size();++i8) {
					int r8 = r3.get(i8);
					if(i8 == i4 || i8 == i7 || !typeof_3(r8,automaton)) { continue; }
					
					t8[j8++] = r8;
				}
				Automaton.Set r9 = new Automaton.Set(t8);
	
				boolean r10 = r6 == r7;        // x eq y
				if(r10) {
					Automaton.Term r11 = False;
					int r12 = automaton.add(r11);
					if(r0 != r12) {
						automaton.rewrite(r0, r12);
						numReductions++;
						return true;
					}
				}
			}
		}
		return false;
	}

	// And({Or({$4<BExpr> xs...}), $4<BExpr> ys...})
	public static boolean reduce_12(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_13(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_3(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_3(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Set r11 = new Automaton.Set(t10);
	
		Automaton.List t12 = new Automaton.List();
		for(int i13=0;i13<r9.size();i13++) {
			int r13 = (int) r9.get(i13);
			Automaton.Set r14 = r11.appendFront(r13); // x append ys
			int r15 = automaton.add(r14);
			Automaton.Term r16 = new Automaton.Term(K_And,r15);
			int r17 = automaton.add(r16);
			t12.add(r17);
		}
		Automaton.Set r12 = new Automaton.Set(t12.toArray());
			int r18 = automaton.add(r12);
			Automaton.Term r19 = new Automaton.Term(K_Or,r18);
			int r20 = automaton.add(r19);
			if(r0 != r20) {
				automaton.rewrite(r0, r20);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term $6<Or($4<^{$1<^BExpr>...}>)>
	public final static int K_Or = 5;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// Or({$4<BExpr> x})
	public static boolean reduce_14(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_3(r4,automaton)) { continue; }
			
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Or({Bool b, $4<BExpr> xs...})
	public static boolean reduce_15(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_7(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_3(r5,automaton)) { continue; }
				
				t5[j5++] = r5;
			}
			Automaton.Set r6 = new Automaton.Set(t5);
	
			Automaton.Term r7 = True;
			Object r8 = (Object) automaton.get(r4);
			boolean r9 = r8.equals(r7);    // b eq True
			if(r9) {
				Automaton.Term r10 = True;
				int r11 = automaton.add(r10);
				if(r0 != r11) {
					automaton.rewrite(r0, r11);
					numReductions++;
					return true;
				}
			}
			Automaton.Int r12 = r6.lengthOf(); // |xs|
			Automaton.Int r13 = new Automaton.Int(0); // 0
			boolean r14 = r12.equals(r13); // |xs| eq 0
			if(r14) {
				Automaton.Term r15 = False;
				int r16 = automaton.add(r15);
				if(r0 != r16) {
					automaton.rewrite(r0, r16);
					numReductions++;
					return true;
				}
			}
			int r17 = automaton.add(r6);
			Automaton.Term r18 = new Automaton.Term(K_Or,r17);
			int r19 = automaton.add(r18);
			if(r0 != r19) {
				automaton.rewrite(r0, r19);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Or({Not($4<BExpr> x), $4<BExpr> y, $4<BExpr> ys...})
	public static boolean reduce_16(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_11(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_3(r7,automaton)) { continue; }
				
				int j8 = 0;
				int[] t8 = new int[r3.size()-2];
				for(int i8=0;i8!=r3.size();++i8) {
					int r8 = r3.get(i8);
					if(i8 == i4 || i8 == i7 || !typeof_3(r8,automaton)) { continue; }
					
					t8[j8++] = r8;
				}
				Automaton.Set r9 = new Automaton.Set(t8);
	
				boolean r10 = r6 == r7;        // x eq y
				if(r10) {
					Automaton.Term r11 = True;
					int r12 = automaton.add(r11);
					if(r0 != r12) {
						automaton.rewrite(r0, r12);
						numReductions++;
						return true;
					}
				}
			}
		}
		return false;
	}

	// Or({Or({$4<BExpr> xs...}), $4<BExpr> ys...})
	public static boolean reduce_17(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_13(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_3(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_3(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Set r11 = new Automaton.Set(t10);
	
			Automaton.Set r12 = r9.append(r11); // xs append ys
			int r13 = automaton.add(r12);
			Automaton.Term r14 = new Automaton.Term(K_Or,r13);
			int r15 = automaton.add(r14);
			if(r0 != r15) {
				automaton.rewrite(r0, r15);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	public static boolean reduce(Automaton automaton) {
		boolean result = false;
		boolean changed = true;
		while(changed) {
			changed = false;
			for(int i=0;i<automaton.nStates();++i) {
				if(numSteps++ > MAX_STEPS) { return result; } // bail out
				if(automaton.get(i) == null) { continue; }
				
				if(typeof_0(i,automaton)) {
					changed |= reduce_0(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_1(i,automaton)) {
					changed |= reduce_1(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_2(i,automaton)) {
					changed |= reduce_2(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_4(i,automaton)) {
					changed |= reduce_4(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_5(i,automaton)) {
					changed |= reduce_5(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_6(i,automaton)) {
					changed |= reduce_6(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_8(i,automaton)) {
					changed |= reduce_8(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_10(i,automaton)) {
					changed |= reduce_10(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_12(i,automaton)) {
					changed |= reduce_12(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_14(i,automaton)) {
					changed |= reduce_14(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_15(i,automaton)) {
					changed |= reduce_15(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_16(i,automaton)) {
					changed |= reduce_16(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_17(i,automaton)) {
					changed |= reduce_17(i,automaton);
					if(changed) { break; } // reset
				}
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

	private final static BitSet visited = new BitSet();

	// ^Not(^Bool)
	private static boolean typeof_0(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_18(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 0);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_18(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^Not(^any))
	private static boolean typeof_1(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_19(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_19(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^$25<And($23<^{$18<^BExpr>...}>)>)
	private static boolean typeof_2(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_20(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 2);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_20(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $17<^BExpr>
	private static boolean typeof_3(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_21(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 3);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_21(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^$28<Or($24<^{$18<^BExpr>...}>)>)
	private static boolean typeof_4(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_22(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 4);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_22(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{$18<^BExpr>})
	private static boolean typeof_5(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_23(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 5);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_23(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Bool$19<^BExpr>...})
	private static boolean typeof_6(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_24(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 6);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_24(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Bool
	private static boolean typeof_7(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_25(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 7);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_25(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^And(^{$18<^BExpr>...})$18...})
	private static boolean typeof_8(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_26(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 8);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_26(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$25<And($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_9(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_27(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 9);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_27(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{$18<^BExpr>,^Not($18)$18...})
	private static boolean typeof_10(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_28(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 10);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_28(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$19<Not($17<^BExpr>)>
	private static boolean typeof_11(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_29(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 11);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_29(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Or(^{$19<^BExpr>...})$19...})
	private static boolean typeof_12(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_30(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 12);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_30(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$28<Or($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_13(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_31(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 13);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_31(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Or(^{$18<^BExpr>})
	private static boolean typeof_14(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_32(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 14);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_32(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Or(^{$18<^BExpr>})
	private static boolean typeof_32(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_33(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$18<^BExpr>}
	private static boolean typeof_33(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_34(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 33);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_34(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {$18<^BExpr>}
	private static boolean typeof_34(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_35(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_35(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_36(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 35);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_36(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<BExpr>
	private static boolean typeof_36(Automaton.State state, Automaton automaton) {
		return typeof_37(state,automaton);
	}

	// $2<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_37(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_38(state,automaton)
			|| typeof_39(state,automaton)
			|| typeof_31(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_38(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_35(data,automaton)) { return true; }
		}
		return false;
	}

	// $26<And($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_39(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_41(data,automaton)) { return true; }
		}
		return false;
	}

	// Var(^string)
	private static boolean typeof_40(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Var) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_42(data,automaton)) { return true; }
		}
		return false;
	}

	// ^string
	private static boolean typeof_42(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_43(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 42);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_43(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// string
	private static boolean typeof_43(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_STRING;
	}

	// $24<^{$18<^BExpr>...}>
	private static boolean typeof_41(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_44(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 41);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_44(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{$18<^BExpr>...}>
	private static boolean typeof_44(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_35(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^Or(^{^Bool$19<^BExpr>...})
	private static boolean typeof_15(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_45(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 15);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_45(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Or(^{^Bool$19<^BExpr>...})
	private static boolean typeof_45(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_46(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Bool$19<^BExpr>...}
	private static boolean typeof_46(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_47(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 46);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_47(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Bool$19<^BExpr>...}
	private static boolean typeof_47(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_7(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_48(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^Or(^{^Or(^{$18<^BExpr>...})$18...})
	private static boolean typeof_17(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_49(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 17);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_49(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Or(^{$18<^BExpr>,^Not($18)$18...})
	private static boolean typeof_16(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_50(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 16);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_50(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Or(^{$18<^BExpr>,^Not($18)$18...})
	private static boolean typeof_50(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_51(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$18<^BExpr>,^Not($18)$18...}
	private static boolean typeof_51(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_52(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 51);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_52(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Or(^{^Or(^{$18<^BExpr>...})$18...})
	private static boolean typeof_49(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_53(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^Not(^any))
	private static boolean typeof_19(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_54(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_48(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_55(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 48);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_55(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Not(^Bool)
	private static boolean typeof_18(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_7(data,automaton)) { return true; }
		}
		return false;
	}

	// $16<BExpr>
	private static boolean typeof_55(Automaton.State state, Automaton automaton) {
		return typeof_56(state,automaton);
	}

	// $4<BExpr>
	private static boolean typeof_21(Automaton.State state, Automaton automaton) {
		return typeof_57(state,automaton);
	}

	// ^Not(^any)
	private static boolean typeof_54(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_58(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 54);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_58(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Not(^$25<And($23<^{$18<^BExpr>...}>)>)
	private static boolean typeof_20(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_59(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Or(^{$18<^BExpr>...})$18...}
	private static boolean typeof_53(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_60(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 53);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_60(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// And(^{$18<^BExpr>})
	private static boolean typeof_23(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_61(data,automaton)) { return true; }
		}
		return false;
	}

	// {$18<^BExpr>,^Not($18)$18...}
	private static boolean typeof_52(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 2) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				for(int s1=0;s1 < state.size();++s1) {
				if(s1==s0) { continue; }
					boolean result=true;
					for(int i=0;i!=state.size();++i) {
						int child = state.get(i);
						if(i == s0) {
							if(!typeof_62(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_63(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_62(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// Not(^$28<Or($24<^{$18<^BExpr>...}>)>)
	private static boolean typeof_22(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_64(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$28<Or($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_64(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_65(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 64);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_65(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $28<Or($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_65(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_66(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{$18<^BExpr>...}>
	private static boolean typeof_66(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_67(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 66);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_67(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{$18<^BExpr>...}>
	private static boolean typeof_67(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_68(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_68(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_69(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 68);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_69(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<BExpr>
	private static boolean typeof_69(Automaton.State state, Automaton automaton) {
		return typeof_70(state,automaton);
	}

	// $3<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_70(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_71(state,automaton)
			|| typeof_72(state,automaton)
			|| typeof_65(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_71(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_68(data,automaton)) { return true; }
		}
		return false;
	}

	// $26<And($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_72(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_66(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$25<And($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_59(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_73(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 59);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_73(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $25<And($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_73(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_74(data,automaton)) { return true; }
		}
		return false;
	}

	// $23<^{$18<^BExpr>...}>
	private static boolean typeof_74(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_75(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 74);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_75(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $22<{$18<^BExpr>...}>
	private static boolean typeof_75(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_76(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_77(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 76);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_77(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<BExpr>
	private static boolean typeof_77(Automaton.State state, Automaton automaton) {
		return typeof_78(state,automaton);
	}

	// $3<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_78(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_79(state,automaton)
			|| typeof_73(state,automaton)
			|| typeof_80(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_79(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76(data,automaton)) { return true; }
		}
		return false;
	}

	// $28<Or($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_80(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_74(data,automaton)) { return true; }
		}
		return false;
	}

	// Bool
	private static boolean typeof_25(Automaton.State state, Automaton automaton) {
		return typeof_81(state,automaton);
	}

	// True|False
	private static boolean typeof_81(Automaton.State state, Automaton automaton) {
		return typeof_82(state,automaton)
			|| typeof_83(state,automaton);
	}

	// False
	private static boolean typeof_83(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_False) {
			return true;
		}
		return false;
	}

	// True
	private static boolean typeof_82(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_True) {
			return true;
		}
		return false;
	}

	// Not(^any)
	private static boolean typeof_58(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_84(data,automaton)) { return true; }
		}
		return false;
	}

	// ^any
	private static boolean typeof_84(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_85(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 84);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_85(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// any
	private static boolean typeof_85(Automaton.State state, Automaton automaton) {
		return true;
	}

	// And(^{^Bool$19<^BExpr>...})
	private static boolean typeof_24(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_86(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Bool$19<^BExpr>...}
	private static boolean typeof_86(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_87(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 86);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_87(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Bool$19<^BExpr>...}
	private static boolean typeof_87(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_7(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_88(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $1<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_57(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_89(state,automaton)
			|| typeof_90(state,automaton)
			|| typeof_91(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $25<And($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_27(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_92(data,automaton)) { return true; }
		}
		return false;
	}

	// $23<^{$18<^BExpr>...}>
	private static boolean typeof_92(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_93(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 92);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_93(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $22<{$18<^BExpr>...}>
	private static boolean typeof_93(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_94(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_94(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_95(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 94);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_95(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<BExpr>
	private static boolean typeof_95(Automaton.State state, Automaton automaton) {
		return typeof_96(state,automaton);
	}

	// $2<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_96(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_97(state,automaton)
			|| typeof_27(state,automaton)
			|| typeof_98(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $28<Or($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_98(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_92(data,automaton)) { return true; }
		}
		return false;
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_97(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_94(data,automaton)) { return true; }
		}
		return false;
	}

	// $13<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_56(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_99(state,automaton)
			|| typeof_100(state,automaton)
			|| typeof_101(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $26<And($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_100(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_102(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{$18<^BExpr>...}>
	private static boolean typeof_102(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_103(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 102);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_103(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{$18<^BExpr>...}>
	private static boolean typeof_103(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_48(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $28<Or($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_101(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_102(data,automaton)) { return true; }
		}
		return false;
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_99(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_48(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^And(^{$18<^BExpr>...})$18...})
	private static boolean typeof_26(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_104(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^And(^{$18<^BExpr>...})$18...}
	private static boolean typeof_104(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_105(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 104);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_105(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^And(^{$18<^BExpr>...})$18...}
	private static boolean typeof_105(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_9(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_94(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $19<Not($17<^BExpr>)>
	private static boolean typeof_89(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$20<Not($18<^BExpr>)>
	private static boolean typeof_63(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_106(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 63);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_106(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_106(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_62(data,automaton)) { return true; }
		}
		return false;
	}

	// $19<Not($17<^BExpr>)>
	private static boolean typeof_29(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_107(data,automaton)) { return true; }
		}
		return false;
	}

	// $17<^BExpr>
	private static boolean typeof_107(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_108(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 107);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_108(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<BExpr>
	private static boolean typeof_108(Automaton.State state, Automaton automaton) {
		return typeof_109(state,automaton);
	}

	// $2<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_109(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_29(state,automaton)
			|| typeof_110(state,automaton)
			|| typeof_111(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $25<And($23<^{$17<^BExpr>...}>)>
	private static boolean typeof_110(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_112(data,automaton)) { return true; }
		}
		return false;
	}

	// $28<Or($23<^{$17<^BExpr>...}>)>
	private static boolean typeof_111(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_112(data,automaton)) { return true; }
		}
		return false;
	}

	// $23<^{$17<^BExpr>...}>
	private static boolean typeof_112(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_113(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 112);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_113(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $22<{$17<^BExpr>...}>
	private static boolean typeof_113(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_107(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_88(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_114(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 88);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_114(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<BExpr>
	private static boolean typeof_114(Automaton.State state, Automaton automaton) {
		return typeof_115(state,automaton);
	}

	// $13<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_115(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_116(state,automaton)
			|| typeof_117(state,automaton)
			|| typeof_118(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $28<Or($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_118(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// $23<^{$18<^BExpr>...}>
	private static boolean typeof_119(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_120(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 119);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_120(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $25<And($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_117(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_116(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_88(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_62(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_121(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 62);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_121(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// And(^{$18<^BExpr>,^Not($18)$18...})
	private static boolean typeof_28(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_122(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$18<^BExpr>,^Not($18)$18...}
	private static boolean typeof_122(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_123(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 122);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_123(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {$18<^BExpr>,^Not($18)$18...}
	private static boolean typeof_123(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 2) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				for(int s1=0;s1 < state.size();++s1) {
				if(s1==s0) { continue; }
					boolean result=true;
					for(int i=0;i!=state.size();++i) {
						int child = state.get(i);
						if(i == s0) {
							if(!typeof_124(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_125(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_124(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^$20<Not($18<^BExpr>)>
	private static boolean typeof_125(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_126(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 125);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_126(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $20<Not($18<^BExpr>)>
	private static boolean typeof_126(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_124(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^BExpr>
	private static boolean typeof_124(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_127(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 124);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_127(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<BExpr>
	private static boolean typeof_127(Automaton.State state, Automaton automaton) {
		return typeof_128(state,automaton);
	}

	// $3<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_128(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_126(state,automaton)
			|| typeof_129(state,automaton)
			|| typeof_130(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $25<And($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_129(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_131(data,automaton)) { return true; }
		}
		return false;
	}

	// $23<^{$18<^BExpr>...}>
	private static boolean typeof_131(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_132(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 131);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_132(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $28<Or($23<^{$18<^BExpr>...}>)>
	private static boolean typeof_130(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_131(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<{$18<^BExpr>...}>
	private static boolean typeof_132(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_124(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $6<BExpr>
	private static boolean typeof_121(Automaton.State state, Automaton automaton) {
		return typeof_133(state,automaton);
	}

	// $3<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_133(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_106(state,automaton)
			|| typeof_134(state,automaton)
			|| typeof_135(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $28<Or($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_135(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_136(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{$18<^BExpr>...}>
	private static boolean typeof_136(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_137(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 136);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_137(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{$18<^BExpr>...}>
	private static boolean typeof_137(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_62(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $26<And($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_134(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_136(data,automaton)) { return true; }
		}
		return false;
	}

	// $28<Or($23<^{$17<^BExpr>...}>)>
	private static boolean typeof_91(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_138(data,automaton)) { return true; }
		}
		return false;
	}

	// $23<^{$17<^BExpr>...}>
	private static boolean typeof_138(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_139(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 138);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_139(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $22<{$17<^BExpr>...}>
	private static boolean typeof_139(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{$18<^BExpr>}
	private static boolean typeof_61(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_140(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 61);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_140(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {$18<^BExpr>}
	private static boolean typeof_140(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_94(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $28<Or($24<^{$18<^BExpr>...}>)>
	private static boolean typeof_31(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_41(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<{$18<^BExpr>...}>
	private static boolean typeof_120(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_88(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $25<And($23<^{$17<^BExpr>...}>)>
	private static boolean typeof_90(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_138(data,automaton)) { return true; }
		}
		return false;
	}

	// {^Or(^{$18<^BExpr>...})$18...}
	private static boolean typeof_60(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_13(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_35(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// And(^{^Or(^{$19<^BExpr>...})$19...})
	private static boolean typeof_30(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_141(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Or(^{$19<^BExpr>...})$19...}
	private static boolean typeof_141(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_142(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 141);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_142(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Or(^{$19<^BExpr>...})$19...}
	private static boolean typeof_142(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_143(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_144(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^$28<Or($24<^{$19<^BExpr>...}>)>
	private static boolean typeof_143(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_145(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 143);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_145(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $19<^BExpr>
	private static boolean typeof_144(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_146(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 144);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_146(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $28<Or($24<^{$19<^BExpr>...}>)>
	private static boolean typeof_145(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_147(data,automaton)) { return true; }
		}
		return false;
	}

	// $6<BExpr>
	private static boolean typeof_146(Automaton.State state, Automaton automaton) {
		return typeof_148(state,automaton);
	}

	// $24<^{$19<^BExpr>...}>
	private static boolean typeof_147(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_149(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 147);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_149(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $3<Bool|Not(^BExpr)|And(^{^BExpr...})|Or(^{^BExpr...})|Var(^string)>
	private static boolean typeof_148(Automaton.State state, Automaton automaton) {
		return typeof_25(state,automaton)
			|| typeof_150(state,automaton)
			|| typeof_151(state,automaton)
			|| typeof_145(state,automaton)
			|| typeof_40(state,automaton);
	}

	// $23<{$19<^BExpr>...}>
	private static boolean typeof_149(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_144(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $21<Not($19<^BExpr>)>
	private static boolean typeof_150(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_144(data,automaton)) { return true; }
		}
		return false;
	}

	// $26<And($24<^{$19<^BExpr>...}>)>
	private static boolean typeof_151(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_147(data,automaton)) { return true; }
		}
		return false;
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
		// $3<Not($1<^BExpr>)>
		Schema.Term("Not",Schema.Or(Schema.Any, Schema.Or(Schema.Term("True"), Schema.Term("False")), Schema.Term("And",Schema.Set(true)), Schema.Term("Or",Schema.Any), Schema.Term("Var",Schema.String))),
		// $6<And($4<^{$1<^BExpr>...}>)>
		Schema.Term("And",Schema.Set(true)),
		// $6<Or($4<^{$1<^BExpr>...}>)>
		Schema.Term("Or",Schema.Set(true))
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
