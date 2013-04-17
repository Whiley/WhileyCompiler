package wyone.core;

import java.io.*;
import java.util.*;
import java.math.BigInteger;
import wyautl.util.BigRational;
import wyautl.io.*;
import wyautl.core.*;
import wyone.io.*;
import wyone.core.*;
import wyone.util.Runtime;
import static wyone.util.Runtime.*;

public final class Types {
	// term $3<Not($1<^Type>)>
	public final static int K_Not = 0;
	public final static int Not(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Not, r0));
	}

	// Not(Any)
	public static boolean reduce_0(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
	
		Automaton.Term r3 = Void;
		int r4 = automaton.add(r3);
		if(r0 != r4) {
			automaton.rewrite(r0, r4);
			numReductions++;
			return true;
		}
		return false;
	}

	// Not(Void)
	public static boolean reduce_1(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
	
		Automaton.Term r3 = Any;
		int r4 = automaton.add(r3);
		if(r0 != r4) {
			automaton.rewrite(r0, r4);
			numReductions++;
			return true;
		}
		return false;
	}

	// Not(Or({$4<Type> es...}))
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
		Automaton.Term r13 = new Automaton.Term(K_And,r12);
		int r14 = automaton.add(r13);
		if(r0 != r14) {
			automaton.rewrite(r0, r14);
			numReductions++;
			return true;
		}
		return false;
	}

	// Not(And({$4<Type> es...}))
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
		Automaton.Term r13 = new Automaton.Term(K_Or,r12);
		int r14 = automaton.add(r13);
		if(r0 != r14) {
			automaton.rewrite(r0, r14);
			numReductions++;
			return true;
		}
		return false;
	}

	// term $6<And($4<^{$1<^Type>...}>)>
	public final static int K_And = 1;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// And({$4<Type> t})
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

	// And({And({$4<Type> xs...}), $4<Type> ys...})
	public static boolean reduce_6(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_7(r4,automaton)) { continue; }
			
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

	// And({Or({$4<Type> xs...}), $4<Type> ys...})
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

	// term $6<Or($4<^{$1<^Type>...}>)>
	public final static int K_Or = 2;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// Or({$4<Type> t})
	public static boolean reduce_10(int r0, Automaton automaton) {
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

	// Or({Or({$4<Type> xs...}), $4<Type> ys...})
	public static boolean reduce_11(int r0, Automaton automaton) {
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

	// And({Void, $4<Type> xs...})
	public static boolean reduce_12(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_13(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_3(r5,automaton)) { continue; }
				
				t5[j5++] = r5;
			}
			Automaton.Set r6 = new Automaton.Set(t5);
	
			Automaton.Term r7 = Void;
			int r8 = automaton.add(r7);
			if(r0 != r8) {
				automaton.rewrite(r0, r8);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Any, $4<Type> xs...})
	public static boolean reduce_14(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_15(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_3(r5,automaton)) { continue; }
				
				t5[j5++] = r5;
			}
			Automaton.Set r6 = new Automaton.Set(t5);
	
			int r7 = automaton.add(r6);
			Automaton.Term r8 = new Automaton.Term(K_And,r7);
			int r9 = automaton.add(r8);
			if(r0 != r9) {
				automaton.rewrite(r0, r9);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Proton a1, Proton a2, $4<Type> ts...})
	public static boolean reduce_16(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_17(r4,automaton)) { continue; }
			
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_17(r5,automaton)) { continue; }
				
				int j6 = 0;
				int[] t6 = new int[r3.size()-2];
				for(int i6=0;i6!=r3.size();++i6) {
					int r6 = r3.get(i6);
					if(i6 == i4 || i6 == i5 || !typeof_3(r6,automaton)) { continue; }
					
					t6[j6++] = r6;
				}
				Automaton.Set r7 = new Automaton.Set(t6);
	
				boolean r8 = r4 != r5;         // a1 neq a2
				boolean r9 = false;            // a1 neq a2 && !a2 is ^Any
				if(r8) {
					boolean r10 = typeof_15(r5,automaton); // a2 is ^Any
					boolean r11 = !r10;            // !a2 is ^Any
					r9 = r11;
				}
				if(r9) {
					Automaton.Term r12 = Void;
					int r13 = automaton.add(r12);
					if(r0 != r13) {
						automaton.rewrite(r0, r13);
						numReductions++;
						return true;
					}
				}
			}
		}
		return false;
	}

	// And({Proton a1, Not(Proton a2), $4<Type> ts...})
	public static boolean reduce_18(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_17(r4,automaton)) { continue; }
			
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_19(r5,automaton)) { continue; }
				
				Automaton.Term r6 = (Automaton.Term) automaton.get(r5);
				int r7 = r6.contents;
				int j8 = 0;
				int[] t8 = new int[r3.size()-2];
				for(int i8=0;i8!=r3.size();++i8) {
					int r8 = r3.get(i8);
					if(i8 == i4 || i8 == i5 || !typeof_3(r8,automaton)) { continue; }
					
					t8[j8++] = r8;
				}
				Automaton.Set r9 = new Automaton.Set(t8);
	
				boolean r10 = r4 == r7;        // a1 eq a2
				if(r10) {
					Automaton.Term r11 = Void;
					int r12 = automaton.add(r11);
					if(r0 != r12) {
						automaton.rewrite(r0, r12);
						numReductions++;
						return true;
					}
				}
				Automaton.Term r13 = Any;
				Object r14 = (Object) automaton.get(r7);
				boolean r15 = !r14.equals(r13); // a2 neq Any
				if(r15) {
					Automaton.Set r16 = r9.appendFront(r4); // a1 append ts
					int r17 = automaton.add(r16);
					Automaton.Term r18 = new Automaton.Term(K_And,r17);
					int r19 = automaton.add(r18);
					if(r0 != r19) {
						automaton.rewrite(r0, r19);
						numReductions++;
						return true;
					}
				}
			}
		}
		return false;
	}

	// Or({Any, $4<Type> xs...})
	public static boolean reduce_20(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_15(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_3(r5,automaton)) { continue; }
				
				t5[j5++] = r5;
			}
			Automaton.Set r6 = new Automaton.Set(t5);
	
			Automaton.Term r7 = Any;
			int r8 = automaton.add(r7);
			if(r0 != r8) {
				automaton.rewrite(r0, r8);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Or({Void, $4<Type> xs...})
	public static boolean reduce_21(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_13(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_3(r5,automaton)) { continue; }
				
				t5[j5++] = r5;
			}
			Automaton.Set r6 = new Automaton.Set(t5);
	
			int r7 = automaton.add(r6);
			Automaton.Term r8 = new Automaton.Term(K_Or,r7);
			int r9 = automaton.add(r8);
			if(r0 != r9) {
				automaton.rewrite(r0, r9);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term $3<Ref($1<^Type>)>
	public final static int K_Ref = 9;
	public final static int Ref(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Ref, r0));
	}

	// Ref(Void)
	public static boolean reduce_22(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
	
		Automaton.Term r3 = Void;
		int r4 = automaton.add(r3);
		if(r0 != r4) {
			automaton.rewrite(r0, r4);
			numReductions++;
			return true;
		}
		return false;
	}

	// And({Ref($4<Type> t1), Ref($4<Type> t2), $4<Type> ts...})
	public static boolean reduce_23(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_24(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_24(r7,automaton)) { continue; }
				
				Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
				int r9 = r8.contents;
				int j10 = 0;
				int[] t10 = new int[r3.size()-2];
				for(int i10=0;i10!=r3.size();++i10) {
					int r10 = r3.get(i10);
					if(i10 == i4 || i10 == i7 || !typeof_3(r10,automaton)) { continue; }
					
					t10[j10++] = r10;
				}
				Automaton.Set r11 = new Automaton.Set(t10);
	
				Automaton.Set r12 = new Automaton.Set(r6, r9); // {t1t2}
				int r13 = automaton.add(r12);
				Automaton.Term r14 = new Automaton.Term(K_And,r13);
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_Ref,r15);
				int r17 = automaton.add(r16);
				Automaton.Set r18 = r11.appendFront(r17); // Ref(And({t1t2})) append ts
				int r19 = automaton.add(r18);
				Automaton.Term r20 = new Automaton.Term(K_And,r19);
				int r21 = automaton.add(r20);
				if(r0 != r21) {
					automaton.rewrite(r0, r21);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// Or({Ref(Any) t, Ref($4<Type>), $4<Type> ts...})
	public static boolean reduce_25(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_26(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_24(r7,automaton)) { continue; }
				
				Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
				int r9 = r8.contents;
				int j10 = 0;
				int[] t10 = new int[r3.size()-2];
				for(int i10=0;i10!=r3.size();++i10) {
					int r10 = r3.get(i10);
					if(i10 == i4 || i10 == i7 || !typeof_3(r10,automaton)) { continue; }
					
					t10[j10++] = r10;
				}
				Automaton.Set r11 = new Automaton.Set(t10);
	
				Automaton.Set r12 = r11.appendFront(r4); // t append ts
				int r13 = automaton.add(r12);
				Automaton.Term r14 = new Automaton.Term(K_Or,r13);
				int r15 = automaton.add(r14);
				if(r0 != r15) {
					automaton.rewrite(r0, r15);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// And({Ref($4<Type> t1), Not(Ref($4<Type> t2)), $4<Type> ts...})
	public static boolean reduce_27(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_24(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_28(r7,automaton)) { continue; }
				
				Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
				int r9 = r8.contents;
				Automaton.Term r10 = (Automaton.Term) automaton.get(r9);
				int r11 = r10.contents;
				int j12 = 0;
				int[] t12 = new int[r3.size()-2];
				for(int i12=0;i12!=r3.size();++i12) {
					int r12 = r3.get(i12);
					if(i12 == i4 || i12 == i7 || !typeof_3(r12,automaton)) { continue; }
					
					t12[j12++] = r12;
				}
				Automaton.Set r13 = new Automaton.Set(t12);
	
				Automaton.Term r14 = new Automaton.Term(K_Not,r11);
				int r15 = automaton.add(r14);
				Automaton.Set r16 = new Automaton.Set(r6, r15); // {t1Not(t2)}
				int r17 = automaton.add(r16);
				Automaton.Term r18 = new Automaton.Term(K_And,r17);
				int r19 = automaton.add(r18);
				Automaton.Term r20 = new Automaton.Term(K_Ref,r19);
				int r21 = automaton.add(r20);
				Automaton.Set r22 = r13.appendFront(r21); // Ref(And({t1Not(t2)})) append ts
				int r23 = automaton.add(r22);
				Automaton.Term r24 = new Automaton.Term(K_And,r23);
				int r25 = automaton.add(r24);
				if(r0 != r25) {
					automaton.rewrite(r0, r25);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// term $3<Meta($1<^Type>)>
	public final static int K_Meta = 10;
	public final static int Meta(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Meta, r0));
	}

	// Meta(Void)
	public static boolean reduce_29(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
	
		Automaton.Term r3 = Void;
		int r4 = automaton.add(r3);
		if(r0 != r4) {
			automaton.rewrite(r0, r4);
			numReductions++;
			return true;
		}
		return false;
	}

	// And({Meta($4<Type> t1), Meta($4<Type> t2), $4<Type> ts...})
	public static boolean reduce_30(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_31(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_31(r7,automaton)) { continue; }
				
				Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
				int r9 = r8.contents;
				int j10 = 0;
				int[] t10 = new int[r3.size()-2];
				for(int i10=0;i10!=r3.size();++i10) {
					int r10 = r3.get(i10);
					if(i10 == i4 || i10 == i7 || !typeof_3(r10,automaton)) { continue; }
					
					t10[j10++] = r10;
				}
				Automaton.Set r11 = new Automaton.Set(t10);
	
				Automaton.Set r12 = new Automaton.Set(r6, r9); // {t1t2}
				int r13 = automaton.add(r12);
				Automaton.Term r14 = new Automaton.Term(K_And,r13);
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_Meta,r15);
				int r17 = automaton.add(r16);
				Automaton.Set r18 = r11.appendFront(r17); // Meta(And({t1t2})) append ts
				int r19 = automaton.add(r18);
				Automaton.Term r20 = new Automaton.Term(K_And,r19);
				int r21 = automaton.add(r20);
				if(r0 != r21) {
					automaton.rewrite(r0, r21);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// Or({Meta(Any) t, Meta($4<Type>), $4<Type> ts...})
	public static boolean reduce_32(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_33(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_31(r7,automaton)) { continue; }
				
				Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
				int r9 = r8.contents;
				int j10 = 0;
				int[] t10 = new int[r3.size()-2];
				for(int i10=0;i10!=r3.size();++i10) {
					int r10 = r3.get(i10);
					if(i10 == i4 || i10 == i7 || !typeof_3(r10,automaton)) { continue; }
					
					t10[j10++] = r10;
				}
				Automaton.Set r11 = new Automaton.Set(t10);
	
				Automaton.Set r12 = r11.appendFront(r4); // t append ts
				int r13 = automaton.add(r12);
				Automaton.Term r14 = new Automaton.Term(K_Or,r13);
				int r15 = automaton.add(r14);
				if(r0 != r15) {
					automaton.rewrite(r0, r15);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// And({Meta($4<Type> t1), Not(Meta($4<Type> t2)), $4<Type> ts...})
	public static boolean reduce_34(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_31(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_35(r7,automaton)) { continue; }
				
				Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
				int r9 = r8.contents;
				Automaton.Term r10 = (Automaton.Term) automaton.get(r9);
				int r11 = r10.contents;
				int j12 = 0;
				int[] t12 = new int[r3.size()-2];
				for(int i12=0;i12!=r3.size();++i12) {
					int r12 = r3.get(i12);
					if(i12 == i4 || i12 == i7 || !typeof_3(r12,automaton)) { continue; }
					
					t12[j12++] = r12;
				}
				Automaton.Set r13 = new Automaton.Set(t12);
	
				Automaton.Term r14 = new Automaton.Term(K_Not,r11);
				int r15 = automaton.add(r14);
				Automaton.Set r16 = new Automaton.Set(r6, r15); // {t1Not(t2)}
				int r17 = automaton.add(r16);
				Automaton.Term r18 = new Automaton.Term(K_And,r17);
				int r19 = automaton.add(r18);
				Automaton.Term r20 = new Automaton.Term(K_Meta,r19);
				int r21 = automaton.add(r20);
				Automaton.Set r22 = r13.appendFront(r21); // Meta(And({t1Not(t2)})) append ts
				int r23 = automaton.add(r22);
				Automaton.Term r24 = new Automaton.Term(K_And,r23);
				int r25 = automaton.add(r24);
				if(r0 != r25) {
					automaton.rewrite(r0, r25);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// term $8<Term(^[^string$2<^Type>...])>
	public final static int K_Term = 11;
	public final static int Term(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}
	public final static int Term(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}

	// And({Term([string s1, $4<Type> t1]), Term([string s2, $4<Type> t2]), $4<Type> ts...})
	public static boolean reduce_36(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_37(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.List r7 = (Automaton.List) automaton.get(r6);
			int r8 = r7.get(0);
			int r9 = r7.get(1);
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_37(r10,automaton)) { continue; }
				
				Automaton.Term r11 = (Automaton.Term) automaton.get(r10);
				int r12 = r11.contents;
				Automaton.List r13 = (Automaton.List) automaton.get(r12);
				int r14 = r13.get(0);
				int r15 = r13.get(1);
				int j16 = 0;
				int[] t16 = new int[r3.size()-2];
				for(int i16=0;i16!=r3.size();++i16) {
					int r16 = r3.get(i16);
					if(i16 == i4 || i16 == i10 || !typeof_3(r16,automaton)) { continue; }
					
					t16[j16++] = r16;
				}
				Automaton.Set r17 = new Automaton.Set(t16);
	
				boolean r18 = r8 == r14;       // s1 eq s2
				if(r18) {
					Automaton.Set r19 = new Automaton.Set(r9, r15); // {t1t2}
					int r20 = automaton.add(r19);
					Automaton.Term r21 = new Automaton.Term(K_And,r20);
					int r22 = automaton.add(r21);
					Automaton.List r23 = new Automaton.List(r8, r22); // [s1And({t1t2})]
					int r24 = automaton.add(r23);
					Automaton.Term r25 = new Automaton.Term(K_Term,r24);
					int r26 = automaton.add(r25);
					Automaton.Set r27 = r17.appendFront(r26); // Term([s1And({t1t2})]) append ts
					int r28 = automaton.add(r27);
					Automaton.Term r29 = new Automaton.Term(K_And,r28);
					int r30 = automaton.add(r29);
					if(r0 != r30) {
						automaton.rewrite(r0, r30);
						numReductions++;
						return true;
					}
				}
				Automaton.Term r31 = Void;
				int r32 = automaton.add(r31);
				if(r0 != r32) {
					automaton.rewrite(r0, r32);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// term $8<Nominal(^[^string,$2<^Type>])>
	public final static int K_Nominal = 12;
	public final static int Nominal(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}
	public final static int Nominal(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}

	// Nominal([string, $4<Type> t])
	public static boolean reduce_38(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
	
		if(r0 != r5) {
			automaton.rewrite(r0, r5);
			numReductions++;
			return true;
		}
		return false;
	}

	// term Fun(^[$1<^Type>,$1])
	public final static int K_Fun = 13;
	public final static int Fun(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}
	public final static int Fun(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}

	// term $10<Set($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	public final static int K_Set = 14;
	public final static int Set(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}
	public final static int Set(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}

	// Set([$4<Type> t, {|Void, $4<Type> ts...|}])
	public static boolean reduce_39(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_13(r7,automaton)) { continue; }
			
			int j8 = 0;
			int[] t8 = new int[r6.size()-1];
			for(int i8=0;i8!=r6.size();++i8) {
				int r8 = r6.get(i8);
				if(i8 == i7 || !typeof_3(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Bag r9 = new Automaton.Bag(t8);
	
			int r10 = automaton.add(r9);
			Automaton.List r11 = new Automaton.List(r4, r10); // [tts]
			int r12 = automaton.add(r11);
			Automaton.Term r13 = new Automaton.Term(K_Set,r12);
			int r14 = automaton.add(r13);
			if(r0 != r14) {
				automaton.rewrite(r0, r14);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term $10<Bag($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	public final static int K_Bag = 15;
	public final static int Bag(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}
	public final static int Bag(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}

	// Bag([$4<Type> t, {|Void, $4<Type> ts...|}])
	public static boolean reduce_40(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_13(r7,automaton)) { continue; }
			
			int j8 = 0;
			int[] t8 = new int[r6.size()-1];
			for(int i8=0;i8!=r6.size();++i8) {
				int r8 = r6.get(i8);
				if(i8 == i7 || !typeof_3(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Bag r9 = new Automaton.Bag(t8);
	
			int r10 = automaton.add(r9);
			Automaton.List r11 = new Automaton.List(r4, r10); // [tts]
			int r12 = automaton.add(r11);
			Automaton.Term r13 = new Automaton.Term(K_Bag,r12);
			int r14 = automaton.add(r13);
			if(r0 != r14) {
				automaton.rewrite(r0, r14);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term $10<List(^[$1<^Type>,^[$1...]])>
	public final static int K_List = 16;
	public final static int List(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}
	public final static int List(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}

	// List([$4<Type> t, [$4<Type> t1s...]])
	public static boolean reduce_41(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.List r6 = (Automaton.List) automaton.get(r5);
		Automaton.List r7 = r6.sublist(0);
	
		Automaton.Term r8 = Void;
		int r9 = automaton.add(r8);
		boolean r10 = r7.contains(r9); // Void in t1s
		if(r10) {
			Automaton.Term r11 = Void;
			int r12 = automaton.add(r11);
			if(r0 != r12) {
				automaton.rewrite(r0, r12);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({$10<List(^[$1<^Type>,^[$1...]])> l, $4<Type> t, $4<Type> ts...})
	public static boolean reduce_42(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_43(r4,automaton)) { continue; }
			
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_3(r5,automaton)) { continue; }
				
				int j6 = 0;
				int[] t6 = new int[r3.size()-2];
				for(int i6=0;i6!=r3.size();++i6) {
					int r6 = r3.get(i6);
					if(i6 == i4 || i6 == i5 || !typeof_3(r6,automaton)) { continue; }
					
					t6[j6++] = r6;
				}
				Automaton.Set r7 = new Automaton.Set(t6);
	
				boolean r8 = typeof_44(r5,automaton); // t is ^Proton
				boolean r9 = typeof_45(r5,automaton); // t is ^SetOrBag
				boolean r10 = r8 || r9;        // t is ^Proton || t is ^SetOrBag
				if(r10) {
					Automaton.Term r11 = Void;
					int r12 = automaton.add(r11);
					if(r0 != r12) {
						automaton.rewrite(r0, r12);
						numReductions++;
						return true;
					}
				}
				boolean r13 = typeof_46(r5,automaton); // t is ^Not(^Proton)
				boolean r14 = typeof_47(r5,automaton); // t is ^Not(^SetOrBag)
				boolean r15 = r13 || r14;      // t is ^Not(^Proton) || t is ^Not(^SetOrBag)
				if(r15) {
					Automaton.Set r16 = r7.appendFront(r4); // l append ts
					int r17 = automaton.add(r16);
					Automaton.Term r18 = new Automaton.Term(K_And,r17);
					int r19 = automaton.add(r18);
					if(r0 != r19) {
						automaton.rewrite(r0, r19);
						numReductions++;
						return true;
					}
				}
			}
		}
		return false;
	}

	// And({List([bool ub1, [$4<Type> t1s...]]), List([bool ub2, [$4<Type> t2s...]]), $4<Type> ts...})
	public static boolean reduce_48(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_49(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.List r7 = (Automaton.List) automaton.get(r6);
			int r8 = r7.get(0);
			int r9 = r7.get(1);
			Automaton.List r10 = (Automaton.List) automaton.get(r9);
			Automaton.List r11 = r10.sublist(0);
			for(int i12=0;i12!=r3.size();++i12) {
				int r12 = r3.get(i12);
				if(i12 == i4 || !typeof_49(r12,automaton)) { continue; }
				
				Automaton.Term r13 = (Automaton.Term) automaton.get(r12);
				int r14 = r13.contents;
				Automaton.List r15 = (Automaton.List) automaton.get(r14);
				int r16 = r15.get(0);
				int r17 = r15.get(1);
				Automaton.List r18 = (Automaton.List) automaton.get(r17);
				Automaton.List r19 = r18.sublist(0);
				int j20 = 0;
				int[] t20 = new int[r3.size()-2];
				for(int i20=0;i20!=r3.size();++i20) {
					int r20 = r3.get(i20);
					if(i20 == i4 || i20 == i12 || !typeof_3(r20,automaton)) { continue; }
					
					t20[j20++] = r20;
				}
				Automaton.Set r21 = new Automaton.Set(t20);
	
				boolean r22 = ((Automaton.Bool)automaton.get(r8)).value;
				boolean r23 = ((Automaton.Bool)automaton.get(r16)).value;
				boolean r24 = r22 || r23;      // ub1 || ub2
				boolean r25 = !r24;            // !ub1 || ub2
				boolean r26 = false;           // !ub1 || ub2 && |t1s| neq |t2s|
				if(r25) {
					Automaton.Int r27 = r11.lengthOf(); // |t1s|
					Automaton.Int r28 = r19.lengthOf(); // |t2s|
					boolean r29 = !r27.equals(r28); // |t1s| neq |t2s|
					r26 = r29;
				}
				if(r26) {
					Automaton.Term r30 = Void;
					int r31 = automaton.add(r30);
					if(r0 != r31) {
						automaton.rewrite(r0, r31);
						numReductions++;
						return true;
					}
				}
				boolean r32 = ((Automaton.Bool)automaton.get(r8)).value;
				boolean r33 = false;           // ub1 && !ub2 && |t1s| gt |t2s| add 1
				if(r32) {
					boolean r34 = ((Automaton.Bool)automaton.get(r16)).value;
					boolean r35 = !r34;            // !ub2
					boolean r36 = false;           // !ub2 && |t1s| gt |t2s| add 1
					if(r35) {
						Automaton.Int r37 = r11.lengthOf(); // |t1s|
						Automaton.Int r38 = r19.lengthOf(); // |t2s|
						Automaton.Int r39 = new Automaton.Int(1); // 1
						Automaton.Int r40 = r38.add(r39); // |t2s| add 1
						boolean r41 = r37.compareTo(r40)>0; // |t1s| gt |t2s| add 1
						r36 = r41;
					}
					r33 = r36;
				}
				if(r33) {
					Automaton.Term r42 = Void;
					int r43 = automaton.add(r42);
					if(r0 != r43) {
						automaton.rewrite(r0, r43);
						numReductions++;
						return true;
					}
				}
				boolean r44 = ((Automaton.Bool)automaton.get(r8)).value;
				boolean r45 = !r44;            // !ub1
				boolean r46 = false;           // !ub1 && ub2 && |t2s| gt |t1s| add 1
				if(r45) {
					boolean r47 = ((Automaton.Bool)automaton.get(r16)).value;
					boolean r48 = false;           // ub2 && |t2s| gt |t1s| add 1
					if(r47) {
						Automaton.Int r49 = r19.lengthOf(); // |t2s|
						Automaton.Int r50 = r11.lengthOf(); // |t1s|
						Automaton.Int r51 = new Automaton.Int(1); // 1
						Automaton.Int r52 = r50.add(r51); // |t1s| add 1
						boolean r53 = r49.compareTo(r52)>0; // |t2s| gt |t1s| add 1
						r48 = r53;
					}
					r46 = r48;
				}
				if(r46) {
					Automaton.Term r54 = Void;
					int r55 = automaton.add(r54);
					if(r0 != r55) {
						automaton.rewrite(r0, r55);
						numReductions++;
						return true;
					}
				}
		Automaton.Int r57 = new Automaton.Int(0); // 0
		Automaton.Int r58 = r11.lengthOf(); // |t1s|
		Automaton.List r59 = Runtime.rangeOf(automaton,r57,r58); // 0 range |t1s|
		Automaton.List t56 = new Automaton.List();
		for(int i60=0;i60<r59.size();i60++) {
			Automaton.Int r60 = (Automaton.Int) automaton.get(r59.get(i60));;
			int r61 = r11.indexOf(r60);    // t1s[i]
			int r62 = r19.indexOf(r60);    // t2s[i]
			Automaton.Set r63 = new Automaton.Set(r61, r62); // {t1s[i]t2s[i]}
			int r64 = automaton.add(r63);
			Automaton.Term r65 = new Automaton.Term(K_And,r64);
			int r66 = automaton.add(r65);
			t56.add(r66);
		}
		Automaton.List r56 = t56;
				boolean r67 = ((Automaton.Bool)automaton.get(r8)).value;
				boolean r68 = ((Automaton.Bool)automaton.get(r16)).value;
				boolean r69 = r67 || r68;      // ub1 || ub2
				boolean r70 = !r69;            // !ub1 || ub2
				if(r70) {
					boolean r71 = false;           // false
					int r72 = automaton.add(r71 ? Automaton.TRUE : Automaton.FALSE);
					int r73 = automaton.add(r56);
					Automaton.List r74 = new Automaton.List(r72, r73); // [falset3s]
					int r75 = automaton.add(r74);
					Automaton.Term r76 = new Automaton.Term(K_List,r75);
					int r77 = automaton.add(r76);
					Automaton.Set r78 = r21.appendFront(r77); // List([falset3s]) append ts
					int r79 = automaton.add(r78);
					Automaton.Term r80 = new Automaton.Term(K_And,r79);
					int r81 = automaton.add(r80);
					if(r0 != r81) {
						automaton.rewrite(r0, r81);
						numReductions++;
						return true;
					}
				}
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
				
				if(typeof_11(i,automaton)) {
					changed |= reduce_11(i,automaton);
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
				
				if(typeof_16(i,automaton)) {
					changed |= reduce_16(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_18(i,automaton)) {
					changed |= reduce_18(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_20(i,automaton)) {
					changed |= reduce_20(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_21(i,automaton)) {
					changed |= reduce_21(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_22(i,automaton)) {
					changed |= reduce_22(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_23(i,automaton)) {
					changed |= reduce_23(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_25(i,automaton)) {
					changed |= reduce_25(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_27(i,automaton)) {
					changed |= reduce_27(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_29(i,automaton)) {
					changed |= reduce_29(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_30(i,automaton)) {
					changed |= reduce_30(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_32(i,automaton)) {
					changed |= reduce_32(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_34(i,automaton)) {
					changed |= reduce_34(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_36(i,automaton)) {
					changed |= reduce_36(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_38(i,automaton)) {
					changed |= reduce_38(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_39(i,automaton)) {
					changed |= reduce_39(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_40(i,automaton)) {
					changed |= reduce_40(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_41(i,automaton)) {
					changed |= reduce_41(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_42(i,automaton)) {
					changed |= reduce_42(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_48(i,automaton)) {
					changed |= reduce_48(i,automaton);
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

	// ^Not(^Any)
	private static boolean typeof_0(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_50(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 0);
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

	// ^Not(^Void)
	private static boolean typeof_1(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_51(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_51(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^$43<Or($41<^{$38<^Type>...}>)>)
	private static boolean typeof_2(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_52(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 2);
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

	// $38<^Type>
	private static boolean typeof_3(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_53(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 3);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_53(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^$46<And($42<^{$39<^Type>...}>)>)
	private static boolean typeof_4(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_54(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 4);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_54(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{$39<^Type>})
	private static boolean typeof_5(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_55(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 5);
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

	// ^And(^{^And(^{$39<^Type>...})$39...})
	private static boolean typeof_6(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_56(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 6);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_56(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_7(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_57(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 7);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_57(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Or(^{$39<^Type>...})$39...})
	private static boolean typeof_8(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_58(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 8);
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

	// ^$43<Or($41<^{$38<^Type>...}>)>
	private static boolean typeof_9(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_59(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 9);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_59(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Or(^{$38<^Type>})
	private static boolean typeof_10(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_60(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 10);
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

	// ^Or(^{^Or(^{$38<^Type>...})$38...})
	private static boolean typeof_11(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_61(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 11);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_61(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Void$40<^Type>...})
	private static boolean typeof_12(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_62(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 12);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_62(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Void
	private static boolean typeof_13(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_63(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 13);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_63(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Any$40<^Type>...})
	private static boolean typeof_14(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_64(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 14);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_64(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Any
	private static boolean typeof_15(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_65(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 15);
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

	// ^Proton
	private static boolean typeof_17(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_66(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 17);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_66(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Proton,^Proton$39<^Type>...})
	private static boolean typeof_16(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_67(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 16);
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

	// ^Not(^Proton)
	private static boolean typeof_19(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_68(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 19);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_68(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Proton,^Not(^Proton)$40<^Type>...})
	private static boolean typeof_18(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_69(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 18);
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

	// ^Or(^{^Void$39<^Type>...})
	private static boolean typeof_21(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_70(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 21);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_70(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Or(^{^Any$39<^Type>...})
	private static boolean typeof_20(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_71(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 20);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_71(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Ref($40<^Type>),^Ref($40)$40...})
	private static boolean typeof_23(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_72(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 23);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_72(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Ref(^Void)
	private static boolean typeof_22(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_73(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 22);
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

	// ^Or(^{^Ref(^Any),^Ref($43<^Type>)$43...})
	private static boolean typeof_25(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_74(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 25);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_74(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$51<Ref($39<^Type>)>
	private static boolean typeof_24(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_75(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 24);
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

	// ^And(^{^Ref($40<^Type>),^Not(^Ref($40))$40...})
	private static boolean typeof_27(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_76(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 27);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_76(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Ref(^Any)
	private static boolean typeof_26(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_77(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 26);
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

	// ^Meta(^Void)
	private static boolean typeof_29(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_78(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 29);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_78(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^$51<Ref($39<^Type>)>)
	private static boolean typeof_28(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_79(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 28);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_79(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$54<Meta($39<^Type>)>
	private static boolean typeof_31(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_80(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 31);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_80(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Meta($40<^Type>),^Meta($40)$40...})
	private static boolean typeof_30(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_81(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 30);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_81(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Meta($40<^Type>),^Not(^Meta($40))$40...})
	private static boolean typeof_34(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_82(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 34);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_82(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^$54<Meta($39<^Type>)>)
	private static boolean typeof_35(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_83(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 35);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_83(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Or(^{^Meta(^Any),^Meta($43<^Type>)$43...})
	private static boolean typeof_32(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_84(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 32);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_84(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Meta(^Any)
	private static boolean typeof_33(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_85(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 33);
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

	// ^$62<Nominal(^[^string,$40<^Type>])>
	private static boolean typeof_38(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_86(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 38);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_86(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Set(^[$39<^Type>,^{|^Void$39...|}[^Void$39...]])
	private static boolean typeof_39(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_87(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 39);
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

	// ^And(^{^Term(^[^string,$41<^Type>]),^Term(^[^string,$41])$41...})
	private static boolean typeof_36(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_88(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 36);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_88(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Term($61<^[^string,$40<^Type>]>)
	private static boolean typeof_37(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_89(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 37);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_89(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{$2<^Type>,^List(^[$2,^[$2...]])$2...})
	private static boolean typeof_42(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_90(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 42);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_90(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$10<List(^[$1<^Type>,^[$1...]])>
	private static boolean typeof_43(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_91(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 43);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_91(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Bag(^[$39<^Type>,^{|^Void$39...|}[^Void$39...]])
	private static boolean typeof_40(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_92(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 40);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_92(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_41(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_93(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 41);
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

	// ^Not(^Proton)
	private static boolean typeof_46(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_94(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 46);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_94(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^SetOrBag)
	private static boolean typeof_47(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_95(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 47);
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

	// ^Proton
	private static boolean typeof_44(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_66(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 44);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_66(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^SetOrBag
	private static boolean typeof_45(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_96(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 45);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_96(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Not(^Void)
	private static boolean typeof_51(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_13(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^Any)
	private static boolean typeof_50(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_15(data,automaton)) { return true; }
		}
		return false;
	}

	// ^List(^[^bool,$86<^[$40<^Type>...]>])
	private static boolean typeof_49(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_97(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 49);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_97(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^List(^[^bool,^[$41<^Type>...]]),^List(^[^bool,^[$41...]])$41...})
	private static boolean typeof_48(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_98(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 48);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_98(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// And(^{$39<^Type>})
	private static boolean typeof_55(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_99(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^$46<And($42<^{$39<^Type>...}>)>)
	private static boolean typeof_54(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_100(data,automaton)) { return true; }
		}
		return false;
	}

	// $4<Type>
	private static boolean typeof_53(Automaton.State state, Automaton automaton) {
		return typeof_101(state,automaton);
	}

	// Not(^$43<Or($41<^{$38<^Type>...}>)>)
	private static boolean typeof_52(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_102(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Or($41<^{$38<^Type>...}>)>
	private static boolean typeof_59(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_103(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Or(^{$39<^Type>...})$39...})
	private static boolean typeof_58(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_104(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_57(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_105(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^And(^{$39<^Type>...})$39...})
	private static boolean typeof_56(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_106(data,automaton)) { return true; }
		}
		return false;
	}

	// Void
	private static boolean typeof_63(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Void) {
			return true;
		}
		return false;
	}

	// And(^{^Void$40<^Type>...})
	private static boolean typeof_62(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_107(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Or(^{$38<^Type>...})$38...})
	private static boolean typeof_61(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_108(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{$38<^Type>})
	private static boolean typeof_60(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_109(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^Proton)
	private static boolean typeof_68(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_17(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Proton,^Not(^Proton)$40<^Type>...})
	private static boolean typeof_69(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_110(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Void$39<^Type>...})
	private static boolean typeof_70(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_111(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Any$39<^Type>...})
	private static boolean typeof_71(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_112(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Any$40<^Type>...})
	private static boolean typeof_64(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_113(data,automaton)) { return true; }
		}
		return false;
	}

	// Any
	private static boolean typeof_65(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Any) {
			return true;
		}
		return false;
	}

	// Proton
	private static boolean typeof_66(Automaton.State state, Automaton automaton) {
		return typeof_114(state,automaton);
	}

	// And(^{^Proton,^Proton$39<^Type>...})
	private static boolean typeof_67(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_115(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Ref($40<^Type>),^Not(^Ref($40))$40...})
	private static boolean typeof_76(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_116(data,automaton)) { return true; }
		}
		return false;
	}

	// Ref(^Any)
	private static boolean typeof_77(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_15(data,automaton)) { return true; }
		}
		return false;
	}

	// Meta(^Void)
	private static boolean typeof_78(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_13(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^$51<Ref($39<^Type>)>)
	private static boolean typeof_79(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_117(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Ref($40<^Type>),^Ref($40)$40...})
	private static boolean typeof_72(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_118(data,automaton)) { return true; }
		}
		return false;
	}

	// Ref(^Void)
	private static boolean typeof_73(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_13(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Ref(^Any),^Ref($43<^Type>)$43...})
	private static boolean typeof_74(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_75(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_120(data,automaton)) { return true; }
		}
		return false;
	}

	// Meta(^Any)
	private static boolean typeof_85(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_15(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Meta(^Any),^Meta($43<^Type>)$43...})
	private static boolean typeof_84(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_121(data,automaton)) { return true; }
		}
		return false;
	}

	// Set(^[$39<^Type>,^{|^Void$39...|}[^Void$39...]])
	private static boolean typeof_87(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_122(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$40<^Type>])>
	private static boolean typeof_86(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_123(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Meta($40<^Type>),^Meta($40)$40...})
	private static boolean typeof_81(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_124(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_80(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_125(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^$54<Meta($39<^Type>)>)
	private static boolean typeof_83(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_126(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Meta($40<^Type>),^Not(^Meta($40))$40...})
	private static boolean typeof_82(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_127(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_93(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_128(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_128(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_129(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 128);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_129(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_129(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_130(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
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

	// $39<^Type>
	private static boolean typeof_130(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_133(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 130);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_133(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<Type>
	private static boolean typeof_133(Automaton.State state, Automaton automaton) {
		return typeof_134(state,automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_132(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_130(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $2<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_134(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_136(state,automaton)
			|| typeof_137(state,automaton)
			|| typeof_138(state,automaton)
			|| typeof_139(state,automaton)
			|| typeof_140(state,automaton)
			|| typeof_141(state,automaton)
			|| typeof_142(state,automaton)
			|| typeof_143(state,automaton)
			|| typeof_144(state,automaton)
			|| typeof_93(state,automaton);
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_137(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_145(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_136(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_145(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($39<^Type>)>
	private static boolean typeof_139(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_130(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_138(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_130(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_141(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_146(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Meta($39<^Type>)>
	private static boolean typeof_140(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_130(data,automaton)) { return true; }
		}
		return false;
	}

	// $79<Set($77<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_143(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_147(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<Term(^[^string$39<^Type>...])>
	private static boolean typeof_142(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_148(data,automaton)) { return true; }
		}
		return false;
	}

	// Atom
	private static boolean typeof_135(Automaton.State state, Automaton automaton) {
		return typeof_149(state,automaton);
	}

	// $82<Bag($77<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_144(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_147(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_145(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_150(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 145);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_150(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $61<^[^string,$39<^Type>]>
	private static boolean typeof_146(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_151(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 146);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_151(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $77<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_147(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_152(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 147);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_152(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $76<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_152(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_130(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_153(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_153(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_154(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 153);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_154(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $72<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_154(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_130(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $68<^[^string$39<^Type>...]>
	private static boolean typeof_148(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_155(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 148);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_155(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $67<[^string$39<^Type>...]>
	private static boolean typeof_155(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_130(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^string
	private static boolean typeof_156(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_157(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 156);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_157(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// string
	private static boolean typeof_157(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_STRING;
	}

	// Not(^Proton)|Proton
	private static boolean typeof_149(Automaton.State state, Automaton automaton) {
		return typeof_94(state,automaton)
			|| typeof_66(state,automaton);
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_150(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_130(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<[^string,$39<^Type>]>
	private static boolean typeof_151(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_130(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Bag(^[$39<^Type>,^{|^Void$39...|}[^Void$39...]])
	private static boolean typeof_92(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_158(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[$39<^Type>,^{|^Void$39...|}[^Void$39...]]
	private static boolean typeof_158(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_159(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 158);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_159(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [$39<^Type>,^{|^Void$39...|}[^Void$39...]]
	private static boolean typeof_159(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_161(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Void$39<^Type>...|}[^Void$39<^Type>...]
	private static boolean typeof_161(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_162(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 161);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_162(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Void$39<^Type>...|}[^Void$39<^Type>...]
	private static boolean typeof_162(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_160(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_160(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_163(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 160);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_163(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<Type>
	private static boolean typeof_163(Automaton.State state, Automaton automaton) {
		return typeof_164(state,automaton);
	}

	// $2<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_164(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_165(state,automaton)
			|| typeof_166(state,automaton)
			|| typeof_167(state,automaton)
			|| typeof_168(state,automaton)
			|| typeof_169(state,automaton)
			|| typeof_170(state,automaton)
			|| typeof_171(state,automaton)
			|| typeof_172(state,automaton)
			|| typeof_173(state,automaton)
			|| typeof_174(state,automaton);
	}

	// $70<Term(^[^string$39<^Type>...])>
	private static boolean typeof_171(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_175(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_170(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_176(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Meta($39<^Type>)>
	private static boolean typeof_169(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_160(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($39<^Type>)>
	private static boolean typeof_168(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_160(data,automaton)) { return true; }
		}
		return false;
	}

	// $68<^[^string$39<^Type>...]>
	private static boolean typeof_175(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_177(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 175);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_177(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_174(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_178(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($77<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_173(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_179(data,automaton)) { return true; }
		}
		return false;
	}

	// $79<Set($77<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_172(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_179(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_167(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_160(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_166(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_180(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_165(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_180(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_178(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_181(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 178);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_181(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $77<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_179(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_182(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 179);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_182(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $61<^[^string,$39<^Type>]>
	private static boolean typeof_176(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_183(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 176);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_183(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $67<[^string$39<^Type>...]>
	private static boolean typeof_177(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_182(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_184(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_184(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_185(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 184);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_185(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $72<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_185(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<[^string,$39<^Type>]>
	private static boolean typeof_183(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_180(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_186(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 180);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_186(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_186(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_181(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_187(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_187(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_188(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 187);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_188(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_188(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_160(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^SetOrBag)
	private static boolean typeof_95(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_189(data,automaton)) { return true; }
		}
		return false;
	}

	// ^SetOrBag
	private static boolean typeof_189(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_190(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 189);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_190(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// SetOrBag
	private static boolean typeof_190(Automaton.State state, Automaton automaton) {
		return typeof_191(state,automaton);
	}

	// $13<Set($11<^[$4<^Type>,^{|$4...|}[$4...]]>)>|Bag($11)
	private static boolean typeof_191(Automaton.State state, Automaton automaton) {
		return typeof_192(state,automaton)
			|| typeof_193(state,automaton);
	}

	// $81<Bag($9<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_193(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_194(data,automaton)) { return true; }
		}
		return false;
	}

	// $11<Set($9<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_192(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_194(data,automaton)) { return true; }
		}
		return false;
	}

	// $9<^[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_194(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_195(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 194);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_195(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $8<[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_195(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_196(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_197(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<^{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_197(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_198(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 197);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_198(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $2<^Type>
	private static boolean typeof_196(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_199(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 196);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_199(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<Type>
	private static boolean typeof_199(Automaton.State state, Automaton automaton) {
		return typeof_200(state,automaton);
	}

	// $13<Set(^[^Type,^{|^Type...|}[^Type...]])|Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_200(Automaton.State state, Automaton automaton) {
		return typeof_192(state,automaton)
			|| typeof_135(state,automaton)
			|| typeof_201(state,automaton)
			|| typeof_202(state,automaton)
			|| typeof_203(state,automaton)
			|| typeof_204(state,automaton)
			|| typeof_205(state,automaton)
			|| typeof_206(state,automaton)
			|| typeof_207(state,automaton)
			|| typeof_193(state,automaton)
			|| typeof_208(state,automaton);
	}

	// $63<Meta($2<^Type>)>
	private static boolean typeof_205(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_196(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Ref($2<^Type>)>
	private static boolean typeof_204(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_196(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Term(^[^string$2<^Type>...])>
	private static boolean typeof_207(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_209(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<Nominal(^[^string,$2<^Type>])>
	private static boolean typeof_206(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_210(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Or($50<^{$2<^Type>...}>)>
	private static boolean typeof_201(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_211(data,automaton)) { return true; }
		}
		return false;
	}

	// $57<Not($2<^Type>)>
	private static boolean typeof_203(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_196(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<And($50<^{$2<^Type>...}>)>
	private static boolean typeof_202(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_211(data,automaton)) { return true; }
		}
		return false;
	}

	// $4<{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_198(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_196(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^Proton)
	private static boolean typeof_94(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_44(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$2<^Type>,^[$2...]])>
	private static boolean typeof_208(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_212(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$2<^Type>,^[$2...]]>
	private static boolean typeof_212(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_213(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 212);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_213(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$2<^Type>,^[$2...]]>
	private static boolean typeof_213(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_196(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_214(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$2<^Type>...]>
	private static boolean typeof_214(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_215(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 214);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_215(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$2<^Type>...]>
	private static boolean typeof_215(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_196(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Term($61<^[^string,$40<^Type>]>)
	private static boolean typeof_89(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_216(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<^[^string,$40<^Type>]>
	private static boolean typeof_216(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_217(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 216);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_217(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $60<[^string,$40<^Type>]>
	private static boolean typeof_217(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $40<^Type>
	private static boolean typeof_218(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_219(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 218);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_219(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_219(Automaton.State state, Automaton automaton) {
		return typeof_220(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_220(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_221(state,automaton)
			|| typeof_222(state,automaton)
			|| typeof_223(state,automaton)
			|| typeof_224(state,automaton)
			|| typeof_225(state,automaton)
			|| typeof_226(state,automaton)
			|| typeof_227(state,automaton)
			|| typeof_228(state,automaton)
			|| typeof_229(state,automaton)
			|| typeof_230(state,automaton);
	}

	// $45<Or($43<^{$40<^Type>...}>)>
	private static boolean typeof_221(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_231(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<And($43<^{$40<^Type>...}>)>
	private static boolean typeof_222(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_231(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<Not($40<^Type>)>
	private static boolean typeof_223(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_218(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[^string$2<^Type>...]>
	private static boolean typeof_209(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_232(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 209);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_232(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// And(^{^Term(^[^string,$41<^Type>]),^Term(^[^string,$41])$41...})
	private static boolean typeof_88(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_233(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<^[^string,$2<^Type>]>
	private static boolean typeof_210(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_234(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 210);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_234(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $10<List(^[$1<^Type>,^[$1...]])>
	private static boolean typeof_91(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_235(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<^{$2<^Type>...}>
	private static boolean typeof_211(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_236(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 211);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_236(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// And(^{$2<^Type>,^List(^[$2,^[$2...]])$2...})
	private static boolean typeof_90(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_237(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$43<Or($41<^{$38<^Type>...}>)>
	private static boolean typeof_102(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_238(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 102);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_238(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $43<Or($41<^{$38<^Type>...}>)>
	private static boolean typeof_238(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_239(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<^{$38<^Type>...}>
	private static boolean typeof_239(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_240(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 239);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_240(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<^{$38<^Type>...}>
	private static boolean typeof_103(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_241(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 103);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_241(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^{$2<^Type>,^List(^[$2,^[$2...]])$2...}
	private static boolean typeof_237(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_242(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 237);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_242(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^$46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_100(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_243(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 100);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_243(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $49<{$2<^Type>...}>
	private static boolean typeof_236(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_196(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $1<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_101(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_244(state,automaton)
			|| typeof_245(state,automaton)
			|| typeof_246(state,automaton)
			|| typeof_247(state,automaton)
			|| typeof_248(state,automaton)
			|| typeof_249(state,automaton)
			|| typeof_250(state,automaton)
			|| typeof_251(state,automaton)
			|| typeof_252(state,automaton)
			|| typeof_253(state,automaton);
	}

	// $8<^[$1<^Type>,^[$1...]]>
	private static boolean typeof_235(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_254(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 235);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_254(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// And(^{^List(^[^bool,^[$41<^Type>...]]),^List(^[^bool,^[$41...]])$41...})
	private static boolean typeof_98(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_255(data,automaton)) { return true; }
		}
		return false;
	}

	// $68<[^string,$2<^Type>]>
	private static boolean typeof_234(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_196(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{$39<^Type>}
	private static boolean typeof_99(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_256(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 99);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_256(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {$39<^Type>}
	private static boolean typeof_256(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_257(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_257(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_258(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 257);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_258(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<Type>
	private static boolean typeof_258(Automaton.State state, Automaton automaton) {
		return typeof_259(state,automaton);
	}

	// $2<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_259(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_260(state,automaton)
			|| typeof_57(state,automaton)
			|| typeof_261(state,automaton)
			|| typeof_262(state,automaton)
			|| typeof_263(state,automaton)
			|| typeof_264(state,automaton)
			|| typeof_265(state,automaton)
			|| typeof_266(state,automaton)
			|| typeof_267(state,automaton)
			|| typeof_268(state,automaton);
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_262(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_257(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_263(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_257(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_260(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_105(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($39<^Type>)>
	private static boolean typeof_261(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_257(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_266(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_269(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_267(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_269(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_264(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_270(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_265(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_271(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_270(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_272(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 270);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_272(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_272(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_257(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_271(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_273(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 271);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_273(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_273(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_257(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_268(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_274(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_274(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_275(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 274);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_275(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_275(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_257(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_276(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_276(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_277(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 276);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_277(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_277(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_257(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_269(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_278(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 269);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_278(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_278(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_257(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_279(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_279(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_280(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 279);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_280(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_280(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_257(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Term(^[^string,$41<^Type>]),^Term(^[^string,$41])$41...}
	private static boolean typeof_233(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_281(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 233);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_281(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Term(^[^string,$41<^Type>]),^Term(^[^string,$41])$41...}
	private static boolean typeof_281(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_282(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_282(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_283(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $41<^Type>
	private static boolean typeof_283(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_284(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 283);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_284(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Term($61<^[^string,$41<^Type>]>)
	private static boolean typeof_282(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_285(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 282);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_285(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Term($61<^[^string,$41<^Type>]>)
	private static boolean typeof_285(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_286(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<^[^string,$41<^Type>]>
	private static boolean typeof_286(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_287(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 286);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_287(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $60<[^string,$41<^Type>]>
	private static boolean typeof_287(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $7<Type>
	private static boolean typeof_284(Automaton.State state, Automaton automaton) {
		return typeof_288(state,automaton);
	}

	// $4<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_288(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_289(state,automaton)
			|| typeof_290(state,automaton)
			|| typeof_291(state,automaton)
			|| typeof_292(state,automaton)
			|| typeof_293(state,automaton)
			|| typeof_294(state,automaton)
			|| typeof_295(state,automaton)
			|| typeof_296(state,automaton)
			|| typeof_297(state,automaton)
			|| typeof_298(state,automaton);
	}

	// $46<Or($44<^{$41<^Type>...}>)>
	private static boolean typeof_289(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_299(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<And($44<^{$41<^Type>...}>)>
	private static boolean typeof_290(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_299(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<Not($41<^Type>)>
	private static boolean typeof_291(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_283(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Ref($41<^Type>)>
	private static boolean typeof_292(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_283(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Meta($41<^Type>)>
	private static boolean typeof_293(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_283(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Nominal(^[^string,$41<^Type>])>
	private static boolean typeof_294(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_286(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$41<^Type>...])>
	private static boolean typeof_295(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_300(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$41<^Type>,^{|$41...|}[$41...]]>)>
	private static boolean typeof_296(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_301(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$41<^Type>,^{|$41...|}[$41...]]>)>
	private static boolean typeof_297(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_301(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$41<^Type>,^[$41...]])>
	private static boolean typeof_298(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_302(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<^{$41<^Type>...}>
	private static boolean typeof_299(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_303(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 299);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_303(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $67<^[^string$41<^Type>...]>
	private static boolean typeof_300(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_304(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 300);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_304(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$41<^Type>...]>
	private static boolean typeof_304(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$41<^Type>,^{|$41...|}[$41...]]>
	private static boolean typeof_301(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_305(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 301);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_305(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$41<^Type>,^{|$41...|}[$41...]]>
	private static boolean typeof_305(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_306(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$41<^Type>...|}[$41<^Type>...]>
	private static boolean typeof_306(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_307(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 306);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_307(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$41<^Type>...|}[$41<^Type>...]>
	private static boolean typeof_307(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$41<^Type>,^[$41...]]>
	private static boolean typeof_302(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_308(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 302);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_308(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$41<^Type>,^[$41...]]>
	private static boolean typeof_308(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_309(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$41<^Type>...]>
	private static boolean typeof_309(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_310(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 309);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_310(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$41<^Type>...]>
	private static boolean typeof_310(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $43<{$41<^Type>...}>
	private static boolean typeof_303(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// SetOrBag
	private static boolean typeof_96(Automaton.State state, Automaton automaton) {
		return typeof_311(state,automaton);
	}

	// $12<Set($10<^[$3<^Type>,^{|$3...|}[$3...]]>)>|Bag($10)
	private static boolean typeof_311(Automaton.State state, Automaton automaton) {
		return typeof_312(state,automaton)
			|| typeof_313(state,automaton);
	}

	// $81<Bag($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_313(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_314(data,automaton)) { return true; }
		}
		return false;
	}

	// $10<Set($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_312(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_314(data,automaton)) { return true; }
		}
		return false;
	}

	// $8<^[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_314(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_315(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 314);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_315(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $7<[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_315(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_316(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_317(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $4<^{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_317(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_318(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 317);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_318(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $1<^Type>
	private static boolean typeof_316(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_319(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 316);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_319(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $15<Type>
	private static boolean typeof_319(Automaton.State state, Automaton automaton) {
		return typeof_320(state,automaton);
	}

	// $3<{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_318(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_316(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $12<Set(^[^Type,^{|^Type...|}[^Type...]])|Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_320(Automaton.State state, Automaton automaton) {
		return typeof_312(state,automaton)
			|| typeof_135(state,automaton)
			|| typeof_321(state,automaton)
			|| typeof_322(state,automaton)
			|| typeof_323(state,automaton)
			|| typeof_324(state,automaton)
			|| typeof_325(state,automaton)
			|| typeof_326(state,automaton)
			|| typeof_327(state,automaton)
			|| typeof_313(state,automaton)
			|| typeof_328(state,automaton);
	}

	// $71<Nominal(^[^string,$1<^Type>])>
	private static boolean typeof_326(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_329(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Term(^[^string$1<^Type>...])>
	private static boolean typeof_327(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_330(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Ref($1<^Type>)>
	private static boolean typeof_324(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_316(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Meta($1<^Type>)>
	private static boolean typeof_325(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_316(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<And($50<^{$1<^Type>...}>)>
	private static boolean typeof_322(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_331(data,automaton)) { return true; }
		}
		return false;
	}

	// $57<Not($1<^Type>)>
	private static boolean typeof_323(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_316(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Or($50<^{$1<^Type>...}>)>
	private static boolean typeof_321(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_331(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[^string$1<^Type>...]>
	private static boolean typeof_330(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_332(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 330);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_332(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[^string$1<^Type>...]>
	private static boolean typeof_332(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_316(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $50<^{$1<^Type>...}>
	private static boolean typeof_331(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_333(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 331);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_333(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $49<{$1<^Type>...}>
	private static boolean typeof_333(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_316(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$1<^Type>,^[$1...]])>
	private static boolean typeof_328(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_334(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$1<^Type>,^[$1...]]>
	private static boolean typeof_334(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_335(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 334);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_335(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$1<^Type>,^[$1...]]>
	private static boolean typeof_335(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_316(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_336(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$1<^Type>...]>
	private static boolean typeof_336(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_337(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 336);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_337(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$1<^Type>...]>
	private static boolean typeof_337(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_316(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<^[^string,$1<^Type>]>
	private static boolean typeof_329(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_338(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 329);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_338(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $68<[^string,$1<^Type>]>
	private static boolean typeof_338(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_316(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $75<[^string$2<^Type>...]>
	private static boolean typeof_232(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_196(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// List(^[^bool,$86<^[$40<^Type>...]>])
	private static boolean typeof_97(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_339(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^bool,$86<^[$40<^Type>...]>]
	private static boolean typeof_339(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_340(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 339);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_340(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^bool,$86<^[$40<^Type>...]>]
	private static boolean typeof_340(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_341(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^bool
	private static boolean typeof_341(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_342(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 341);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_342(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// bool
	private static boolean typeof_342(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_BOOL;
	}

	// $43<^{$40<^Type>...}>
	private static boolean typeof_231(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_343(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 231);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_343(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $42<{$40<^Type>...}>
	private static boolean typeof_343(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Proton,^Not(^Proton)$40<^Type>...}
	private static boolean typeof_110(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_344(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 110);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_344(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Proton,^Not(^Proton)$40<^Type>...}
	private static boolean typeof_344(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_17(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_345(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_346(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_346(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_347(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 346);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_347(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $32<Type>
	private static boolean typeof_347(Automaton.State state, Automaton automaton) {
		return typeof_348(state,automaton);
	}

	// $29<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_348(Automaton.State state, Automaton automaton) {
		return typeof_349(state,automaton)
			|| typeof_350(state,automaton)
			|| typeof_351(state,automaton)
			|| typeof_352(state,automaton)
			|| typeof_353(state,automaton)
			|| typeof_354(state,automaton)
			|| typeof_355(state,automaton)
			|| typeof_356(state,automaton)
			|| typeof_357(state,automaton)
			|| typeof_358(state,automaton)
			|| typeof_359(state,automaton);
	}

	// $46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_351(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_360(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_350(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_360(data,automaton)) { return true; }
		}
		return false;
	}

	// Atom
	private static boolean typeof_349(Automaton.State state, Automaton automaton) {
		return typeof_361(state,automaton);
	}

	// ^Not(^Proton)
	private static boolean typeof_345(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_362(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 345);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_362(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $91<List(^[$40<^Type>,^[$40...]])>
	private static boolean typeof_230(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_363(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Void$39<^Type>...}
	private static boolean typeof_111(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_364(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 111);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_364(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $81<Bag($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_229(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_365(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Or(^{$38<^Type>...})$38...}
	private static boolean typeof_108(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_366(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 108);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_366(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $78<Set($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_228(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_365(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$38<^Type>}
	private static boolean typeof_109(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_367(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 109);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_367(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $69<Term(^[^string$40<^Type>...])>
	private static boolean typeof_227(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_368(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$40<^Type>...]>
	private static boolean typeof_368(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_369(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 368);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_369(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$40<^Type>...]>
	private static boolean typeof_369(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^And(^{$39<^Type>...})$39...}
	private static boolean typeof_106(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_370(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 106);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_370(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^And(^{$39<^Type>...})$39...}
	private static boolean typeof_370(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_257(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $63<Nominal(^[^string,$40<^Type>])>
	private static boolean typeof_226(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_216(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Void$40<^Type>...}
	private static boolean typeof_107(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_371(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 107);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_371(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Void$40<^Type>...}
	private static boolean typeof_371(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_372(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_372(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_373(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 372);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_373(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $8<Type>
	private static boolean typeof_373(Automaton.State state, Automaton automaton) {
		return typeof_374(state,automaton);
	}

	// $5<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_374(Automaton.State state, Automaton automaton) {
		return typeof_375(state,automaton)
			|| typeof_376(state,automaton)
			|| typeof_377(state,automaton)
			|| typeof_378(state,automaton)
			|| typeof_379(state,automaton)
			|| typeof_380(state,automaton)
			|| typeof_381(state,automaton)
			|| typeof_382(state,automaton)
			|| typeof_383(state,automaton)
			|| typeof_384(state,automaton)
			|| typeof_385(state,automaton);
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_385(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_386(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_386(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_387(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 386);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_387(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_387(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_372(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_388(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_384(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_389(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_389(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_390(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 389);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_390(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_390(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_372(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_391(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_391(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_392(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 391);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_392(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_392(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_372(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_388(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_393(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 388);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_393(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_393(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_372(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Atom
	private static boolean typeof_375(Automaton.State state, Automaton automaton) {
		return typeof_394(state,automaton);
	}

	// Not(^Proton)|Proton
	private static boolean typeof_394(Automaton.State state, Automaton automaton) {
		return typeof_395(state,automaton)
			|| typeof_396(state,automaton);
	}

	// Not(^Proton)
	private static boolean typeof_395(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_397(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton
	private static boolean typeof_397(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_396(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 397);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_396(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Proton
	private static boolean typeof_396(Automaton.State state, Automaton automaton) {
		return typeof_398(state,automaton);
	}

	// Void|Any|Bool|Int|Real|String
	private static boolean typeof_398(Automaton.State state, Automaton automaton) {
		return typeof_63(state,automaton)
			|| typeof_65(state,automaton)
			|| typeof_399(state,automaton)
			|| typeof_400(state,automaton)
			|| typeof_401(state,automaton)
			|| typeof_402(state,automaton);
	}

	// String
	private static boolean typeof_402(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_String) {
			return true;
		}
		return false;
	}

	// Int
	private static boolean typeof_400(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Int) {
			return true;
		}
		return false;
	}

	// Real
	private static boolean typeof_401(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Real) {
			return true;
		}
		return false;
	}

	// Bool
	private static boolean typeof_399(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bool) {
			return true;
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_381(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_403(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_403(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_404(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 403);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_404(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_404(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_372(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_380(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_372(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_383(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_389(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_382(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_405(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_405(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_406(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 405);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_406(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_406(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_372(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_377(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_407(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_407(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_408(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 407);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_408(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_408(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_372(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_376(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_407(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_379(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_372(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Meta($40<^Type>)>
	private static boolean typeof_225(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_218(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Or(^{$39<^Type>...})$39...}
	private static boolean typeof_104(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_409(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 104);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_409(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Or(^{$39<^Type>...})$39...}
	private static boolean typeof_409(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_410(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_411(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^$44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_410(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_412(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 410);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_412(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $39<^Type>
	private static boolean typeof_411(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_413(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 411);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_413(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_412(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_414(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_414(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_415(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 414);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_415(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_415(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_411(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $6<Type>
	private static boolean typeof_413(Automaton.State state, Automaton automaton) {
		return typeof_416(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_416(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_412(state,automaton)
			|| typeof_417(state,automaton)
			|| typeof_418(state,automaton)
			|| typeof_419(state,automaton)
			|| typeof_420(state,automaton)
			|| typeof_421(state,automaton)
			|| typeof_422(state,automaton)
			|| typeof_423(state,automaton)
			|| typeof_424(state,automaton)
			|| typeof_425(state,automaton);
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_425(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_426(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_424(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_427(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_427(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_428(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 427);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_428(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_426(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_429(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 426);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_429(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_429(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_411(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_430(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_428(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_411(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_431(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_431(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_432(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 431);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_432(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_432(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_411(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_430(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_433(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 430);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_433(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_433(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_411(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_417(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_414(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_419(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_411(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($39<^Type>)>
	private static boolean typeof_418(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_411(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_421(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_434(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_434(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_435(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 434);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_435(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_435(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_411(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_420(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_411(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_423(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_427(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_422(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_436(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_436(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_437(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 436);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_437(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_437(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_411(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $48<Not($39<^Type>)>
	private static boolean typeof_378(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_372(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Ref($40<^Type>)>
	private static boolean typeof_224(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_218(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_105(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_438(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 105);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_438(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_438(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_257(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_356(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_439(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_439(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_440(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 439);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_440(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_440(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_346(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $7<[$1<^Type>,^[$1...]]>
	private static boolean typeof_254(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_441(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_442(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $1<^Type>
	private static boolean typeof_441(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_443(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 441);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_443(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $4<^[$1<^Type>...]>
	private static boolean typeof_442(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_444(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 442);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_444(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $15<Type>
	private static boolean typeof_443(Automaton.State state, Automaton automaton) {
		return typeof_445(state,automaton);
	}

	// $3<[$1<^Type>...]>
	private static boolean typeof_444(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_441(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $12<List(^[^Type,^[^Type...]])|Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])>
	private static boolean typeof_445(Automaton.State state, Automaton automaton) {
		return typeof_91(state,automaton)
			|| typeof_135(state,automaton)
			|| typeof_446(state,automaton)
			|| typeof_447(state,automaton)
			|| typeof_448(state,automaton)
			|| typeof_449(state,automaton)
			|| typeof_450(state,automaton)
			|| typeof_451(state,automaton)
			|| typeof_452(state,automaton)
			|| typeof_453(state,automaton)
			|| typeof_454(state,automaton);
	}

	// $53<Or($51<^{$1<^Type>...}>)>
	private static boolean typeof_446(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_455(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<And($51<^{$1<^Type>...}>)>
	private static boolean typeof_447(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_455(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<^{$1<^Type>...}>
	private static boolean typeof_455(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_456(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 455);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_456(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $50<{$1<^Type>...}>
	private static boolean typeof_456(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_441(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<Bag($86<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_454(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_457(data,automaton)) { return true; }
		}
		return false;
	}

	// $86<^[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_457(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_458(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 457);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_458(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $85<[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_458(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_441(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_459(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $82<^{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_459(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_460(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 459);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_460(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $81<{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_460(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_441(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $88<Set($86<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_453(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_457(data,automaton)) { return true; }
		}
		return false;
	}

	// $79<Term(^[^string$1<^Type>...])>
	private static boolean typeof_452(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_461(data,automaton)) { return true; }
		}
		return false;
	}

	// $77<^[^string$1<^Type>...]>
	private static boolean typeof_461(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_462(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 461);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_462(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $76<[^string$1<^Type>...]>
	private static boolean typeof_462(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_441(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<Nominal(^[^string,$1<^Type>])>
	private static boolean typeof_451(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_463(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<^[^string,$1<^Type>]>
	private static boolean typeof_463(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_464(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 463);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_464(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $69<[^string,$1<^Type>]>
	private static boolean typeof_464(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_441(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $64<Meta($1<^Type>)>
	private static boolean typeof_450(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_441(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Ref($1<^Type>)>
	private static boolean typeof_449(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_441(data,automaton)) { return true; }
		}
		return false;
	}

	// $58<Not($1<^Type>)>
	private static boolean typeof_448(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_441(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Ref(^Any),^Ref($43<^Type>)$43...}
	private static boolean typeof_119(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_465(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 119);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_465(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Ref(^Any),^Ref($43<^Type>)$43...}
	private static boolean typeof_465(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_26(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_466(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_467(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^$51<Ref($39<^Type>)>
	private static boolean typeof_466(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_468(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 466);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_468(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_468(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_467(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_467(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_469(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 467);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_469(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<Type>
	private static boolean typeof_469(Automaton.State state, Automaton automaton) {
		return typeof_470(state,automaton);
	}

	// $6<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_470(Automaton.State state, Automaton automaton) {
		return typeof_471(state,automaton)
			|| typeof_472(state,automaton)
			|| typeof_473(state,automaton)
			|| typeof_474(state,automaton)
			|| typeof_468(state,automaton)
			|| typeof_475(state,automaton)
			|| typeof_476(state,automaton)
			|| typeof_477(state,automaton)
			|| typeof_478(state,automaton)
			|| typeof_479(state,automaton)
			|| typeof_480(state,automaton);
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_478(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_481(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_479(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_481(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_476(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_482(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_477(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_483(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_474(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_467(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_475(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_467(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_472(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_484(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_473(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_484(data,automaton)) { return true; }
		}
		return false;
	}

	// Atom
	private static boolean typeof_471(Automaton.State state, Automaton automaton) {
		return typeof_485(state,automaton);
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_357(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_486(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^List(^[^bool,^[$41<^Type>...]]),^List(^[^bool,^[$41...]])$41...}
	private static boolean typeof_255(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_487(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 255);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_487(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^{^Ref($40<^Type>),^Ref($40)$40...}
	private static boolean typeof_118(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_488(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 118);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_488(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_358(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_486(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_252(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_489(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$51<Ref($39<^Type>)>
	private static boolean typeof_117(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_490(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 117);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_490(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_359(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_491(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_253(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_492(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_492(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_493(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 492);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_493(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_493(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_494(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_494(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_495(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 494);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_495(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_495(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
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

	// ^{^Ref($40<^Type>),^Not(^Ref($40))$40...}
	private static boolean typeof_116(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_496(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 116);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_496(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Ref($40<^Type>),^Not(^Ref($40))$40...}
	private static boolean typeof_496(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_497(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_498(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_499(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^$51<Ref($40<^Type>)>
	private static boolean typeof_497(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_500(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 497);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_500(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $51<Ref($40<^Type>)>
	private static boolean typeof_500(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_499(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^$51<Ref($40<^Type>)>)
	private static boolean typeof_498(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_501(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 498);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_501(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Not(^$51<Ref($40<^Type>)>)
	private static boolean typeof_501(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_497(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<^Type>
	private static boolean typeof_499(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_502(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 499);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_502(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_502(Automaton.State state, Automaton automaton) {
		return typeof_503(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_503(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_504(state,automaton)
			|| typeof_505(state,automaton)
			|| typeof_506(state,automaton)
			|| typeof_500(state,automaton)
			|| typeof_507(state,automaton)
			|| typeof_508(state,automaton)
			|| typeof_509(state,automaton)
			|| typeof_510(state,automaton)
			|| typeof_511(state,automaton)
			|| typeof_512(state,automaton);
	}

	// $91<List(^[$40<^Type>,^[$40...]])>
	private static boolean typeof_512(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_513(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$40<^Type>,^[$40...]]>
	private static boolean typeof_513(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_514(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 513);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_514(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$40<^Type>,^[$40...]]>
	private static boolean typeof_514(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_499(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_515(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$40<^Type>...]>
	private static boolean typeof_515(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_516(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 515);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_516(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$40<^Type>...]>
	private static boolean typeof_516(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_499(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $62<Nominal(^[^string,$40<^Type>])>
	private static boolean typeof_508(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_517(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$40<^Type>]>
	private static boolean typeof_517(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_518(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 517);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_518(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$40<^Type>]>
	private static boolean typeof_518(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_499(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<Term(^[^string$40<^Type>...])>
	private static boolean typeof_509(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_519(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$40<^Type>...]>
	private static boolean typeof_519(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_520(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 519);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_520(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$40<^Type>...]>
	private static boolean typeof_520(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_499(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_510(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_521(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_521(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_522(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 521);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_522(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_522(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_499(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_523(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_523(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_524(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 523);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_524(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_524(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_499(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_511(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_521(data,automaton)) { return true; }
		}
		return false;
	}

	// $45<Or($43<^{$40<^Type>...}>)>
	private static boolean typeof_504(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_525(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<^{$40<^Type>...}>
	private static boolean typeof_525(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_526(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 525);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_526(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $42<{$40<^Type>...}>
	private static boolean typeof_526(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_499(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<And($43<^{$40<^Type>...}>)>
	private static boolean typeof_505(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_525(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($40<^Type>)>
	private static boolean typeof_506(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_499(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($40<^Type>)>
	private static boolean typeof_507(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_499(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_489(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_527(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 489);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_527(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_527(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_528(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_528(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_529(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 528);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_529(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_529(Automaton.State _state, Automaton automaton) {
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

	// $48<Not($39<^Type>)>
	private static boolean typeof_352(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_346(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$38<^Type>...])>
	private static boolean typeof_250(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_530(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$38<^Type>...]>
	private static boolean typeof_530(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_531(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 530);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_531(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$38<^Type>...]>
	private static boolean typeof_531(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Proton,^Proton$39<^Type>...}
	private static boolean typeof_115(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_532(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 115);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_532(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Proton,^Proton$39<^Type>...}
	private static boolean typeof_532(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_17(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_17(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_533(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_533(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_534(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 533);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_534(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $29<Type>
	private static boolean typeof_534(Automaton.State state, Automaton automaton) {
		return typeof_535(state,automaton);
	}

	// $26<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_535(Automaton.State state, Automaton automaton) {
		return typeof_349(state,automaton)
			|| typeof_536(state,automaton)
			|| typeof_537(state,automaton)
			|| typeof_538(state,automaton)
			|| typeof_539(state,automaton)
			|| typeof_540(state,automaton)
			|| typeof_541(state,automaton)
			|| typeof_542(state,automaton)
			|| typeof_543(state,automaton)
			|| typeof_544(state,automaton)
			|| typeof_545(state,automaton);
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_544(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_546(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_546(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_547(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 546);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_547(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_547(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_548(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_548(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_549(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 548);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_549(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_549(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_545(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_550(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_550(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_551(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 550);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_551(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_551(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_552(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_552(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_553(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 552);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_553(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_553(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_541(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_554(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_554(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_555(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 554);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_555(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_555(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_540(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_533(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_543(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_546(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_542(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_556(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_556(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_557(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 556);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_557(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_557(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_537(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_558(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_558(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_559(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 558);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_559(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_559(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_536(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_558(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_539(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_533(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($39<^Type>)>
	private static boolean typeof_538(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_533(data,automaton)) { return true; }
		}
		return false;
	}

	// {^Ref($40<^Type>),^Ref($40)$40...}
	private static boolean typeof_488(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_497(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_497(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_499(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_353(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_346(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_251(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_489(data,automaton)) { return true; }
		}
		return false;
	}

	// Any|Void|Bool|Int|Real|String
	private static boolean typeof_114(Automaton.State state, Automaton automaton) {
		return typeof_65(state,automaton)
			|| typeof_63(state,automaton)
			|| typeof_399(state,automaton)
			|| typeof_400(state,automaton)
			|| typeof_401(state,automaton)
			|| typeof_402(state,automaton);
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_491(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_560(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 491);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_560(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_560(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_346(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_561(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_561(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_562(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 561);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_562(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_562(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_346(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_354(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_346(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($38<^Type>)>
	private static boolean typeof_248(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Any$40<^Type>...}
	private static boolean typeof_113(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_563(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 113);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_563(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Any$40<^Type>...}
	private static boolean typeof_563(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_15(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_564(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_564(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_565(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 564);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_565(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $8<Type>
	private static boolean typeof_565(Automaton.State state, Automaton automaton) {
		return typeof_566(state,automaton);
	}

	// $5<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_566(Automaton.State state, Automaton automaton) {
		return typeof_471(state,automaton)
			|| typeof_567(state,automaton)
			|| typeof_568(state,automaton)
			|| typeof_569(state,automaton)
			|| typeof_570(state,automaton)
			|| typeof_571(state,automaton)
			|| typeof_572(state,automaton)
			|| typeof_573(state,automaton)
			|| typeof_574(state,automaton)
			|| typeof_575(state,automaton)
			|| typeof_576(state,automaton);
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_567(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_577(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_575(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_578(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_574(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_578(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_573(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_579(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_572(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_580(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_571(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_564(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_570(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_564(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($39<^Type>)>
	private static boolean typeof_569(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_564(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_568(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_577(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_576(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_581(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_577(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_582(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 577);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_582(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_578(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_583(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 578);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_583(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_579(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_584(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 579);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_584(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_580(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_585(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 580);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_585(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_581(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_586(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 581);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_586(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_582(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_564(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_583(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_564(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_587(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_584(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_564(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_585(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_564(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_586(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_564(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_588(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_587(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_589(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 587);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_589(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_588(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_590(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 588);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_590(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_589(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_564(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_590(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_564(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_490(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_591(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_591(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_592(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 591);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_592(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_592(Automaton.State state, Automaton automaton) {
		return typeof_593(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_593(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_594(state,automaton)
			|| typeof_595(state,automaton)
			|| typeof_596(state,automaton)
			|| typeof_490(state,automaton)
			|| typeof_597(state,automaton)
			|| typeof_598(state,automaton)
			|| typeof_599(state,automaton)
			|| typeof_600(state,automaton)
			|| typeof_601(state,automaton)
			|| typeof_602(state,automaton);
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_595(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_603(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_594(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_603(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_597(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_591(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_596(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_591(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_599(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_604(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_598(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_605(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_355(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_606(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$38<^Type>])>
	private static boolean typeof_249(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_607(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Any$39<^Type>...}
	private static boolean typeof_112(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_608(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 112);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_608(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Any$39<^Type>...}
	private static boolean typeof_608(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_15(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_609(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_609(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_610(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 609);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_610(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $8<Type>
	private static boolean typeof_610(Automaton.State state, Automaton automaton) {
		return typeof_611(state,automaton);
	}

	// $5<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_611(Automaton.State state, Automaton automaton) {
		return typeof_471(state,automaton)
			|| typeof_612(state,automaton)
			|| typeof_613(state,automaton)
			|| typeof_614(state,automaton)
			|| typeof_615(state,automaton)
			|| typeof_616(state,automaton)
			|| typeof_617(state,automaton)
			|| typeof_618(state,automaton)
			|| typeof_619(state,automaton)
			|| typeof_620(state,automaton)
			|| typeof_621(state,automaton);
	}

	// $48<Not($38<^Type>)>
	private static boolean typeof_614(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_609(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($38<^Type>)>
	private static boolean typeof_615(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_609(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Or($41<^{$38<^Type>...}>)>
	private static boolean typeof_612(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_622(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<And($41<^{$38<^Type>...}>)>
	private static boolean typeof_613(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_622(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$38<^Type>...])>
	private static boolean typeof_618(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_623(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_619(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_624(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($38<^Type>)>
	private static boolean typeof_616(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_609(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$38<^Type>])>
	private static boolean typeof_617(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_625(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<^{$38<^Type>...}>
	private static boolean typeof_622(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_626(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 622);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_626(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $67<^[^string$38<^Type>...]>
	private static boolean typeof_623(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_627(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 623);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_627(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_620(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_624(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_621(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_628(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<[^string$38<^Type>...]>
	private static boolean typeof_627(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $40<{$38<^Type>...}>
	private static boolean typeof_626(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$38<^Type>]>
	private static boolean typeof_625(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_629(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 625);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_629(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_624(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_630(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 624);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_630(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_630(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_631(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_631(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_632(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 631);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_632(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$38<^Type>]>
	private static boolean typeof_629(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_628(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_633(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 628);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_633(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_633(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_634(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_634(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_635(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 634);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_635(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_635(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_632(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_601(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_636(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_636(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_637(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 636);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_637(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_637(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_591(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_638(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_639(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 638);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_639(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_639(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_591(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^Proton)|Proton
	private static boolean typeof_485(Automaton.State state, Automaton automaton) {
		return typeof_640(state,automaton)
			|| typeof_66(state,automaton);
	}

	// Not(^Proton)
	private static boolean typeof_640(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_641(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton
	private static boolean typeof_641(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_66(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 641);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_66(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Void$39<^Type>...}
	private static boolean typeof_364(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_642(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_642(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_643(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 642);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_643(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $8<Type>
	private static boolean typeof_643(Automaton.State state, Automaton automaton) {
		return typeof_644(state,automaton);
	}

	// $5<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_644(Automaton.State state, Automaton automaton) {
		return typeof_375(state,automaton)
			|| typeof_645(state,automaton)
			|| typeof_646(state,automaton)
			|| typeof_647(state,automaton)
			|| typeof_648(state,automaton)
			|| typeof_649(state,automaton)
			|| typeof_650(state,automaton)
			|| typeof_651(state,automaton)
			|| typeof_652(state,automaton)
			|| typeof_653(state,automaton)
			|| typeof_654(state,automaton);
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_653(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_655(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_652(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_655(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_655(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_656(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 655);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_656(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_654(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_657(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($38<^Type>)>
	private static boolean typeof_649(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_642(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($38<^Type>)>
	private static boolean typeof_648(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_642(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$38<^Type>...])>
	private static boolean typeof_651(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_658(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$38<^Type>])>
	private static boolean typeof_650(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_659(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Or($41<^{$38<^Type>...}>)>
	private static boolean typeof_645(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_660(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($38<^Type>)>
	private static boolean typeof_647(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_642(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<And($41<^{$38<^Type>...}>)>
	private static boolean typeof_646(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_660(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<^{$38<^Type>...}>
	private static boolean typeof_660(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_661(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 660);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_661(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $40<{$38<^Type>...}>
	private static boolean typeof_661(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_642(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_656(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_642(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_662(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_662(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_663(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 662);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_663(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_663(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_642(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_657(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_664(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 657);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_664(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_664(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_642(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_665(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_665(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_666(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 665);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_666(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_666(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_642(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<^[^string$38<^Type>...]>
	private static boolean typeof_658(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_667(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 658);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_667(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$38<^Type>...]>
	private static boolean typeof_667(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_642(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$38<^Type>]>
	private static boolean typeof_659(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_668(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 659);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_668(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$38<^Type>]>
	private static boolean typeof_668(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_642(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $48<Not($38<^Type>)>
	private static boolean typeof_246(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Meta($40<^Type>),^Not(^Meta($40))$40...}
	private static boolean typeof_127(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_669(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 127);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_669(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Meta($40<^Type>),^Not(^Meta($40))$40...}
	private static boolean typeof_669(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_670(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_671(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_672(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $40<^Type>
	private static boolean typeof_672(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_673(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 672);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_673(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_673(Automaton.State state, Automaton automaton) {
		return typeof_674(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_674(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_675(state,automaton)
			|| typeof_676(state,automaton)
			|| typeof_677(state,automaton)
			|| typeof_678(state,automaton)
			|| typeof_679(state,automaton)
			|| typeof_680(state,automaton)
			|| typeof_681(state,automaton)
			|| typeof_682(state,automaton)
			|| typeof_683(state,automaton)
			|| typeof_684(state,automaton);
	}

	// $91<List(^[$40<^Type>,^[$40...]])>
	private static boolean typeof_684(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_685(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$40<^Type>,^[$40...]]>
	private static boolean typeof_685(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_686(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 685);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_686(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$40<^Type>,^[$40...]]>
	private static boolean typeof_686(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_672(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_687(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$40<^Type>...]>
	private static boolean typeof_687(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_688(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 687);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_688(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $81<Bag($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_683(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_689(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_682(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_689(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$40<^Type>...])>
	private static boolean typeof_681(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_690(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$40<^Type>])>
	private static boolean typeof_680(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_691(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($40<^Type>)>
	private static boolean typeof_679(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_672(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($40<^Type>)>
	private static boolean typeof_678(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_672(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($40<^Type>)>
	private static boolean typeof_677(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_672(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<And($43<^{$40<^Type>...}>)>
	private static boolean typeof_676(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_692(data,automaton)) { return true; }
		}
		return false;
	}

	// $45<Or($43<^{$40<^Type>...}>)>
	private static boolean typeof_675(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_692(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<^{$40<^Type>...}>
	private static boolean typeof_692(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_693(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 692);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_693(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $42<{$40<^Type>...}>
	private static boolean typeof_693(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_672(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<^[^string$40<^Type>...]>
	private static boolean typeof_690(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_694(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 690);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_694(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$40<^Type>...]>
	private static boolean typeof_694(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_672(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$40<^Type>]>
	private static boolean typeof_691(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_695(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 691);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_695(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$40<^Type>]>
	private static boolean typeof_695(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_672(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<[$40<^Type>...]>
	private static boolean typeof_688(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_672(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_689(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_696(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 689);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_696(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_696(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_672(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_697(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_697(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_698(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 697);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_698(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_698(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_672(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^$54<Meta($40<^Type>)>
	private static boolean typeof_670(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_679(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 670);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_679(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Not(^$54<Meta($40<^Type>)>)
	private static boolean typeof_671(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_699(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 671);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_699(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Not(^$54<Meta($40<^Type>)>)
	private static boolean typeof_699(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_670(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_600(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_636(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_484(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_700(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 484);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_700(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_700(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_467(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_365(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_701(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 365);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_701(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_701(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_702(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_702(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_703(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 702);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_703(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_703(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<Ref($38<^Type>)>
	private static boolean typeof_247(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$54<Meta($39<^Type>)>
	private static boolean typeof_126(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_704(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 126);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_704(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_704(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_705(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_705(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_706(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 705);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_706(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_706(Automaton.State state, Automaton automaton) {
		return typeof_707(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_707(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_708(state,automaton)
			|| typeof_709(state,automaton)
			|| typeof_710(state,automaton)
			|| typeof_711(state,automaton)
			|| typeof_704(state,automaton)
			|| typeof_712(state,automaton)
			|| typeof_713(state,automaton)
			|| typeof_714(state,automaton)
			|| typeof_715(state,automaton)
			|| typeof_716(state,automaton);
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_713(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_717(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_712(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_718(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_715(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_719(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_714(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_719(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_717(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_720(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 717);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_720(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_716(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_721(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_719(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_722(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 719);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_722(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_718(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_723(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 718);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_723(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_709(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_724(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_708(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_724(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($39<^Type>)>
	private static boolean typeof_711(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_705(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_710(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_705(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_720(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_705(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_721(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_725(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 721);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_725(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_722(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_705(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_726(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_603(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_727(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 603);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_727(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^List(^[^bool,^[$41<^Type>...]]),^List(^[^bool,^[$41...]])$41...}
	private static boolean typeof_487(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_728(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_728(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_729(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^List(^[^bool,$86<^[$41<^Type>...]>])
	private static boolean typeof_728(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_730(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 728);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_730(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $40<^Type>
	private static boolean typeof_729(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_731(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 729);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_731(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// List(^[^bool,$86<^[$41<^Type>...]>])
	private static boolean typeof_730(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_732(data,automaton)) { return true; }
		}
		return false;
	}

	// $6<Type>
	private static boolean typeof_731(Automaton.State state, Automaton automaton) {
		return typeof_733(state,automaton);
	}

	// ^[^bool,$86<^[$41<^Type>...]>]
	private static boolean typeof_732(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_734(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 732);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_734(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_733(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_735(state,automaton)
			|| typeof_736(state,automaton)
			|| typeof_737(state,automaton)
			|| typeof_738(state,automaton)
			|| typeof_739(state,automaton)
			|| typeof_740(state,automaton)
			|| typeof_741(state,automaton)
			|| typeof_742(state,automaton)
			|| typeof_743(state,automaton)
			|| typeof_744(state,automaton);
	}

	// $91<List(^[$40<^Type>,^[$40...]])>
	private static boolean typeof_744(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_745(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$40<^Type>,^[$40...]]>
	private static boolean typeof_745(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_746(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 745);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_746(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$40<^Type>,^[$40...]]>
	private static boolean typeof_746(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_729(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_747(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$40<^Type>...]>
	private static boolean typeof_747(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_748(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 747);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_748(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$40<^Type>...]>
	private static boolean typeof_748(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_729(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $55<Meta($40<^Type>)>
	private static boolean typeof_739(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_729(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($40<^Type>)>
	private static boolean typeof_738(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_729(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($40<^Type>)>
	private static boolean typeof_737(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_729(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<And($43<^{$40<^Type>...}>)>
	private static boolean typeof_736(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_749(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<^{$40<^Type>...}>
	private static boolean typeof_749(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_750(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 749);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_750(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $42<{$40<^Type>...}>
	private static boolean typeof_750(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_729(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $82<Bag($77<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_743(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_751(data,automaton)) { return true; }
		}
		return false;
	}

	// $77<^[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_751(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_752(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 751);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_752(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $79<Set($77<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_742(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_751(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<Term(^[^string$40<^Type>...])>
	private static boolean typeof_741(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_753(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Nominal(^[^string,$40<^Type>])>
	private static boolean typeof_740(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_754(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<^[^string,$40<^Type>]>
	private static boolean typeof_754(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_755(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 754);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_755(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $60<[^string,$40<^Type>]>
	private static boolean typeof_755(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_729(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_752(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_729(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_756(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $68<^[^string$40<^Type>...]>
	private static boolean typeof_753(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_757(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 753);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_757(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $73<^{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_756(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_758(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 756);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_758(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $72<{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_758(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_729(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<[^string$40<^Type>...]>
	private static boolean typeof_757(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_729(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// [^bool,$86<^[$41<^Type>...]>]
	private static boolean typeof_734(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_341(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_747(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $45<Or($43<^{$40<^Type>...}>)>
	private static boolean typeof_735(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_749(data,automaton)) { return true; }
		}
		return false;
	}

	// {^Or(^{$38<^Type>...})$38...}
	private static boolean typeof_366(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_759(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_759(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_760(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 759);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_760(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<Type>
	private static boolean typeof_760(Automaton.State state, Automaton automaton) {
		return typeof_761(state,automaton);
	}

	// $2<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_761(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_59(state,automaton)
			|| typeof_762(state,automaton)
			|| typeof_763(state,automaton)
			|| typeof_764(state,automaton)
			|| typeof_765(state,automaton)
			|| typeof_766(state,automaton)
			|| typeof_767(state,automaton)
			|| typeof_768(state,automaton)
			|| typeof_769(state,automaton)
			|| typeof_770(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_770(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_771(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_771(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_772(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 771);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_772(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_772(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_759(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_773(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_773(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_774(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 773);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_774(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_774(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_759(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_768(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_775(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_775(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_776(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 775);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_776(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_769(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_775(data,automaton)) { return true; }
		}
		return false;
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_776(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_759(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_777(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_777(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_778(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 777);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_778(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_778(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_759(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<And($41<^{$38<^Type>...}>)>
	private static boolean typeof_762(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_103(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($38<^Type>)>
	private static boolean typeof_763(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_759(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$38<^Type>])>
	private static boolean typeof_766(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_779(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$38<^Type>]>
	private static boolean typeof_779(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_780(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 779);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_780(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$38<^Type>]>
	private static boolean typeof_780(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_759(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<Term(^[^string$38<^Type>...])>
	private static boolean typeof_767(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_781(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$38<^Type>...]>
	private static boolean typeof_781(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_782(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 781);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_782(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$38<^Type>...]>
	private static boolean typeof_782(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_759(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<Ref($38<^Type>)>
	private static boolean typeof_764(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_759(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($38<^Type>)>
	private static boolean typeof_765(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_759(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Or($41<^{$38<^Type>...}>)>
	private static boolean typeof_244(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_783(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<^{$38<^Type>...}>
	private static boolean typeof_783(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_784(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 783);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_784(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $40<{$38<^Type>...}>
	private static boolean typeof_784(Automaton.State _state, Automaton automaton) {
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

	// $39<^Type>
	private static boolean typeof_125(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_785(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 125);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_785(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<Type>
	private static boolean typeof_785(Automaton.State state, Automaton automaton) {
		return typeof_786(state,automaton);
	}

	// $2<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_786(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_787(state,automaton)
			|| typeof_788(state,automaton)
			|| typeof_789(state,automaton)
			|| typeof_790(state,automaton)
			|| typeof_80(state,automaton)
			|| typeof_791(state,automaton)
			|| typeof_792(state,automaton)
			|| typeof_793(state,automaton)
			|| typeof_794(state,automaton)
			|| typeof_795(state,automaton);
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_791(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_796(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($39<^Type>)>
	private static boolean typeof_790(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_125(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_789(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_125(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_788(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_797(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_787(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_797(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_797(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_798(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 797);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_798(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_798(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_125(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_796(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_799(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 796);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_799(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_799(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_125(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_795(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_800(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_800(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_801(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 800);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_801(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_801(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_125(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_802(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_802(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_803(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 802);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_803(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_803(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_125(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_794(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_804(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_804(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_805(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 804);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_805(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_805(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_125(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_806(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_806(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_807(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 806);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_807(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_807(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_125(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_793(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_804(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_792(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_808(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_808(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_809(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 808);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_809(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_809(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_125(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_723(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_705(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_602(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_810(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_810(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_811(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 810);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_811(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_811(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_591(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_812(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_812(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_813(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 812);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_813(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_813(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_591(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_486(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_814(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 486);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_814(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_814(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_346(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_815(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_815(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_816(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 815);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_816(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_816(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_346(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {$38<^Type>}
	private static boolean typeof_367(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_759(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $46<And($41<^{$38<^Type>...}>)>
	private static boolean typeof_245(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_783(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Meta($40<^Type>),^Meta($40)$40...}
	private static boolean typeof_124(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_817(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 124);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_817(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Meta($40<^Type>),^Meta($40)$40...}
	private static boolean typeof_817(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_670(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_670(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_672(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_724(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_818(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 724);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_818(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_818(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_705(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_605(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_819(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 605);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_819(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_819(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_591(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_481(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_820(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 481);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_820(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_820(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_467(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_821(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_821(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_822(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 821);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_822(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_822(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_467(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_360(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_823(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 360);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_823(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_823(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_346(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {$2<^Type>,^List(^[$2,^[$2...]])$2...}
	private static boolean typeof_242(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_824(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_825(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_824(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^$11<List(^[$2<^Type>,^[$2...]])>
	private static boolean typeof_825(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_826(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 825);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_826(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $2<^Type>
	private static boolean typeof_824(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_827(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 824);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_827(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<Type>
	private static boolean typeof_827(Automaton.State state, Automaton automaton) {
		return typeof_828(state,automaton);
	}

	// $13<List(^[^Type,^[^Type...]])|Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])>
	private static boolean typeof_828(Automaton.State state, Automaton automaton) {
		return typeof_826(state,automaton)
			|| typeof_135(state,automaton)
			|| typeof_829(state,automaton)
			|| typeof_830(state,automaton)
			|| typeof_831(state,automaton)
			|| typeof_832(state,automaton)
			|| typeof_833(state,automaton)
			|| typeof_834(state,automaton)
			|| typeof_835(state,automaton)
			|| typeof_836(state,automaton)
			|| typeof_837(state,automaton);
	}

	// $54<Or($52<^{$2<^Type>...}>)>
	private static boolean typeof_829(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_838(data,automaton)) { return true; }
		}
		return false;
	}

	// $58<Not($2<^Type>)>
	private static boolean typeof_831(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_824(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<And($52<^{$2<^Type>...}>)>
	private static boolean typeof_830(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_838(data,automaton)) { return true; }
		}
		return false;
	}

	// $11<List(^[$2<^Type>,^[$2...]])>
	private static boolean typeof_826(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_839(data,automaton)) { return true; }
		}
		return false;
	}

	// $72<Nominal(^[^string,$2<^Type>])>
	private static boolean typeof_834(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_840(data,automaton)) { return true; }
		}
		return false;
	}

	// $79<Term(^[^string$2<^Type>...])>
	private static boolean typeof_835(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_841(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Ref($2<^Type>)>
	private static boolean typeof_832(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_824(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<Meta($2<^Type>)>
	private static boolean typeof_833(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_824(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^{$2<^Type>...}>
	private static boolean typeof_838(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_842(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 838);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_842(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<^[$2<^Type>,^[$2...]]>
	private static boolean typeof_839(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_843(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 839);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_843(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<Set($86<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_836(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_844(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<Bag($86<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_837(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_844(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<{$2<^Type>...}>
	private static boolean typeof_842(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_824(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $8<[$2<^Type>,^[$2...]]>
	private static boolean typeof_843(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_824(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_845(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $70<^[^string,$2<^Type>]>
	private static boolean typeof_840(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_846(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 840);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_846(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $77<^[^string$2<^Type>...]>
	private static boolean typeof_841(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_847(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 841);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_847(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $69<[^string,$2<^Type>]>
	private static boolean typeof_846(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_824(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$40<^Type>]>
	private static boolean typeof_123(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_848(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 123);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_848(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$40<^Type>]>
	private static boolean typeof_848(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_849(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $40<^Type>
	private static boolean typeof_849(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_850(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 849);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_850(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_850(Automaton.State state, Automaton automaton) {
		return typeof_851(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_851(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_852(state,automaton)
			|| typeof_853(state,automaton)
			|| typeof_854(state,automaton)
			|| typeof_855(state,automaton)
			|| typeof_856(state,automaton)
			|| typeof_86(state,automaton)
			|| typeof_857(state,automaton)
			|| typeof_858(state,automaton)
			|| typeof_859(state,automaton)
			|| typeof_860(state,automaton);
	}

	// $53<Ref($40<^Type>)>
	private static boolean typeof_855(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_849(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<Not($40<^Type>)>
	private static boolean typeof_854(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_849(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<And($43<^{$40<^Type>...}>)>
	private static boolean typeof_853(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_861(data,automaton)) { return true; }
		}
		return false;
	}

	// $45<Or($43<^{$40<^Type>...}>)>
	private static boolean typeof_852(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_861(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_859(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_862(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_858(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_862(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$40<^Type>...])>
	private static boolean typeof_857(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_863(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Meta($40<^Type>)>
	private static boolean typeof_856(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_849(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$40<^Type>...]>
	private static boolean typeof_863(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_864(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 863);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_864(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$40<^Type>...]>
	private static boolean typeof_864(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_849(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_862(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_865(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 862);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_865(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_865(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_849(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_866(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_866(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_867(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 866);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_867(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_867(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_849(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $43<^{$40<^Type>...}>
	private static boolean typeof_861(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_868(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 861);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_868(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $42<{$40<^Type>...}>
	private static boolean typeof_868(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_849(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$40<^Type>,^[$40...]])>
	private static boolean typeof_860(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_869(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$40<^Type>,^[$40...]]>
	private static boolean typeof_869(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_870(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 869);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_870(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$40<^Type>,^[$40...]]>
	private static boolean typeof_870(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_849(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_871(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$40<^Type>...]>
	private static boolean typeof_871(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_872(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 871);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_872(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$40<^Type>...]>
	private static boolean typeof_872(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_849(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<[^string$2<^Type>...]>
	private static boolean typeof_847(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_824(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_725(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_705(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_873(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_873(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_874(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 873);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_874(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_874(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_705(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_604(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_875(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 604);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_875(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_875(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_591(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_480(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_876(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_876(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_877(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 876);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_877(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_877(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_467(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_878(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_878(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_879(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 878);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_879(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_879(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_467(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Proton|Not(^Proton)
	private static boolean typeof_361(Automaton.State state, Automaton automaton) {
		return typeof_66(state,automaton)
			|| typeof_362(state,automaton);
	}

	// $46<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_243(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_880(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_880(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_881(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 880);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_881(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_881(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_882(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_882(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_883(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 882);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_883(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_883(Automaton.State state, Automaton automaton) {
		return typeof_884(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_884(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_885(state,automaton)
			|| typeof_243(state,automaton)
			|| typeof_886(state,automaton)
			|| typeof_887(state,automaton)
			|| typeof_888(state,automaton)
			|| typeof_889(state,automaton)
			|| typeof_890(state,automaton)
			|| typeof_891(state,automaton)
			|| typeof_892(state,automaton)
			|| typeof_893(state,automaton);
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_885(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_880(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($39<^Type>)>
	private static boolean typeof_887(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_882(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($39<^Type>)>
	private static boolean typeof_886(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_882(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_889(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_894(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_888(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_882(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_891(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_895(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_890(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_896(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_896(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_897(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 896);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_897(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_897(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_882(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_893(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_898(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_898(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_899(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 898);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_899(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_899(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_882(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_900(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_900(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_901(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 900);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_901(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_901(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_882(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_892(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_895(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_895(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_902(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 895);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_902(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_902(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_882(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_903(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_903(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_904(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 903);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_904(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_904(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_882(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_894(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_905(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 894);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_905(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_905(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_882(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^[$39<^Type>,^{|^Void$39...|}[^Void$39...]]
	private static boolean typeof_122(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_906(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 122);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_906(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [$39<^Type>,^{|^Void$39...|}[^Void$39...]]
	private static boolean typeof_906(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_908(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Void$39<^Type>...|}[^Void$39<^Type>...]
	private static boolean typeof_908(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_909(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 908);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_909(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Void$39<^Type>...|}[^Void$39<^Type>...]
	private static boolean typeof_909(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_907(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_907(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_910(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 907);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_910(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<Type>
	private static boolean typeof_910(Automaton.State state, Automaton automaton) {
		return typeof_911(state,automaton);
	}

	// $2<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_911(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_912(state,automaton)
			|| typeof_913(state,automaton)
			|| typeof_914(state,automaton)
			|| typeof_915(state,automaton)
			|| typeof_916(state,automaton)
			|| typeof_917(state,automaton)
			|| typeof_918(state,automaton)
			|| typeof_919(state,automaton)
			|| typeof_920(state,automaton)
			|| typeof_921(state,automaton);
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_920(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_922(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_922(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_923(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 922);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_923(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_923(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_924(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_924(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_925(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 924);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_925(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_925(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_921(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_926(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_926(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_927(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 926);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_927(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_927(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_928(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_928(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_929(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 928);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_929(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_929(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $70<Term(^[^string$39<^Type>...])>
	private static boolean typeof_918(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_930(data,automaton)) { return true; }
		}
		return false;
	}

	// $68<^[^string$39<^Type>...]>
	private static boolean typeof_930(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_931(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 930);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_931(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $67<[^string$39<^Type>...]>
	private static boolean typeof_931(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_919(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_922(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Meta($39<^Type>)>
	private static boolean typeof_916(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_907(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_917(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_932(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<^[^string,$39<^Type>]>
	private static boolean typeof_932(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_933(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 932);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_933(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $60<[^string,$39<^Type>]>
	private static boolean typeof_933(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_914(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_907(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($39<^Type>)>
	private static boolean typeof_915(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_907(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_912(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_934(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_934(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_935(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 934);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_935(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_935(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_907(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_913(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_934(data,automaton)) { return true; }
		}
		return false;
	}

	// $86<^[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_844(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_936(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 844);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_936(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $85<[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_936(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_824(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_937(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $82<^{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_937(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_938(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 937);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_938(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $81<{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_938(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_824(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_726(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_939(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 726);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_939(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_939(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_705(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$38<^Type>]>
	private static boolean typeof_607(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_940(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 607);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_940(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$38<^Type>]>
	private static boolean typeof_940(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_483(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_941(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 483);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_941(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_941(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_467(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^Proton)
	private static boolean typeof_362(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_17(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<{$38<^Type>...}>
	private static boolean typeof_240(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_942(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_942(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_943(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 942);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_943(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<Type>
	private static boolean typeof_943(Automaton.State state, Automaton automaton) {
		return typeof_944(state,automaton);
	}

	// $3<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_944(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_238(state,automaton)
			|| typeof_945(state,automaton)
			|| typeof_946(state,automaton)
			|| typeof_947(state,automaton)
			|| typeof_948(state,automaton)
			|| typeof_949(state,automaton)
			|| typeof_950(state,automaton)
			|| typeof_951(state,automaton)
			|| typeof_952(state,automaton)
			|| typeof_953(state,automaton);
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_952(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_954(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_953(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_955(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_954(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_956(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 954);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_956(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_956(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_942(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_957(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_957(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_958(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 957);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_958(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_958(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_942(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_955(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_959(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 955);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_959(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_959(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_942(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_960(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Meta($38<^Type>)>
	private static boolean typeof_948(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_942(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$38<^Type>])>
	private static boolean typeof_949(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_961(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$38<^Type>...])>
	private static boolean typeof_950(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_962(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_951(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_954(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<And($41<^{$38<^Type>...}>)>
	private static boolean typeof_945(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_239(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($38<^Type>)>
	private static boolean typeof_946(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_942(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($38<^Type>)>
	private static boolean typeof_947(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_942(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$38<^Type>...]>
	private static boolean typeof_962(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_963(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 962);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_963(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$38<^Type>...]>
	private static boolean typeof_963(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_942(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$38<^Type>]>
	private static boolean typeof_961(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_964(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 961);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_964(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_960(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_965(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 960);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_965(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_965(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_942(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Meta(^Any),^Meta($43<^Type>)$43...}
	private static boolean typeof_121(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_966(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 121);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_966(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Meta(^Any),^Meta($43<^Type>)$43...}
	private static boolean typeof_966(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_33(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_967(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_968(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_968(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_969(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 968);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_969(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<Type>
	private static boolean typeof_969(Automaton.State state, Automaton automaton) {
		return typeof_970(state,automaton);
	}

	// $6<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_970(Automaton.State state, Automaton automaton) {
		return typeof_471(state,automaton)
			|| typeof_971(state,automaton)
			|| typeof_972(state,automaton)
			|| typeof_973(state,automaton)
			|| typeof_974(state,automaton)
			|| typeof_975(state,automaton)
			|| typeof_976(state,automaton)
			|| typeof_977(state,automaton)
			|| typeof_978(state,automaton)
			|| typeof_979(state,automaton)
			|| typeof_980(state,automaton);
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_978(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_981(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_979(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_981(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_976(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_982(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_977(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_983(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_982(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_984(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 982);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_984(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_984(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_968(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_983(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_985(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 983);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_985(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_985(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_968(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_980(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_986(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_986(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_987(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 986);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_987(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_987(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_968(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_988(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_988(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_989(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 988);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_989(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_989(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_968(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_981(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_990(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 981);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_990(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_990(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_968(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_991(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_991(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_992(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 991);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_992(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_992(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_968(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_971(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_993(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_993(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_994(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 993);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_994(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_994(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_968(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_975(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_968(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($39<^Type>)>
	private static boolean typeof_974(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_968(data,automaton)) { return true; }
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_973(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_968(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_972(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_993(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$54<Meta($39<^Type>)>
	private static boolean typeof_967(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_975(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 967);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_975(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$38<^Type>]>
	private static boolean typeof_964(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_942(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<^[$2<^Type>...]>
	private static boolean typeof_845(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_995(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 845);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_995(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $4<[$2<^Type>...]>
	private static boolean typeof_995(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_824(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_727(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_591(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_606(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_996(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 606);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_996(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_996(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_346(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_482(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_997(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 482);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_997(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_997(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_467(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$40<^Type>,^[$40...]]>
	private static boolean typeof_363(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_998(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 363);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_998(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$40<^Type>,^[$40...]]>
	private static boolean typeof_998(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_999(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$40<^Type>...]>
	private static boolean typeof_999(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1000(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 999);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1000(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$40<^Type>...]>
	private static boolean typeof_1000(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $40<{$38<^Type>...}>
	private static boolean typeof_241(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_759(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_120(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1001(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 120);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1001(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<Type>
	private static boolean typeof_1001(Automaton.State state, Automaton automaton) {
		return typeof_1002(state,automaton);
	}

	// $2<Atom|Or(^{^Type...})|And(^{^Type...})|Not(^Type)|Ref(^Type)|Meta(^Type)|Nominal(^[^string,^Type])|Term(^[^string^Type...])|Set(^[^Type,^{|^Type...|}[^Type...]])|Bag(^[^Type,^{|^Type...|}[^Type...]])|List(^[^Type,^[^Type...]])>
	private static boolean typeof_1002(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton)
			|| typeof_1003(state,automaton)
			|| typeof_1004(state,automaton)
			|| typeof_1005(state,automaton)
			|| typeof_75(state,automaton)
			|| typeof_1006(state,automaton)
			|| typeof_1007(state,automaton)
			|| typeof_1008(state,automaton)
			|| typeof_1009(state,automaton)
			|| typeof_1010(state,automaton)
			|| typeof_1011(state,automaton);
	}

	// $69<Term(^[^string$39<^Type>...])>
	private static boolean typeof_1008(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1012(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_1009(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1013(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_1010(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1013(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_1011(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1014(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<^[^string$39<^Type>...]>
	private static boolean typeof_1012(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1015(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1012);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1015(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_1013(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1016(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1013);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1016(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_1016(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_120(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_1017(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_1017(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1018(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1017);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1018(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_1018(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_120(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_1014(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1019(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1014);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1019(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_1019(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_120(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_1020(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_1020(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1021(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1020);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1021(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_1021(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_120(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<[^string$39<^Type>...]>
	private static boolean typeof_1015(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_120(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $44<Or($42<^{$39<^Type>...}>)>
	private static boolean typeof_1003(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1022(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<^{$39<^Type>...}>
	private static boolean typeof_1022(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1023(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1022);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1023(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $41<{$39<^Type>...}>
	private static boolean typeof_1023(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_120(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $49<Not($39<^Type>)>
	private static boolean typeof_1005(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_120(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<And($42<^{$39<^Type>...}>)>
	private static boolean typeof_1004(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1022(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$39<^Type>])>
	private static boolean typeof_1007(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1024(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$39<^Type>]>
	private static boolean typeof_1024(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_1025(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1024);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_1025(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $59<[^string,$39<^Type>]>
	private static boolean typeof_1025(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_156(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_120(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Meta($39<^Type>)>
	private static boolean typeof_1006(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_120(data,automaton)) { return true; }
		}
		return false;
	}

	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// $3<Not($1<^Type>)>
		Schema.Term("Not",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $6<And($4<^{$1<^Type>...}>)>
		Schema.Term("And",Schema.Set(true)),
		// $6<Or($4<^{$1<^Type>...}>)>
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
		// $3<Ref($1<^Type>)>
		Schema.Term("Ref",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $3<Meta($1<^Type>)>
		Schema.Term("Meta",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $8<Term(^[^string$2<^Type>...])>
		Schema.Term("Term",Schema.List(true,Schema.String)),
		// $8<Nominal(^[^string,$2<^Type>])>
		Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))))),
		// Fun(^[$1<^Type>,$1])
		Schema.Term("Fun",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Any)),
		// $10<Set($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
		Schema.Term("Set",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Bag(true))),
		// $10<Bag($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
		Schema.Term("Bag",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Bag(true))),
		// $10<List(^[$1<^Type>,^[$1...]])>
		Schema.Term("List",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any)),Schema.List(true)))
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
