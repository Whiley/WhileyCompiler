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

	// And({Meta($4<Type> t1), Meta($4<Type> t2), $4<Type> ts...})
	public static boolean reduce_29(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_30(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_30(r7,automaton)) { continue; }
				
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
	public static boolean reduce_31(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_32(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_30(r7,automaton)) { continue; }
				
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
	public static boolean reduce_33(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_30(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			for(int i7=0;i7!=r3.size();++i7) {
				int r7 = r3.get(i7);
				if(i7 == i4 || !typeof_34(r7,automaton)) { continue; }
				
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

	// term $8<Term(^[^string>$2<^Type>...])>
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
	public static boolean reduce_35(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_36(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.List r7 = (Automaton.List) automaton.get(r6);
			int r8 = r7.get(0);
			int r9 = r7.get(1);
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_36(r10,automaton)) { continue; }
				
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

	// term $8<Nominal(^[^string>,$2<^Type>])>
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
	public static boolean reduce_37(int r0, Automaton automaton) {
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
	public static boolean reduce_38(int r0, Automaton automaton) {
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
	public static boolean reduce_40(int r0, Automaton automaton) {
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
	public static boolean reduce_41(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_42(r4,automaton)) { continue; }
			
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
	
				boolean r8 = typeof_43(r5,automaton); // t is ^Proton
				boolean r9 = typeof_44(r5,automaton); // t is ^SetOrBag
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
				boolean r13 = typeof_45(r5,automaton); // t is ^Not(^Proton)
				boolean r14 = typeof_46(r5,automaton); // t is ^Not(^SetOrBag)
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
	public static boolean reduce_47(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_48(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.List r7 = (Automaton.List) automaton.get(r6);
			int r8 = r7.get(0);
			int r9 = r7.get(1);
			Automaton.List r10 = (Automaton.List) automaton.get(r9);
			Automaton.List r11 = r10.sublist(0);
			for(int i12=0;i12!=r3.size();++i12) {
				int r12 = r3.get(i12);
				if(i12 == i4 || !typeof_48(r12,automaton)) { continue; }
				
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
				
				if(typeof_31(i,automaton)) {
					changed |= reduce_31(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_33(i,automaton)) {
					changed |= reduce_33(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_35(i,automaton)) {
					changed |= reduce_35(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_37(i,automaton)) {
					changed |= reduce_37(i,automaton);
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
				
				if(typeof_47(i,automaton)) {
					changed |= reduce_47(i,automaton);
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

	// ^Not(^Any)
	private static boolean typeof_0(int index, Automaton automaton) {
		return typeof_49(automaton.get(index),automaton);
	}

	// ^Not(^Void)
	private static boolean typeof_1(int index, Automaton automaton) {
		return typeof_50(automaton.get(index),automaton);
	}

	// ^Not(^$66<Or($64<^{$38<^Type>...}>)>)
	private static boolean typeof_2(int index, Automaton automaton) {
		return typeof_51(automaton.get(index),automaton);
	}

	// $37<^Type>
	private static boolean typeof_3(int index, Automaton automaton) {
		return typeof_52(automaton.get(index),automaton);
	}

	// ^Not(^$69<And($65<^{$38<^Type>...}>)>)
	private static boolean typeof_4(int index, Automaton automaton) {
		return typeof_53(automaton.get(index),automaton);
	}

	// ^And(^{$38<^Type>})
	private static boolean typeof_5(int index, Automaton automaton) {
		return typeof_54(automaton.get(index),automaton);
	}

	// ^And(^{^And(^{$38<^Type>...}>)>$38...})
	private static boolean typeof_6(int index, Automaton automaton) {
		return typeof_55(automaton.get(index),automaton);
	}

	// ^$69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_7(int index, Automaton automaton) {
		return typeof_56(automaton.get(index),automaton);
	}

	// ^And(^{^Or(^{$39<^Type>...}>)>$39...})
	private static boolean typeof_8(int index, Automaton automaton) {
		return typeof_57(automaton.get(index),automaton);
	}

	// ^$66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_9(int index, Automaton automaton) {
		return typeof_58(automaton.get(index),automaton);
	}

	// ^Or(^{$38<^Type>})
	private static boolean typeof_10(int index, Automaton automaton) {
		return typeof_59(automaton.get(index),automaton);
	}

	// ^Or(^{^Or(^{$38<^Type>...}>)>$38...})
	private static boolean typeof_11(int index, Automaton automaton) {
		return typeof_60(automaton.get(index),automaton);
	}

	// ^And(^{^Void>$39<^Type>...})
	private static boolean typeof_12(int index, Automaton automaton) {
		return typeof_61(automaton.get(index),automaton);
	}

	// ^Void
	private static boolean typeof_13(int index, Automaton automaton) {
		return typeof_62(automaton.get(index),automaton);
	}

	// ^And(^{^Any>$39<^Type>...})
	private static boolean typeof_14(int index, Automaton automaton) {
		return typeof_63(automaton.get(index),automaton);
	}

	// ^Any
	private static boolean typeof_15(int index, Automaton automaton) {
		return typeof_64(automaton.get(index),automaton);
	}

	// ^Proton
	private static boolean typeof_17(int index, Automaton automaton) {
		return typeof_65(automaton.get(index),automaton);
	}

	// ^And(^{^Proton>>,^Proton>>$38<^Type>...})
	private static boolean typeof_16(int index, Automaton automaton) {
		return typeof_66(automaton.get(index),automaton);
	}

	// ^Not(^Proton)
	private static boolean typeof_19(int index, Automaton automaton) {
		return typeof_67(automaton.get(index),automaton);
	}

	// ^And(^{^Proton>>,^Not(^Proton>>)>$39<^Type>...})
	private static boolean typeof_18(int index, Automaton automaton) {
		return typeof_68(automaton.get(index),automaton);
	}

	// ^Or(^{^Void>$39<^Type>...})
	private static boolean typeof_21(int index, Automaton automaton) {
		return typeof_69(automaton.get(index),automaton);
	}

	// ^Or(^{^Any>$39<^Type>...})
	private static boolean typeof_20(int index, Automaton automaton) {
		return typeof_70(automaton.get(index),automaton);
	}

	// ^And(^{^Ref($39<^Type>)>>,^Ref($39)>>$39...})
	private static boolean typeof_23(int index, Automaton automaton) {
		return typeof_71(automaton.get(index),automaton);
	}

	// ^Ref(^Void)
	private static boolean typeof_22(int index, Automaton automaton) {
		return typeof_72(automaton.get(index),automaton);
	}

	// ^Or(^{^Ref(^Any>),^Ref($43<^Type>)>$43...})
	private static boolean typeof_25(int index, Automaton automaton) {
		return typeof_73(automaton.get(index),automaton);
	}

	// ^$42<Ref($38<^Type>)>
	private static boolean typeof_24(int index, Automaton automaton) {
		return typeof_74(automaton.get(index),automaton);
	}

	// ^And(^{^Ref($39<^Type>)>>,^Not(^Ref($39)>>)$39...})
	private static boolean typeof_27(int index, Automaton automaton) {
		return typeof_75(automaton.get(index),automaton);
	}

	// ^Ref(^Any)
	private static boolean typeof_26(int index, Automaton automaton) {
		return typeof_76(automaton.get(index),automaton);
	}

	// ^And(^{^Meta($39<^Type>)>>,^Meta($39)>>$39...})
	private static boolean typeof_29(int index, Automaton automaton) {
		return typeof_77(automaton.get(index),automaton);
	}

	// ^Not(^$42<Ref($38<^Type>)>)
	private static boolean typeof_28(int index, Automaton automaton) {
		return typeof_78(automaton.get(index),automaton);
	}

	// ^Or(^{^Meta(^Any>),^Meta($43<^Type>)>$43...})
	private static boolean typeof_31(int index, Automaton automaton) {
		return typeof_79(automaton.get(index),automaton);
	}

	// ^$45<Meta($38<^Type>)>
	private static boolean typeof_30(int index, Automaton automaton) {
		return typeof_80(automaton.get(index),automaton);
	}

	// ^Not(^$45<Meta($38<^Type>)>)
	private static boolean typeof_34(int index, Automaton automaton) {
		return typeof_81(automaton.get(index),automaton);
	}

	// ^And(^{^Term(^[^string>,$40<^Type>]>)>,^Term(^[^string>,$40]>)>$40...})
	private static boolean typeof_35(int index, Automaton automaton) {
		return typeof_82(automaton.get(index),automaton);
	}

	// ^Meta(^Any)
	private static boolean typeof_32(int index, Automaton automaton) {
		return typeof_83(automaton.get(index),automaton);
	}

	// ^And(^{^Meta($39<^Type>)>>,^Not(^Meta($39)>>)$39...})
	private static boolean typeof_33(int index, Automaton automaton) {
		return typeof_84(automaton.get(index),automaton);
	}

	// ^Set(^[$38<^Type>,^{|^Void>$38...|}[^Void>$38...]])
	private static boolean typeof_38(int index, Automaton automaton) {
		return typeof_85(automaton.get(index),automaton);
	}

	// ^Bag(^[$38<^Type>,^{|^Void>$38...|}[^Void>$38...]])
	private static boolean typeof_39(int index, Automaton automaton) {
		return typeof_86(automaton.get(index),automaton);
	}

	// ^Term($52<^[^string>,$39<^Type>]>)
	private static boolean typeof_36(int index, Automaton automaton) {
		return typeof_87(automaton.get(index),automaton);
	}

	// ^$53<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_37(int index, Automaton automaton) {
		return typeof_88(automaton.get(index),automaton);
	}

	// ^$10<List(^[$1<^Type>,^[$1...]])>
	private static boolean typeof_42(int index, Automaton automaton) {
		return typeof_89(automaton.get(index),automaton);
	}

	// ^Proton
	private static boolean typeof_43(int index, Automaton automaton) {
		return typeof_65(automaton.get(index),automaton);
	}

	// ^$91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_40(int index, Automaton automaton) {
		return typeof_90(automaton.get(index),automaton);
	}

	// ^And(^{$2<^Type>,^List(^[$2,^[$2...]])>$2...})
	private static boolean typeof_41(int index, Automaton automaton) {
		return typeof_91(automaton.get(index),automaton);
	}

	// ^Not(^SetOrBag)
	private static boolean typeof_46(int index, Automaton automaton) {
		return typeof_92(automaton.get(index),automaton);
	}

	// ^And(^{^List(^[^bool,^[$40<^Type>...]>])>,^List(^[^bool,^[$40...]>])>$40...})
	private static boolean typeof_47(int index, Automaton automaton) {
		return typeof_93(automaton.get(index),automaton);
	}

	// ^SetOrBag
	private static boolean typeof_44(int index, Automaton automaton) {
		return typeof_94(automaton.get(index),automaton);
	}

	// ^Not(^Proton)
	private static boolean typeof_45(int index, Automaton automaton) {
		return typeof_95(automaton.get(index),automaton);
	}

	// Not(^$66<Or($64<^{$38<^Type>...}>)>)
	private static boolean typeof_51(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_96(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^Void)
	private static boolean typeof_50(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_13(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^Any)
	private static boolean typeof_49(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_15(data,automaton)) { return true; }
		}
		return false;
	}

	// ^List(^[^bool,$86<^[$39<^Type>...]>])
	private static boolean typeof_48(int index, Automaton automaton) {
		return typeof_97(automaton.get(index),automaton);
	}

	// And(^{^And(^{$38<^Type>...}>)>$38...})
	private static boolean typeof_55(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_98(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{$38<^Type>})
	private static boolean typeof_54(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_99(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^$69<And($65<^{$38<^Type>...}>)>)
	private static boolean typeof_53(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_100(data,automaton)) { return true; }
		}
		return false;
	}

	// $4<Type>
	private static boolean typeof_52(Automaton.State state, Automaton automaton) {
		return typeof_101(state,automaton);
	}

	// Or(^{$38<^Type>})
	private static boolean typeof_59(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_102(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_58(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_103(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Or(^{$39<^Type>...}>)>$39...})
	private static boolean typeof_57(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_104(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_56(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_105(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Any>$39<^Type>...})
	private static boolean typeof_63(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_106(data,automaton)) { return true; }
		}
		return false;
	}

	// Void
	private static boolean typeof_62(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Void) {
			return true;
		}
		return false;
	}

	// And(^{^Void>$39<^Type>...})
	private static boolean typeof_61(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_107(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Or(^{$38<^Type>...}>)>$38...})
	private static boolean typeof_60(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_108(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Proton>>,^Not(^Proton>>)>$39<^Type>...})
	private static boolean typeof_68(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_109(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Void>$39<^Type>...})
	private static boolean typeof_69(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_110(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Any>$39<^Type>...})
	private static boolean typeof_70(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_111(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Ref($39<^Type>)>>,^Ref($39)>>$39...})
	private static boolean typeof_71(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_112(data,automaton)) { return true; }
		}
		return false;
	}

	// Any
	private static boolean typeof_64(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Any) {
			return true;
		}
		return false;
	}

	// Proton
	private static boolean typeof_65(Automaton.State state, Automaton automaton) {
		return typeof_113(state,automaton);
	}

	// And(^{^Proton>>,^Proton>>$38<^Type>...})
	private static boolean typeof_66(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_114(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^Proton)
	private static boolean typeof_67(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_17(data,automaton)) { return true; }
		}
		return false;
	}

	// Ref(^Any)
	private static boolean typeof_76(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_15(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Meta($39<^Type>)>>,^Meta($39)>>$39...})
	private static boolean typeof_77(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_115(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^$42<Ref($38<^Type>)>)
	private static boolean typeof_78(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_116(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Meta(^Any>),^Meta($43<^Type>)>$43...})
	private static boolean typeof_79(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_117(data,automaton)) { return true; }
		}
		return false;
	}

	// Ref(^Void)
	private static boolean typeof_72(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_13(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^Ref(^Any>),^Ref($43<^Type>)>$43...})
	private static boolean typeof_73(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_118(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<Ref($38<^Type>)>
	private static boolean typeof_74(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Ref($39<^Type>)>>,^Not(^Ref($39)>>)$39...})
	private static boolean typeof_75(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_120(data,automaton)) { return true; }
		}
		return false;
	}

	// Set(^[$38<^Type>,^{|^Void>$38...|}[^Void>$38...]])
	private static boolean typeof_85(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_121(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Meta($39<^Type>)>>,^Not(^Meta($39)>>)$39...})
	private static boolean typeof_84(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_122(data,automaton)) { return true; }
		}
		return false;
	}

	// Term($52<^[^string>,$39<^Type>]>)
	private static boolean typeof_87(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_123(data,automaton)) { return true; }
		}
		return false;
	}

	// Bag(^[$38<^Type>,^{|^Void>$38...|}[^Void>$38...]])
	private static boolean typeof_86(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_124(data,automaton)) { return true; }
		}
		return false;
	}

	// Not(^$45<Meta($38<^Type>)>)
	private static boolean typeof_81(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_125(data,automaton)) { return true; }
		}
		return false;
	}

	// $45<Meta($38<^Type>)>
	private static boolean typeof_80(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_126(data,automaton)) { return true; }
		}
		return false;
	}

	// Meta(^Any)
	private static boolean typeof_83(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_15(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Term(^[^string>,$40<^Type>]>)>,^Term(^[^string>,$40]>)>$40...})
	private static boolean typeof_82(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_127(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^List(^[^bool,^[$40<^Type>...]>])>,^List(^[^bool,^[$40...]>])>$40...})
	private static boolean typeof_93(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_128(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^List(^[^bool,^[$40<^Type>...]>])>,^List(^[^bool,^[$40...]>])>$40...}
	private static boolean typeof_128(int index, Automaton automaton) {
		return typeof_129(automaton.get(index),automaton);
	}

	// {^List(^[^bool,^[$40<^Type>...]>])>,^List(^[^bool,^[$40...]>])>$40...}
	private static boolean typeof_129(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_130(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_130(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_131(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_131(int index, Automaton automaton) {
		return typeof_132(automaton.get(index),automaton);
	}

	// ^List(^[^bool,$86<^[$40<^Type>...]>])
	private static boolean typeof_130(int index, Automaton automaton) {
		return typeof_133(automaton.get(index),automaton);
	}

	// List(^[^bool,$86<^[$40<^Type>...]>])
	private static boolean typeof_133(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_134(data,automaton)) { return true; }
		}
		return false;
	}

	// $6<Type>
	private static boolean typeof_132(Automaton.State state, Automaton automaton) {
		return typeof_135(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_135(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_137(state,automaton)
			|| typeof_138(state,automaton)
			|| typeof_139(state,automaton)
			|| typeof_140(state,automaton)
			|| typeof_141(state,automaton)
			|| typeof_142(state,automaton)
			|| typeof_143(state,automaton)
			|| typeof_144(state,automaton)
			|| typeof_145(state,automaton)
			|| typeof_146(state,automaton);
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_137(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_131(data,automaton)) { return true; }
		}
		return false;
	}

	// Atom
	private static boolean typeof_136(Automaton.State state, Automaton automaton) {
		return typeof_147(state,automaton);
	}

	// $47<Meta($39<^Type>)>
	private static boolean typeof_139(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_131(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type>)>
	private static boolean typeof_138(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_131(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_141(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_148(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_140(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_149(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<And($66<^{$39<^Type>...}>)>
	private static boolean typeof_143(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_150(data,automaton)) { return true; }
		}
		return false;
	}

	// $68<Or($66<^{$39<^Type>...}>)>
	private static boolean typeof_142(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_150(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^bool,$86<^[$40<^Type>...]>]
	private static boolean typeof_134(int index, Automaton automaton) {
		return typeof_151(automaton.get(index),automaton);
	}

	// $79<Set($77<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_144(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_152(data,automaton)) { return true; }
		}
		return false;
	}

	// $77<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_152(int index, Automaton automaton) {
		return typeof_153(automaton.get(index),automaton);
	}

	// $76<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_153(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_154(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_154(int index, Automaton automaton) {
		return typeof_155(automaton.get(index),automaton);
	}

	// $72<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_155(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $82<Bag($77<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_145(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_152(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_146(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_156(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_156(int index, Automaton automaton) {
		return typeof_157(automaton.get(index),automaton);
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_157(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_158(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_158(int index, Automaton automaton) {
		return typeof_159(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_159(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^Proton>)|Proton>
	private static boolean typeof_147(Automaton.State state, Automaton automaton) {
		return typeof_95(state,automaton)
			|| typeof_65(state,automaton);
	}

	// $60<^[^string>$39<^Type>...]>
	private static boolean typeof_148(int index, Automaton automaton) {
		return typeof_160(automaton.get(index),automaton);
	}

	// $53<^[^string>,$39<^Type>]>
	private static boolean typeof_149(int index, Automaton automaton) {
		return typeof_161(automaton.get(index),automaton);
	}

	// $66<^{$39<^Type>...}>
	private static boolean typeof_150(int index, Automaton automaton) {
		return typeof_162(automaton.get(index),automaton);
	}

	// [^bool,$86<^[$40<^Type>...]>]
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
					if(!typeof_163(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_158(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^bool
	private static boolean typeof_163(int index, Automaton automaton) {
		return typeof_164(automaton.get(index),automaton);
	}

	// $65<{$39<^Type>...}>
	private static boolean typeof_162(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $52<[^string>,$39<^Type>]>
	private static boolean typeof_161(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<[^string>$39<^Type>...]>
	private static boolean typeof_160(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_131(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^string
	private static boolean typeof_165(int index, Automaton automaton) {
		return typeof_166(automaton.get(index),automaton);
	}

	// string
	private static boolean typeof_166(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_STRING;
	}

	// bool
	private static boolean typeof_164(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_BOOL;
	}

	// Not(^SetOrBag)
	private static boolean typeof_92(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_167(data,automaton)) { return true; }
		}
		return false;
	}

	// ^SetOrBag
	private static boolean typeof_167(int index, Automaton automaton) {
		return typeof_168(automaton.get(index),automaton);
	}

	// SetOrBag
	private static boolean typeof_168(Automaton.State state, Automaton automaton) {
		return typeof_169(state,automaton);
	}

	// $13<Set($11<^[$4<^Type>,^{|$4...|}[$4...]]>)>|Bag($11)>
	private static boolean typeof_169(Automaton.State state, Automaton automaton) {
		return typeof_170(state,automaton)
			|| typeof_171(state,automaton);
	}

	// $81<Bag($9<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_171(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_172(data,automaton)) { return true; }
		}
		return false;
	}

	// $11<Set($9<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_170(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_172(data,automaton)) { return true; }
		}
		return false;
	}

	// $9<^[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_172(int index, Automaton automaton) {
		return typeof_173(automaton.get(index),automaton);
	}

	// $8<[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_173(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_174(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_175(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<^{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_175(int index, Automaton automaton) {
		return typeof_176(automaton.get(index),automaton);
	}

	// $2<^Type>
	private static boolean typeof_174(int index, Automaton automaton) {
		return typeof_177(automaton.get(index),automaton);
	}

	// $4<{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_176(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_174(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $16<Type>
	private static boolean typeof_177(Automaton.State state, Automaton automaton) {
		return typeof_178(state,automaton);
	}

	// $13<Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_178(Automaton.State state, Automaton automaton) {
		return typeof_170(state,automaton)
			|| typeof_136(state,automaton)
			|| typeof_179(state,automaton)
			|| typeof_180(state,automaton)
			|| typeof_181(state,automaton)
			|| typeof_182(state,automaton)
			|| typeof_183(state,automaton)
			|| typeof_184(state,automaton)
			|| typeof_185(state,automaton)
			|| typeof_171(state,automaton)
			|| typeof_186(state,automaton);
	}

	// $91<List(^[$2<^Type>,^[$2...]])>
	private static boolean typeof_186(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_187(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$2<^Type>,^[$2...]]>
	private static boolean typeof_187(int index, Automaton automaton) {
		return typeof_188(automaton.get(index),automaton);
	}

	// $75<Or($73<^{$2<^Type>...}>)>
	private static boolean typeof_184(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_189(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<And($73<^{$2<^Type>...}>)>
	private static boolean typeof_185(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_189(data,automaton)) { return true; }
		}
		return false;
	}

	// $88<[$2<^Type>,^[$2...]]>
	private static boolean typeof_188(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_174(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_190(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$2<^Type>...]>
	private static boolean typeof_190(int index, Automaton automaton) {
		return typeof_191(automaton.get(index),automaton);
	}

	// $84<[$2<^Type>...]>
	private static boolean typeof_191(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_174(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<^{$2<^Type>...}>
	private static boolean typeof_189(int index, Automaton automaton) {
		return typeof_192(automaton.get(index),automaton);
	}

	// $49<Not($2<^Type>)>
	private static boolean typeof_179(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_174(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Nominal(^[^string>,$2<^Type>])>
	private static boolean typeof_182(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_193(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<Term(^[^string>$2<^Type>...])>
	private static boolean typeof_183(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_194(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<Ref($2<^Type>)>
	private static boolean typeof_180(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_174(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Meta($2<^Type>)>
	private static boolean typeof_181(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_174(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<^[^string>,$2<^Type>]>
	private static boolean typeof_193(int index, Automaton automaton) {
		return typeof_195(automaton.get(index),automaton);
	}

	// $72<{$2<^Type>...}>
	private static boolean typeof_192(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_174(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<[^string>,$2<^Type>]>
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
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_174(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $68<^[^string>$2<^Type>...]>
	private static boolean typeof_194(int index, Automaton automaton) {
		return typeof_196(automaton.get(index),automaton);
	}

	// $67<[^string>$2<^Type>...]>
	private static boolean typeof_196(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_174(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^Proton)
	private static boolean typeof_95(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_43(data,automaton)) { return true; }
		}
		return false;
	}

	// SetOrBag
	private static boolean typeof_94(Automaton.State state, Automaton automaton) {
		return typeof_197(state,automaton);
	}

	// $12<Set($10<^[$3<^Type>,^{|$3...|}[$3...]]>)>|Bag($10)>
	private static boolean typeof_197(Automaton.State state, Automaton automaton) {
		return typeof_198(state,automaton)
			|| typeof_199(state,automaton);
	}

	// $81<Bag($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_199(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_200(data,automaton)) { return true; }
		}
		return false;
	}

	// $8<^[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_200(int index, Automaton automaton) {
		return typeof_201(automaton.get(index),automaton);
	}

	// $7<[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_201(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_202(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_203(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $4<^{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_203(int index, Automaton automaton) {
		return typeof_204(automaton.get(index),automaton);
	}

	// $3<{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_204(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_202(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $1<^Type>
	private static boolean typeof_202(int index, Automaton automaton) {
		return typeof_205(automaton.get(index),automaton);
	}

	// $15<Type>
	private static boolean typeof_205(Automaton.State state, Automaton automaton) {
		return typeof_206(state,automaton);
	}

	// $12<Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_206(Automaton.State state, Automaton automaton) {
		return typeof_198(state,automaton)
			|| typeof_136(state,automaton)
			|| typeof_207(state,automaton)
			|| typeof_208(state,automaton)
			|| typeof_209(state,automaton)
			|| typeof_210(state,automaton)
			|| typeof_211(state,automaton)
			|| typeof_212(state,automaton)
			|| typeof_213(state,automaton)
			|| typeof_199(state,automaton)
			|| typeof_214(state,automaton);
	}

	// $49<Not($1<^Type>)>
	private static boolean typeof_207(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_202(data,automaton)) { return true; }
		}
		return false;
	}

	// $10<Set($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_198(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_200(data,automaton)) { return true; }
		}
		return false;
	}

	// $75<Or($73<^{$1<^Type>...}>)>
	private static boolean typeof_212(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_215(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<And($73<^{$1<^Type>...}>)>
	private static boolean typeof_213(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_215(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$1<^Type>,^[$1...]])>
	private static boolean typeof_214(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_216(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$1<^Type>,^[$1...]]>
	private static boolean typeof_216(int index, Automaton automaton) {
		return typeof_217(automaton.get(index),automaton);
	}

	// $88<[$1<^Type>,^[$1...]]>
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
					if(!typeof_202(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_218(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$1<^Type>...]>
	private static boolean typeof_218(int index, Automaton automaton) {
		return typeof_219(automaton.get(index),automaton);
	}

	// $84<[$1<^Type>...]>
	private static boolean typeof_219(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_202(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<^{$1<^Type>...}>
	private static boolean typeof_215(int index, Automaton automaton) {
		return typeof_220(automaton.get(index),automaton);
	}

	// $72<{$1<^Type>...}>
	private static boolean typeof_220(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_202(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $52<Ref($1<^Type>)>
	private static boolean typeof_208(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_202(data,automaton)) { return true; }
		}
		return false;
	}

	// $10<List(^[$1<^Type>,^[$1...]])>
	private static boolean typeof_89(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_221(data,automaton)) { return true; }
		}
		return false;
	}

	// $8<^[$1<^Type>,^[$1...]]>
	private static boolean typeof_221(int index, Automaton automaton) {
		return typeof_222(automaton.get(index),automaton);
	}

	// $7<[$1<^Type>,^[$1...]]>
	private static boolean typeof_222(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_223(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_224(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $1<^Type>
	private static boolean typeof_223(int index, Automaton automaton) {
		return typeof_225(automaton.get(index),automaton);
	}

	// $55<Meta($1<^Type>)>
	private static boolean typeof_209(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_202(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_88(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_226(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Nominal(^[^string>,$1<^Type>])>
	private static boolean typeof_210(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_227(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{$2<^Type>,^List(^[$2,^[$2...]])>$2...})
	private static boolean typeof_91(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_228(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<Term(^[^string>$1<^Type>...])>
	private static boolean typeof_211(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_229(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_90(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_230(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$38<^Type>}
	private static boolean typeof_102(int index, Automaton automaton) {
		return typeof_231(automaton.get(index),automaton);
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_103(int index, Automaton automaton) {
		return typeof_232(automaton.get(index),automaton);
	}

	// ^$69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_100(int index, Automaton automaton) {
		return typeof_233(automaton.get(index),automaton);
	}

	// $1<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_101(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_234(state,automaton)
			|| typeof_235(state,automaton)
			|| typeof_236(state,automaton)
			|| typeof_237(state,automaton)
			|| typeof_238(state,automaton)
			|| typeof_239(state,automaton)
			|| typeof_240(state,automaton)
			|| typeof_241(state,automaton)
			|| typeof_242(state,automaton)
			|| typeof_243(state,automaton);
	}

	// $66<Or($64<^{$37<^Type>...}>)>
	private static boolean typeof_239(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_244(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Term(^[^string>$37<^Type>...])>
	private static boolean typeof_238(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_245(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Nominal(^[^string>,$37<^Type>])>
	private static boolean typeof_237(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_246(data,automaton)) { return true; }
		}
		return false;
	}

	// $45<Meta($37<^Type>)>
	private static boolean typeof_236(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<Ref($37<^Type>)>
	private static boolean typeof_235(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^And(^{$38<^Type>...}>)>$38...}
	private static boolean typeof_98(int index, Automaton automaton) {
		return typeof_247(automaton.get(index),automaton);
	}

	// $39<Not($37<^Type>)>
	private static boolean typeof_234(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$38<^Type>}
	private static boolean typeof_99(int index, Automaton automaton) {
		return typeof_248(automaton.get(index),automaton);
	}

	// $69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_233(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_249(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_96(int index, Automaton automaton) {
		return typeof_250(automaton.get(index),automaton);
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_232(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_251(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// List(^[^bool,$86<^[$39<^Type>...]>])
	private static boolean typeof_97(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_252(data,automaton)) { return true; }
		}
		return false;
	}

	// {$38<^Type>}
	private static boolean typeof_231(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_251(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^{^Void>$39<^Type>...}
	private static boolean typeof_110(int index, Automaton automaton) {
		return typeof_253(automaton.get(index),automaton);
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_230(int index, Automaton automaton) {
		return typeof_254(automaton.get(index),automaton);
	}

	// ^{^Any>$39<^Type>...}
	private static boolean typeof_111(int index, Automaton automaton) {
		return typeof_255(automaton.get(index),automaton);
	}

	// $68<^[^string>$1<^Type>...]>
	private static boolean typeof_229(int index, Automaton automaton) {
		return typeof_256(automaton.get(index),automaton);
	}

	// $67<[^string>$1<^Type>...]>
	private static boolean typeof_256(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_202(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Or(^{$38<^Type>...}>)>$38...}
	private static boolean typeof_108(int index, Automaton automaton) {
		return typeof_257(automaton.get(index),automaton);
	}

	// {^Or(^{$38<^Type>...}>)>$38...}
	private static boolean typeof_257(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_251(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^{$2<^Type>,^List(^[$2,^[$2...]])>$2...}
	private static boolean typeof_228(int index, Automaton automaton) {
		return typeof_258(automaton.get(index),automaton);
	}

	// {$2<^Type>,^List(^[$2,^[$2...]])>$2...}
	private static boolean typeof_258(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_259(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_260(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_259(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $2<^Type>
	private static boolean typeof_259(int index, Automaton automaton) {
		return typeof_261(automaton.get(index),automaton);
	}

	// ^$11<List(^[$2<^Type>,^[$2...]])>
	private static boolean typeof_260(int index, Automaton automaton) {
		return typeof_262(automaton.get(index),automaton);
	}

	// $11<List(^[$2<^Type>,^[$2...]])>
	private static boolean typeof_262(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_263(data,automaton)) { return true; }
		}
		return false;
	}

	// $9<^[$2<^Type>,^[$2...]]>
	private static boolean typeof_263(int index, Automaton automaton) {
		return typeof_264(automaton.get(index),automaton);
	}

	// $16<Type>
	private static boolean typeof_261(Automaton.State state, Automaton automaton) {
		return typeof_265(state,automaton);
	}

	// $8<[$2<^Type>,^[$2...]]>
	private static boolean typeof_264(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_259(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_266(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<^[$2<^Type>...]>
	private static boolean typeof_266(int index, Automaton automaton) {
		return typeof_267(automaton.get(index),automaton);
	}

	// $4<[$2<^Type>...]>
	private static boolean typeof_267(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_259(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $13<List(^[^Type>,^[^Type>...]])|Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)>
	private static boolean typeof_265(Automaton.State state, Automaton automaton) {
		return typeof_262(state,automaton)
			|| typeof_136(state,automaton)
			|| typeof_268(state,automaton)
			|| typeof_269(state,automaton)
			|| typeof_270(state,automaton)
			|| typeof_271(state,automaton)
			|| typeof_272(state,automaton)
			|| typeof_273(state,automaton)
			|| typeof_274(state,automaton)
			|| typeof_275(state,automaton)
			|| typeof_276(state,automaton);
	}

	// $88<Set($86<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_275(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_277(data,automaton)) { return true; }
		}
		return false;
	}

	// $79<And($75<^{$2<^Type>...}>)>
	private static boolean typeof_274(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_278(data,automaton)) { return true; }
		}
		return false;
	}

	// $77<Or($75<^{$2<^Type>...}>)>
	private static boolean typeof_273(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_278(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<Term(^[^string>$2<^Type>...])>
	private static boolean typeof_272(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_279(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<^[^string>$2<^Type>...]>
	private static boolean typeof_279(int index, Automaton automaton) {
		return typeof_280(automaton.get(index),automaton);
	}

	// $75<^{$2<^Type>...}>
	private static boolean typeof_278(int index, Automaton automaton) {
		return typeof_281(automaton.get(index),automaton);
	}

	// $86<^[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_277(int index, Automaton automaton) {
		return typeof_282(automaton.get(index),automaton);
	}

	// $91<Bag($86<^[$2<^Type>,^{|$2...|}[$2...]]>)>
	private static boolean typeof_276(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_277(data,automaton)) { return true; }
		}
		return false;
	}

	// $85<[$2<^Type>,^{|$2...|}[$2...]]>
	private static boolean typeof_282(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_259(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_283(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $82<^{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_283(int index, Automaton automaton) {
		return typeof_284(automaton.get(index),automaton);
	}

	// $74<{$2<^Type>...}>
	private static boolean typeof_281(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_259(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $68<[^string>$2<^Type>...]>
	private static boolean typeof_280(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_259(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<{|$2<^Type>...|}[$2<^Type>...]>
	private static boolean typeof_284(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_259(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $56<Meta($2<^Type>)>
	private static boolean typeof_270(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_259(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<Nominal(^[^string>,$2<^Type>])>
	private static boolean typeof_271(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_285(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<^[^string>,$2<^Type>]>
	private static boolean typeof_285(int index, Automaton automaton) {
		return typeof_286(automaton.get(index),automaton);
	}

	// $61<[^string>,$2<^Type>]>
	private static boolean typeof_286(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_259(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $50<Not($2<^Type>)>
	private static boolean typeof_268(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_259(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Ref($2<^Type>)>
	private static boolean typeof_269(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_259(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Proton>>,^Not(^Proton>>)>$39<^Type>...}
	private static boolean typeof_109(int index, Automaton automaton) {
		return typeof_287(automaton.get(index),automaton);
	}

	// {^Proton>>,^Not(^Proton>>)>$39<^Type>...}
	private static boolean typeof_287(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_288(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_289(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^Not(^Proton)
	private static boolean typeof_288(int index, Automaton automaton) {
		return typeof_290(automaton.get(index),automaton);
	}

	// $38<^Type>
	private static boolean typeof_289(int index, Automaton automaton) {
		return typeof_291(automaton.get(index),automaton);
	}

	// Not(^Proton)
	private static boolean typeof_290(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_17(data,automaton)) { return true; }
		}
		return false;
	}

	// $32<Type>
	private static boolean typeof_291(Automaton.State state, Automaton automaton) {
		return typeof_292(state,automaton);
	}

	// $29<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_292(Automaton.State state, Automaton automaton) {
		return typeof_293(state,automaton)
			|| typeof_294(state,automaton)
			|| typeof_295(state,automaton)
			|| typeof_296(state,automaton)
			|| typeof_297(state,automaton)
			|| typeof_298(state,automaton)
			|| typeof_299(state,automaton)
			|| typeof_300(state,automaton)
			|| typeof_301(state,automaton)
			|| typeof_302(state,automaton)
			|| typeof_303(state,automaton);
	}

	// Atom
	private static boolean typeof_293(Automaton.State state, Automaton automaton) {
		return typeof_304(state,automaton);
	}

	// Proton>|Not(^Proton>)
	private static boolean typeof_304(Automaton.State state, Automaton automaton) {
		return typeof_65(state,automaton)
			|| typeof_290(state,automaton);
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_294(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_289(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_295(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_289(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_296(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_289(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_297(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_305(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_305(int index, Automaton automaton) {
		return typeof_306(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_306(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_289(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_298(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_307(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_307(int index, Automaton automaton) {
		return typeof_308(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_308(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_289(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_299(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_309(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_309(int index, Automaton automaton) {
		return typeof_310(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_310(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_289(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_300(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_309(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_301(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_311(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_311(int index, Automaton automaton) {
		return typeof_312(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_312(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_289(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_313(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_313(int index, Automaton automaton) {
		return typeof_314(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_314(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_289(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_302(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_311(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_303(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_315(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_315(int index, Automaton automaton) {
		return typeof_316(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_316(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_289(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_317(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_317(int index, Automaton automaton) {
		return typeof_318(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_318(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_289(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<^[^string>,$1<^Type>]>
	private static boolean typeof_227(int index, Automaton automaton) {
		return typeof_319(automaton.get(index),automaton);
	}

	// $60<[^string>,$1<^Type>]>
	private static boolean typeof_319(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_202(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Any>$39<^Type>...}
	private static boolean typeof_106(int index, Automaton automaton) {
		return typeof_320(automaton.get(index),automaton);
	}

	// {^Any>$39<^Type>...}
	private static boolean typeof_320(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_321(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_321(int index, Automaton automaton) {
		return typeof_322(automaton.get(index),automaton);
	}

	// $8<Type>
	private static boolean typeof_322(Automaton.State state, Automaton automaton) {
		return typeof_323(state,automaton);
	}

	// $5<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_323(Automaton.State state, Automaton automaton) {
		return typeof_324(state,automaton)
			|| typeof_325(state,automaton)
			|| typeof_326(state,automaton)
			|| typeof_327(state,automaton)
			|| typeof_328(state,automaton)
			|| typeof_329(state,automaton)
			|| typeof_330(state,automaton)
			|| typeof_331(state,automaton)
			|| typeof_332(state,automaton)
			|| typeof_333(state,automaton)
			|| typeof_334(state,automaton);
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_326(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_321(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_327(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_321(data,automaton)) { return true; }
		}
		return false;
	}

	// Atom
	private static boolean typeof_324(Automaton.State state, Automaton automaton) {
		return typeof_335(state,automaton);
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_325(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_321(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_334(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_336(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_336(int index, Automaton automaton) {
		return typeof_337(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_337(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_321(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_338(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_338(int index, Automaton automaton) {
		return typeof_339(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_339(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_321(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^Proton>)|Proton>
	private static boolean typeof_335(Automaton.State state, Automaton automaton) {
		return typeof_340(state,automaton)
			|| typeof_65(state,automaton);
	}

	// Not(^Proton)
	private static boolean typeof_340(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_341(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton
	private static boolean typeof_341(int index, Automaton automaton) {
		return typeof_65(automaton.get(index),automaton);
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_332(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_342(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_342(int index, Automaton automaton) {
		return typeof_343(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_343(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_321(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_344(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_344(int index, Automaton automaton) {
		return typeof_345(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_345(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_321(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_333(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_342(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_330(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_346(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_346(int index, Automaton automaton) {
		return typeof_347(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_347(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_321(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_331(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_346(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_328(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_348(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_348(int index, Automaton automaton) {
		return typeof_349(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_349(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_321(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_329(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_350(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_350(int index, Automaton automaton) {
		return typeof_351(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_351(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_321(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<^[^string>,$39<^Type>]>
	private static boolean typeof_226(int index, Automaton automaton) {
		return typeof_352(automaton.get(index),automaton);
	}

	// ^{^Void>$39<^Type>...}
	private static boolean typeof_107(int index, Automaton automaton) {
		return typeof_353(automaton.get(index),automaton);
	}

	// $15<Type>
	private static boolean typeof_225(Automaton.State state, Automaton automaton) {
		return typeof_354(state,automaton);
	}

	// ^{^Or(^{$39<^Type>...}>)>$39...}
	private static boolean typeof_104(int index, Automaton automaton) {
		return typeof_355(automaton.get(index),automaton);
	}

	// $4<^[$1<^Type>...]>
	private static boolean typeof_224(int index, Automaton automaton) {
		return typeof_356(automaton.get(index),automaton);
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_105(int index, Automaton automaton) {
		return typeof_357(automaton.get(index),automaton);
	}

	// $3<[$1<^Type>...]>
	private static boolean typeof_356(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_223(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $88<[$38<^Type>,^[$38...]]>
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
					if(!typeof_358(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_359(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_119(int index, Automaton automaton) {
		return typeof_360(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_357(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_361(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {^Any>$39<^Type>...}
	private static boolean typeof_255(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_362(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^{^Ref(^Any>),^Ref($43<^Type>)>$43...}
	private static boolean typeof_118(int index, Automaton automaton) {
		return typeof_363(automaton.get(index),automaton);
	}

	// $38<^Type>
	private static boolean typeof_358(int index, Automaton automaton) {
		return typeof_364(automaton.get(index),automaton);
	}

	// ^[^bool,$86<^[$39<^Type>...]>]
	private static boolean typeof_252(int index, Automaton automaton) {
		return typeof_365(automaton.get(index),automaton);
	}

	// ^{^Meta(^Any>),^Meta($43<^Type>)>$43...}
	private static boolean typeof_117(int index, Automaton automaton) {
		return typeof_366(automaton.get(index),automaton);
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_359(int index, Automaton automaton) {
		return typeof_367(automaton.get(index),automaton);
	}

	// {^Void>$39<^Type>...}
	private static boolean typeof_253(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_368(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_368(int index, Automaton automaton) {
		return typeof_369(automaton.get(index),automaton);
	}

	// $8<Type>
	private static boolean typeof_369(Automaton.State state, Automaton automaton) {
		return typeof_370(state,automaton);
	}

	// $5<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_370(Automaton.State state, Automaton automaton) {
		return typeof_371(state,automaton)
			|| typeof_372(state,automaton)
			|| typeof_373(state,automaton)
			|| typeof_374(state,automaton)
			|| typeof_375(state,automaton)
			|| typeof_376(state,automaton)
			|| typeof_377(state,automaton)
			|| typeof_378(state,automaton)
			|| typeof_379(state,automaton)
			|| typeof_380(state,automaton)
			|| typeof_381(state,automaton);
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_373(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_368(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_372(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_368(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_375(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_382(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_374(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_368(data,automaton)) { return true; }
		}
		return false;
	}

	// Atom
	private static boolean typeof_371(Automaton.State state, Automaton automaton) {
		return typeof_383(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_381(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_384(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_384(int index, Automaton automaton) {
		return typeof_385(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_385(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_368(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_386(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_386(int index, Automaton automaton) {
		return typeof_387(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_387(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_368(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_380(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_388(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_388(int index, Automaton automaton) {
		return typeof_389(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_389(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_368(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_390(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_390(int index, Automaton automaton) {
		return typeof_391(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_391(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_368(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Not(^Proton>)|Proton>
	private static boolean typeof_383(Automaton.State state, Automaton automaton) {
		return typeof_392(state,automaton)
			|| typeof_393(state,automaton);
	}

	// Proton
	private static boolean typeof_393(Automaton.State state, Automaton automaton) {
		return typeof_394(state,automaton);
	}

	// Void|Any|Bool|Int|Real|String
	private static boolean typeof_394(Automaton.State state, Automaton automaton) {
		return typeof_62(state,automaton)
			|| typeof_64(state,automaton)
			|| typeof_395(state,automaton)
			|| typeof_396(state,automaton)
			|| typeof_397(state,automaton)
			|| typeof_398(state,automaton);
	}

	// Bool
	private static boolean typeof_395(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bool) {
			return true;
		}
		return false;
	}

	// Not(^Proton)
	private static boolean typeof_392(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_399(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton
	private static boolean typeof_399(int index, Automaton automaton) {
		return typeof_393(automaton.get(index),automaton);
	}

	// String
	private static boolean typeof_398(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_String) {
			return true;
		}
		return false;
	}

	// Real
	private static boolean typeof_397(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Real) {
			return true;
		}
		return false;
	}

	// Int
	private static boolean typeof_396(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Int) {
			return true;
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_382(int index, Automaton automaton) {
		return typeof_400(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_400(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_368(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_377(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_401(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_401(int index, Automaton automaton) {
		return typeof_402(automaton.get(index),automaton);
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_402(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_368(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_376(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_403(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_403(int index, Automaton automaton) {
		return typeof_404(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_404(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_368(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_379(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_388(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_378(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_401(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$42<Ref($38<^Type>)>
	private static boolean typeof_116(int index, Automaton automaton) {
		return typeof_405(automaton.get(index),automaton);
	}

	// $42<Ref($38<^Type>)>
	private static boolean typeof_405(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_406(data,automaton)) { return true; }
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_406(int index, Automaton automaton) {
		return typeof_407(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_407(Automaton.State state, Automaton automaton) {
		return typeof_408(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_408(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_409(state,automaton)
			|| typeof_405(state,automaton)
			|| typeof_410(state,automaton)
			|| typeof_411(state,automaton)
			|| typeof_412(state,automaton)
			|| typeof_413(state,automaton)
			|| typeof_414(state,automaton)
			|| typeof_415(state,automaton)
			|| typeof_416(state,automaton)
			|| typeof_417(state,automaton);
	}

	// $45<Meta($38<^Type>)>
	private static boolean typeof_410(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_406(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_411(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_418(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_409(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_406(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_414(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_419(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_415(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_420(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_412(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_421(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_413(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_419(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_417(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_422(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_416(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_420(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_419(int index, Automaton automaton) {
		return typeof_423(automaton.get(index),automaton);
	}

	// $51<^[^string>,$38<^Type>]>
	private static boolean typeof_418(int index, Automaton automaton) {
		return typeof_424(automaton.get(index),automaton);
	}

	// $50<[^string>,$38<^Type>]>
	private static boolean typeof_424(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_406(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $58<^[^string>$38<^Type>...]>
	private static boolean typeof_421(int index, Automaton automaton) {
		return typeof_425(automaton.get(index),automaton);
	}

	// $57<[^string>$38<^Type>...]>
	private static boolean typeof_425(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_406(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_420(int index, Automaton automaton) {
		return typeof_426(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_426(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_406(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_427(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_427(int index, Automaton automaton) {
		return typeof_428(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_428(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_406(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_423(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_406(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_422(int index, Automaton automaton) {
		return typeof_429(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
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
					if(!typeof_406(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_430(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_430(int index, Automaton automaton) {
		return typeof_431(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_431(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_406(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $50<[^string>,$39<^Type>]>
	private static boolean typeof_352(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_432(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_432(int index, Automaton automaton) {
		return typeof_433(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_433(Automaton.State state, Automaton automaton) {
		return typeof_434(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_434(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_435(state,automaton)
			|| typeof_436(state,automaton)
			|| typeof_437(state,automaton)
			|| typeof_88(state,automaton)
			|| typeof_438(state,automaton)
			|| typeof_439(state,automaton)
			|| typeof_440(state,automaton)
			|| typeof_441(state,automaton)
			|| typeof_442(state,automaton)
			|| typeof_443(state,automaton);
	}

	// $69<And($64<^{$39<^Type>...}>)>
	private static boolean typeof_440(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_444(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_441(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_445(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_442(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_445(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_443(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_446(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<^{$39<^Type>...}>
	private static boolean typeof_444(int index, Automaton automaton) {
		return typeof_447(automaton.get(index),automaton);
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_445(int index, Automaton automaton) {
		return typeof_448(automaton.get(index),automaton);
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_446(int index, Automaton automaton) {
		return typeof_449(automaton.get(index),automaton);
	}

	// $63<{$39<^Type>...}>
	private static boolean typeof_447(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_432(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_435(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_432(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type>)>
	private static boolean typeof_436(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_432(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<Meta($39<^Type>)>
	private static boolean typeof_437(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_432(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_438(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_450(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<Or($64<^{$39<^Type>...}>)>
	private static boolean typeof_439(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_444(data,automaton)) { return true; }
		}
		return false;
	}

	// $58<^[^string>$39<^Type>...]>
	private static boolean typeof_450(int index, Automaton automaton) {
		return typeof_451(automaton.get(index),automaton);
	}

	// $57<[^string>$39<^Type>...]>
	private static boolean typeof_451(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_432(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_449(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_432(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_452(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_452(int index, Automaton automaton) {
		return typeof_453(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_453(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_432(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_448(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_432(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_454(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_454(int index, Automaton automaton) {
		return typeof_455(automaton.get(index),automaton);
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_455(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_432(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_250(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_456(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_456(int index, Automaton automaton) {
		return typeof_457(automaton.get(index),automaton);
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_457(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_458(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_458(int index, Automaton automaton) {
		return typeof_459(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_459(Automaton.State state, Automaton automaton) {
		return typeof_460(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_460(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_461(state,automaton)
			|| typeof_462(state,automaton)
			|| typeof_463(state,automaton)
			|| typeof_464(state,automaton)
			|| typeof_465(state,automaton)
			|| typeof_250(state,automaton)
			|| typeof_466(state,automaton)
			|| typeof_467(state,automaton)
			|| typeof_468(state,automaton)
			|| typeof_469(state,automaton);
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_468(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_470(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_470(int index, Automaton automaton) {
		return typeof_471(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_471(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_458(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_472(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_472(int index, Automaton automaton) {
		return typeof_473(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_473(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_458(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_469(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_474(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_474(int index, Automaton automaton) {
		return typeof_475(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_475(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_458(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_476(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_476(int index, Automaton automaton) {
		return typeof_477(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_477(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_458(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_466(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_456(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_467(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_470(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_464(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_478(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_478(int index, Automaton automaton) {
		return typeof_479(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_479(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_458(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_465(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_480(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_463(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_458(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_462(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_458(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_461(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_458(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Meta($39<^Type>)>>,^Meta($39)>>$39...}
	private static boolean typeof_115(int index, Automaton automaton) {
		return typeof_481(automaton.get(index),automaton);
	}

	// {^Void>$39<^Type>...}
	private static boolean typeof_353(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_482(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_251(int index, Automaton automaton) {
		return typeof_483(automaton.get(index),automaton);
	}

	// ^{^Proton>>,^Proton>>$38<^Type>...}
	private static boolean typeof_114(int index, Automaton automaton) {
		return typeof_484(automaton.get(index),automaton);
	}

	// $12<List(^[^Type>,^[^Type>...]])|Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)>
	private static boolean typeof_354(Automaton.State state, Automaton automaton) {
		return typeof_89(state,automaton)
			|| typeof_136(state,automaton)
			|| typeof_485(state,automaton)
			|| typeof_486(state,automaton)
			|| typeof_487(state,automaton)
			|| typeof_488(state,automaton)
			|| typeof_489(state,automaton)
			|| typeof_490(state,automaton)
			|| typeof_491(state,automaton)
			|| typeof_492(state,automaton)
			|| typeof_493(state,automaton);
	}

	// $91<Bag($86<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_493(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_494(data,automaton)) { return true; }
		}
		return false;
	}

	// $88<Set($86<^[$1<^Type>,^{|$1...|}[$1...]]>)>
	private static boolean typeof_492(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_494(data,automaton)) { return true; }
		}
		return false;
	}

	// $86<^[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_494(int index, Automaton automaton) {
		return typeof_495(automaton.get(index),automaton);
	}

	// $85<[$1<^Type>,^{|$1...|}[$1...]]>
	private static boolean typeof_495(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_223(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_496(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $82<^{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_496(int index, Automaton automaton) {
		return typeof_497(automaton.get(index),automaton);
	}

	// $81<{|$1<^Type>...|}[$1<^Type>...]>
	private static boolean typeof_497(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_223(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $70<Term(^[^string>$1<^Type>...])>
	private static boolean typeof_489(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_498(data,automaton)) { return true; }
		}
		return false;
	}

	// $68<^[^string>$1<^Type>...]>
	private static boolean typeof_498(int index, Automaton automaton) {
		return typeof_499(automaton.get(index),automaton);
	}

	// $67<[^string>$1<^Type>...]>
	private static boolean typeof_499(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_223(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $63<Nominal(^[^string>,$1<^Type>])>
	private static boolean typeof_488(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_500(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<^[^string>,$1<^Type>]>
	private static boolean typeof_500(int index, Automaton automaton) {
		return typeof_501(automaton.get(index),automaton);
	}

	// $60<[^string>,$1<^Type>]>
	private static boolean typeof_501(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_223(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $79<And($74<^{$1<^Type>...}>)>
	private static boolean typeof_491(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_502(data,automaton)) { return true; }
		}
		return false;
	}

	// $74<^{$1<^Type>...}>
	private static boolean typeof_502(int index, Automaton automaton) {
		return typeof_503(automaton.get(index),automaton);
	}

	// $73<{$1<^Type>...}>
	private static boolean typeof_503(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_223(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {$38<^Type>}
	private static boolean typeof_248(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_361(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// Any|Void|Bool|Int|Real|String
	private static boolean typeof_113(Automaton.State state, Automaton automaton) {
		return typeof_64(state,automaton)
			|| typeof_62(state,automaton)
			|| typeof_395(state,automaton)
			|| typeof_396(state,automaton)
			|| typeof_397(state,automaton)
			|| typeof_398(state,automaton);
	}

	// $76<Or($74<^{$1<^Type>...}>)>
	private static boolean typeof_490(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_502(data,automaton)) { return true; }
		}
		return false;
	}

	// {^Or(^{$39<^Type>...}>)>$39...}
	private static boolean typeof_355(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_504(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_505(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^$67<Or($65<^{$39<^Type>...}>)>
	private static boolean typeof_504(int index, Automaton automaton) {
		return typeof_506(automaton.get(index),automaton);
	}

	// $39<^Type>
	private static boolean typeof_505(int index, Automaton automaton) {
		return typeof_507(automaton.get(index),automaton);
	}

	// $67<Or($65<^{$39<^Type>...}>)>
	private static boolean typeof_506(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_508(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$39<^Type>...}>
	private static boolean typeof_508(int index, Automaton automaton) {
		return typeof_509(automaton.get(index),automaton);
	}

	// $64<{$39<^Type>...}>
	private static boolean typeof_509(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_505(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $6<Type>
	private static boolean typeof_507(Automaton.State state, Automaton automaton) {
		return typeof_510(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_510(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_511(state,automaton)
			|| typeof_512(state,automaton)
			|| typeof_513(state,automaton)
			|| typeof_514(state,automaton)
			|| typeof_515(state,automaton)
			|| typeof_506(state,automaton)
			|| typeof_516(state,automaton)
			|| typeof_517(state,automaton)
			|| typeof_518(state,automaton)
			|| typeof_519(state,automaton);
	}

	// $69<And($65<^{$39<^Type>...}>)>
	private static boolean typeof_516(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_508(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_517(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_520(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_518(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_520(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_519(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_521(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type>)>
	private static boolean typeof_512(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_505(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<Meta($39<^Type>)>
	private static boolean typeof_513(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_505(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_514(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_522(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_515(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_523(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_520(int index, Automaton automaton) {
		return typeof_524(automaton.get(index),automaton);
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_524(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_505(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_525(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_525(int index, Automaton automaton) {
		return typeof_526(automaton.get(index),automaton);
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_526(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_505(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_521(int index, Automaton automaton) {
		return typeof_527(automaton.get(index),automaton);
	}

	// $88<[$39<^Type>,^[$39...]]>
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
					if(!typeof_505(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_528(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $53<^[^string>,$39<^Type>]>
	private static boolean typeof_522(int index, Automaton automaton) {
		return typeof_529(automaton.get(index),automaton);
	}

	// $60<^[^string>$39<^Type>...]>
	private static boolean typeof_523(int index, Automaton automaton) {
		return typeof_530(automaton.get(index),automaton);
	}

	// $52<[^string>,$39<^Type>]>
	private static boolean typeof_529(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_505(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_528(int index, Automaton automaton) {
		return typeof_531(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_531(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_505(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<[^string>$39<^Type>...]>
	private static boolean typeof_530(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_505(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_511(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_505(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_249(int index, Automaton automaton) {
		return typeof_532(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_532(Automaton.State _state, Automaton automaton) {
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

	// $38<^Type>
	private static boolean typeof_533(int index, Automaton automaton) {
		return typeof_534(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_534(Automaton.State state, Automaton automaton) {
		return typeof_535(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_535(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_536(state,automaton)
			|| typeof_537(state,automaton)
			|| typeof_538(state,automaton)
			|| typeof_539(state,automaton)
			|| typeof_540(state,automaton)
			|| typeof_541(state,automaton)
			|| typeof_233(state,automaton)
			|| typeof_542(state,automaton)
			|| typeof_543(state,automaton)
			|| typeof_544(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_544(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_545(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_545(int index, Automaton automaton) {
		return typeof_546(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_546(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_547(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_547(int index, Automaton automaton) {
		return typeof_548(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_548(Automaton.State _state, Automaton automaton) {
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

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_541(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_249(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_540(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_549(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_549(int index, Automaton automaton) {
		return typeof_550(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_550(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_543(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_551(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_551(int index, Automaton automaton) {
		return typeof_552(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_552(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_553(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_553(int index, Automaton automaton) {
		return typeof_554(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_554(Automaton.State _state, Automaton automaton) {
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

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_542(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_551(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_537(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_533(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_536(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_533(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_539(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_555(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_555(int index, Automaton automaton) {
		return typeof_556(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_556(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_533(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_538(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_533(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Ref($39<^Type>)>>,^Ref($39)>>$39...}
	private static boolean typeof_112(int index, Automaton automaton) {
		return typeof_557(automaton.get(index),automaton);
	}

	// {^Ref($39<^Type>)>>,^Ref($39)>>$39...}
	private static boolean typeof_557(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_558(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_558(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_559(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^$43<Ref($39<^Type>)>
	private static boolean typeof_558(int index, Automaton automaton) {
		return typeof_560(automaton.get(index),automaton);
	}

	// $39<^Type>
	private static boolean typeof_559(int index, Automaton automaton) {
		return typeof_561(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_561(Automaton.State state, Automaton automaton) {
		return typeof_562(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_562(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_563(state,automaton)
			|| typeof_560(state,automaton)
			|| typeof_564(state,automaton)
			|| typeof_565(state,automaton)
			|| typeof_566(state,automaton)
			|| typeof_567(state,automaton)
			|| typeof_568(state,automaton)
			|| typeof_569(state,automaton)
			|| typeof_570(state,automaton)
			|| typeof_571(state,automaton);
	}

	// $67<Or($65<^{$39<^Type>...}>)>
	private static boolean typeof_567(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_572(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_566(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_573(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_565(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_574(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($39<^Type>)>
	private static boolean typeof_564(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_559(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_563(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_559(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($39<^Type>)>
	private static boolean typeof_560(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_559(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$39<^Type>]>
	private static boolean typeof_574(int index, Automaton automaton) {
		return typeof_575(automaton.get(index),automaton);
	}

	// $51<[^string>,$39<^Type>]>
	private static boolean typeof_575(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_559(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<^[^string>$39<^Type>...]>
	private static boolean typeof_573(int index, Automaton automaton) {
		return typeof_576(automaton.get(index),automaton);
	}

	// $65<^{$39<^Type>...}>
	private static boolean typeof_572(int index, Automaton automaton) {
		return typeof_577(automaton.get(index),automaton);
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_571(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_578(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_570(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_579(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_569(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_579(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($65<^{$39<^Type>...}>)>
	private static boolean typeof_568(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_572(data,automaton)) { return true; }
		}
		return false;
	}

	// $58<[^string>$39<^Type>...]>
	private static boolean typeof_576(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_559(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $64<{$39<^Type>...}>
	private static boolean typeof_577(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_559(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_578(int index, Automaton automaton) {
		return typeof_580(automaton.get(index),automaton);
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_579(int index, Automaton automaton) {
		return typeof_581(automaton.get(index),automaton);
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_580(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_559(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_582(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_581(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_559(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_583(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_582(int index, Automaton automaton) {
		return typeof_584(automaton.get(index),automaton);
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_583(int index, Automaton automaton) {
		return typeof_585(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_584(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_559(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_585(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_559(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $49<Not($1<^Type>)>
	private static boolean typeof_485(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_223(data,automaton)) { return true; }
		}
		return false;
	}

	// $5<Type>
	private static boolean typeof_364(Automaton.State state, Automaton automaton) {
		return typeof_586(state,automaton);
	}

	// $2<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_586(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_587(state,automaton)
			|| typeof_588(state,automaton)
			|| typeof_589(state,automaton)
			|| typeof_590(state,automaton)
			|| typeof_591(state,automaton)
			|| typeof_592(state,automaton)
			|| typeof_593(state,automaton)
			|| typeof_594(state,automaton)
			|| typeof_595(state,automaton)
			|| typeof_90(state,automaton);
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_587(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_358(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_588(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_358(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_589(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_358(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_590(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_596(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_591(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_597(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_593(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_598(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_592(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_598(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<Bag($77<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_595(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_599(data,automaton)) { return true; }
		}
		return false;
	}

	// $79<Set($77<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_594(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_599(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_597(int index, Automaton automaton) {
		return typeof_600(automaton.get(index),automaton);
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_596(int index, Automaton automaton) {
		return typeof_601(automaton.get(index),automaton);
	}

	// $77<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_599(int index, Automaton automaton) {
		return typeof_602(automaton.get(index),automaton);
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_598(int index, Automaton automaton) {
		return typeof_603(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_601(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_358(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<^[^string>,$37<^Type>]>
	private static boolean typeof_246(int index, Automaton automaton) {
		return typeof_604(automaton.get(index),automaton);
	}

	// ^{^Term(^[^string>,$40<^Type>]>)>,^Term(^[^string>,$40]>)>$40...}
	private static boolean typeof_127(int index, Automaton automaton) {
		return typeof_605(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_600(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_358(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {^Proton>>,^Proton>>$38<^Type>...}
	private static boolean typeof_484(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_606(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// [^bool,$86<^[$39<^Type>...]>]
	private static boolean typeof_365(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_163(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_359(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {^And(^{$38<^Type>...}>)>$38...}
	private static boolean typeof_247(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_361(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_126(int index, Automaton automaton) {
		return typeof_607(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_603(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_358(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $55<Meta($1<^Type>)>
	private static boolean typeof_487(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_223(data,automaton)) { return true; }
		}
		return false;
	}

	// {^Meta(^Any>),^Meta($43<^Type>)>$43...}
	private static boolean typeof_366(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_32(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_608(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_609(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^$46<Meta($39<^Type>)>
	private static boolean typeof_608(int index, Automaton automaton) {
		return typeof_610(automaton.get(index),automaton);
	}

	// $46<Meta($39<^Type>)>
	private static boolean typeof_610(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_609(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_609(int index, Automaton automaton) {
		return typeof_611(automaton.get(index),automaton);
	}

	// $9<Type>
	private static boolean typeof_611(Automaton.State state, Automaton automaton) {
		return typeof_612(state,automaton);
	}

	// $6<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_612(Automaton.State state, Automaton automaton) {
		return typeof_324(state,automaton)
			|| typeof_613(state,automaton)
			|| typeof_614(state,automaton)
			|| typeof_610(state,automaton)
			|| typeof_615(state,automaton)
			|| typeof_616(state,automaton)
			|| typeof_617(state,automaton)
			|| typeof_618(state,automaton)
			|| typeof_619(state,automaton)
			|| typeof_620(state,automaton)
			|| typeof_621(state,automaton);
	}

	// $44<Ref($39<^Type>)>
	private static boolean typeof_614(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_609(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_615(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_622(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_613(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_609(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$39<^Type>...}>)>
	private static boolean typeof_618(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_623(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_619(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_624(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_616(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_625(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<Or($64<^{$39<^Type>...}>)>
	private static boolean typeof_617(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_623(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$39<^Type>]>
	private static boolean typeof_622(int index, Automaton automaton) {
		return typeof_626(automaton.get(index),automaton);
	}

	// $64<^{$39<^Type>...}>
	private static boolean typeof_623(int index, Automaton automaton) {
		return typeof_627(automaton.get(index),automaton);
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_620(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_624(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_621(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_628(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<{$39<^Type>...}>
	private static boolean typeof_627(Automaton.State _state, Automaton automaton) {
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

	// $51<[^string>,$39<^Type>]>
	private static boolean typeof_626(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<^[^string>$39<^Type>...]>
	private static boolean typeof_625(int index, Automaton automaton) {
		return typeof_629(automaton.get(index),automaton);
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_624(int index, Automaton automaton) {
		return typeof_630(automaton.get(index),automaton);
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
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

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_631(int index, Automaton automaton) {
		return typeof_632(automaton.get(index),automaton);
	}

	// $58<[^string>$39<^Type>...]>
	private static boolean typeof_629(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_609(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_628(int index, Automaton automaton) {
		return typeof_633(automaton.get(index),automaton);
	}

	// $88<[$39<^Type>,^[$39...]]>
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

	// $85<^[$39<^Type>...]>
	private static boolean typeof_634(int index, Automaton automaton) {
		return typeof_635(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
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

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
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

	// $64<^{$37<^Type>...}>
	private static boolean typeof_244(int index, Automaton automaton) {
		return typeof_636(automaton.get(index),automaton);
	}

	// $63<{$37<^Type>...}>
	private static boolean typeof_636(Automaton.State _state, Automaton automaton) {
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

	// ^$45<Meta($38<^Type>)>
	private static boolean typeof_125(int index, Automaton automaton) {
		return typeof_637(automaton.get(index),automaton);
	}

	// $45<Meta($38<^Type>)>
	private static boolean typeof_637(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_638(data,automaton)) { return true; }
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_638(int index, Automaton automaton) {
		return typeof_639(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_639(Automaton.State state, Automaton automaton) {
		return typeof_640(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_640(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_641(state,automaton)
			|| typeof_642(state,automaton)
			|| typeof_637(state,automaton)
			|| typeof_643(state,automaton)
			|| typeof_644(state,automaton)
			|| typeof_645(state,automaton)
			|| typeof_646(state,automaton)
			|| typeof_647(state,automaton)
			|| typeof_648(state,automaton)
			|| typeof_649(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_649(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_650(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_648(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_651(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_651(int index, Automaton automaton) {
		return typeof_652(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_652(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_653(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_653(int index, Automaton automaton) {
		return typeof_654(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_654(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_650(int index, Automaton automaton) {
		return typeof_655(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_655(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_656(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_645(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_657(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_644(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_658(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_647(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_651(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_646(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_657(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_641(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_638(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_643(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_659(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_642(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_638(data,automaton)) { return true; }
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_656(int index, Automaton automaton) {
		return typeof_660(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_660(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_657(int index, Automaton automaton) {
		return typeof_661(automaton.get(index),automaton);
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_661(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $58<^[^string>$38<^Type>...]>
	private static boolean typeof_658(int index, Automaton automaton) {
		return typeof_662(automaton.get(index),automaton);
	}

	// $57<[^string>$38<^Type>...]>
	private static boolean typeof_662(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<^[^string>,$38<^Type>]>
	private static boolean typeof_659(int index, Automaton automaton) {
		return typeof_663(automaton.get(index),automaton);
	}

	// $50<[^string>,$38<^Type>]>
	private static boolean typeof_663(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_638(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_602(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_358(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_664(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_664(int index, Automaton automaton) {
		return typeof_665(automaton.get(index),automaton);
	}

	// $72<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_665(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_358(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $52<Ref($1<^Type>)>
	private static boolean typeof_486(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_223(data,automaton)) { return true; }
		}
		return false;
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_367(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_358(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $58<^[^string>$37<^Type>...]>
	private static boolean typeof_245(int index, Automaton automaton) {
		return typeof_666(automaton.get(index),automaton);
	}

	// $57<[^string>$37<^Type>...]>
	private static boolean typeof_666(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^[$38<^Type>,^{|^Void>$38...|}[^Void>$38...]]
	private static boolean typeof_124(int index, Automaton automaton) {
		return typeof_667(automaton.get(index),automaton);
	}

	// [$38<^Type>,^{|^Void>$38...|}[^Void>$38...]]
	private static boolean typeof_667(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_669(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_668(int index, Automaton automaton) {
		return typeof_670(automaton.get(index),automaton);
	}

	// ^{|^Void>$38<^Type>...|}[^Void>$38<^Type>...]
	private static boolean typeof_669(int index, Automaton automaton) {
		return typeof_671(automaton.get(index),automaton);
	}

	// $5<Type>
	private static boolean typeof_670(Automaton.State state, Automaton automaton) {
		return typeof_672(state,automaton);
	}

	// $2<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_672(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_673(state,automaton)
			|| typeof_674(state,automaton)
			|| typeof_675(state,automaton)
			|| typeof_676(state,automaton)
			|| typeof_677(state,automaton)
			|| typeof_678(state,automaton)
			|| typeof_679(state,automaton)
			|| typeof_680(state,automaton)
			|| typeof_681(state,automaton)
			|| typeof_682(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_682(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_683(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_683(int index, Automaton automaton) {
		return typeof_684(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_684(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_685(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_685(int index, Automaton automaton) {
		return typeof_686(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_686(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($77<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_681(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_687(data,automaton)) { return true; }
		}
		return false;
	}

	// $77<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_687(int index, Automaton automaton) {
		return typeof_688(automaton.get(index),automaton);
	}

	// $79<Set($77<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_680(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_687(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_679(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_689(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_678(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_689(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_677(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_690(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_676(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_691(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_675(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_668(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_674(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_668(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_673(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_668(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_690(int index, Automaton automaton) {
		return typeof_692(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_692(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_691(int index, Automaton automaton) {
		return typeof_693(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_693(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_688(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_694(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_694(int index, Automaton automaton) {
		return typeof_695(automaton.get(index),automaton);
	}

	// $72<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_695(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_689(int index, Automaton automaton) {
		return typeof_696(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_696(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_668(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {|^Void>$38<^Type>...|}[^Void>$38<^Type>...]
	private static boolean typeof_671(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_668(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// {^Term(^[^string>,$40<^Type>]>)>,^Term(^[^string>,$40]>)>$40...}
	private static boolean typeof_605(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_697(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_697(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_698(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $40<^Type>
	private static boolean typeof_698(int index, Automaton automaton) {
		return typeof_699(automaton.get(index),automaton);
	}

	// $7<Type>
	private static boolean typeof_699(Automaton.State state, Automaton automaton) {
		return typeof_700(state,automaton);
	}

	// $4<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_700(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_701(state,automaton)
			|| typeof_702(state,automaton)
			|| typeof_703(state,automaton)
			|| typeof_704(state,automaton)
			|| typeof_705(state,automaton)
			|| typeof_706(state,automaton)
			|| typeof_707(state,automaton)
			|| typeof_708(state,automaton)
			|| typeof_709(state,automaton)
			|| typeof_710(state,automaton);
	}

	// $45<Ref($40<^Type>)>
	private static boolean typeof_702(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_698(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Meta($40<^Type>)>
	private static boolean typeof_703(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_698(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<Not($40<^Type>)>
	private static boolean typeof_701(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_698(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Term($53<^[^string>,$40<^Type>]>)
	private static boolean typeof_697(int index, Automaton automaton) {
		return typeof_711(automaton.get(index),automaton);
	}

	// $61<Term(^[^string>$40<^Type>...])>
	private static boolean typeof_705(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_712(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$40<^Type>...]>
	private static boolean typeof_712(int index, Automaton automaton) {
		return typeof_713(automaton.get(index),automaton);
	}

	// $58<[^string>$40<^Type>...]>
	private static boolean typeof_713(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_698(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $55<Nominal(^[^string>,$40<^Type>])>
	private static boolean typeof_704(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_714(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<^[^string>,$40<^Type>]>
	private static boolean typeof_714(int index, Automaton automaton) {
		return typeof_715(automaton.get(index),automaton);
	}

	// $52<[^string>,$40<^Type>]>
	private static boolean typeof_715(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_698(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($65<^{$40<^Type>...}>)>
	private static boolean typeof_707(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_716(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$40<^Type>...}>
	private static boolean typeof_716(int index, Automaton automaton) {
		return typeof_717(automaton.get(index),automaton);
	}

	// $64<{$40<^Type>...}>
	private static boolean typeof_717(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_698(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<Or($65<^{$40<^Type>...}>)>
	private static boolean typeof_706(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_716(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_709(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_718(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_718(int index, Automaton automaton) {
		return typeof_719(automaton.get(index),automaton);
	}

	// $75<[$40<^Type>,^{|$40...|}[$40...]]>
	private static boolean typeof_719(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_698(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_720(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$40<^Type>,^{|$40...|}[$40...]]>)>
	private static boolean typeof_708(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_718(data,automaton)) { return true; }
		}
		return false;
	}

	// Term($53<^[^string>,$40<^Type>]>)
	private static boolean typeof_711(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_714(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$40<^Type>,^[$40...]])>
	private static boolean typeof_710(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_721(data,automaton)) { return true; }
		}
		return false;
	}

	// $72<^{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_720(int index, Automaton automaton) {
		return typeof_722(automaton.get(index),automaton);
	}

	// $89<^[$40<^Type>,^[$40...]]>
	private static boolean typeof_721(int index, Automaton automaton) {
		return typeof_723(automaton.get(index),automaton);
	}

	// $71<{|$40<^Type>...|}[$40<^Type>...]>
	private static boolean typeof_722(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_698(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $88<[$40<^Type>,^[$40...]]>
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
					if(!typeof_698(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_724(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$40<^Type>...]>
	private static boolean typeof_724(int index, Automaton automaton) {
		return typeof_725(automaton.get(index),automaton);
	}

	// {^Meta($39<^Type>)>>,^Meta($39)>>$39...}
	private static boolean typeof_481(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_726(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_726(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_727(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $5<Type>
	private static boolean typeof_360(Automaton.State state, Automaton automaton) {
		return typeof_728(state,automaton);
	}

	// $2<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_728(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_729(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_730(state,automaton)
			|| typeof_731(state,automaton)
			|| typeof_732(state,automaton)
			|| typeof_733(state,automaton)
			|| typeof_734(state,automaton)
			|| typeof_735(state,automaton)
			|| typeof_736(state,automaton)
			|| typeof_737(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_737(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_738(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_738(int index, Automaton automaton) {
		return typeof_739(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_739(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_119(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_740(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_736(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_741(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_741(int index, Automaton automaton) {
		return typeof_742(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_742(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_119(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_743(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_743(int index, Automaton automaton) {
		return typeof_744(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_744(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_119(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_740(int index, Automaton automaton) {
		return typeof_745(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_745(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_119(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_729(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// $45<Meta($38<^Type>)>
	private static boolean typeof_730(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_731(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_746(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<^[^string>,$38<^Type>]>
	private static boolean typeof_746(int index, Automaton automaton) {
		return typeof_747(automaton.get(index),automaton);
	}

	// $50<[^string>,$38<^Type>]>
	private static boolean typeof_747(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_119(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_732(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_748(data,automaton)) { return true; }
		}
		return false;
	}

	// $58<^[^string>$38<^Type>...]>
	private static boolean typeof_748(int index, Automaton automaton) {
		return typeof_749(automaton.get(index),automaton);
	}

	// $57<[^string>$38<^Type>...]>
	private static boolean typeof_749(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_119(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_733(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_750(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_750(int index, Automaton automaton) {
		return typeof_751(automaton.get(index),automaton);
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_751(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_119(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_734(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_750(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_735(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_741(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$37<^Type>,^{|$37...|}[$37...]]>)>
	private static boolean typeof_242(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_752(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$37<^Type>,^{|$37...|}[$37...]]>
	private static boolean typeof_752(int index, Automaton automaton) {
		return typeof_753(automaton.get(index),automaton);
	}

	// $75<[$37<^Type>,^{|$37...|}[$37...]]>
	private static boolean typeof_753(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_754(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$37<^Type>...|}[$37<^Type>...]>
	private static boolean typeof_754(int index, Automaton automaton) {
		return typeof_755(automaton.get(index),automaton);
	}

	// $71<{|$37<^Type>...|}[$37<^Type>...]>
	private static boolean typeof_755(Automaton.State _state, Automaton automaton) {
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

	// $52<^[^string>,$39<^Type>]>
	private static boolean typeof_123(int index, Automaton automaton) {
		return typeof_756(automaton.get(index),automaton);
	}

	// $51<[^string>,$39<^Type>]>
	private static boolean typeof_756(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_757(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_757(int index, Automaton automaton) {
		return typeof_758(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_758(Automaton.State state, Automaton automaton) {
		return typeof_759(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_759(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_760(state,automaton)
			|| typeof_761(state,automaton)
			|| typeof_762(state,automaton)
			|| typeof_763(state,automaton)
			|| typeof_764(state,automaton)
			|| typeof_765(state,automaton)
			|| typeof_766(state,automaton)
			|| typeof_767(state,automaton)
			|| typeof_768(state,automaton)
			|| typeof_769(state,automaton);
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_768(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_770(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_770(int index, Automaton automaton) {
		return typeof_771(automaton.get(index),automaton);
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_771(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_757(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_772(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_772(int index, Automaton automaton) {
		return typeof_773(automaton.get(index),automaton);
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_773(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_757(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_769(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_774(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_774(int index, Automaton automaton) {
		return typeof_775(automaton.get(index),automaton);
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_775(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_757(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_776(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_776(int index, Automaton automaton) {
		return typeof_777(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_777(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_757(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type>)>
	private static boolean typeof_762(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_757(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_763(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_123(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_760(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_757(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type>)>
	private static boolean typeof_761(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_757(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$39<^Type>...}>)>
	private static boolean typeof_766(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_778(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<^{$39<^Type>...}>
	private static boolean typeof_778(int index, Automaton automaton) {
		return typeof_779(automaton.get(index),automaton);
	}

	// $63<{$39<^Type>...}>
	private static boolean typeof_779(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_757(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_767(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_770(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_764(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_780(data,automaton)) { return true; }
		}
		return false;
	}

	// $58<^[^string>$39<^Type>...]>
	private static boolean typeof_780(int index, Automaton automaton) {
		return typeof_781(automaton.get(index),automaton);
	}

	// $57<[^string>$39<^Type>...]>
	private static boolean typeof_781(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_757(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<Or($64<^{$39<^Type>...}>)>
	private static boolean typeof_765(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_778(data,automaton)) { return true; }
		}
		return false;
	}

	// $84<[$40<^Type>...]>
	private static boolean typeof_725(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_698(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $50<[^string>,$37<^Type>]>
	private static boolean typeof_604(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_480(int index, Automaton automaton) {
		return typeof_782(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
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
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_458(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_361(int index, Automaton automaton) {
		return typeof_783(automaton.get(index),automaton);
	}

	// $5<Type>
	private static boolean typeof_783(Automaton.State state, Automaton automaton) {
		return typeof_784(state,automaton);
	}

	// $2<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_784(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_785(state,automaton)
			|| typeof_786(state,automaton)
			|| typeof_787(state,automaton)
			|| typeof_788(state,automaton)
			|| typeof_789(state,automaton)
			|| typeof_790(state,automaton)
			|| typeof_56(state,automaton)
			|| typeof_791(state,automaton)
			|| typeof_792(state,automaton)
			|| typeof_793(state,automaton);
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_791(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_794(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_790(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_105(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_789(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_795(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_788(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_796(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_787(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_361(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_786(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_361(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_785(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_361(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_796(int index, Automaton automaton) {
		return typeof_797(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_797(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_361(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_795(int index, Automaton automaton) {
		return typeof_798(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_798(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_361(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_794(int index, Automaton automaton) {
		return typeof_799(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
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
					if(!typeof_361(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_800(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_800(int index, Automaton automaton) {
		return typeof_801(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_801(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_361(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_793(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_802(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_802(int index, Automaton automaton) {
		return typeof_803(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_803(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_361(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_804(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_804(int index, Automaton automaton) {
		return typeof_805(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_805(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_361(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_792(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_794(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$37<^Type>,^[$37...]])>
	private static boolean typeof_243(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_806(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$37<^Type>,^[$37...]]>
	private static boolean typeof_806(int index, Automaton automaton) {
		return typeof_807(automaton.get(index),automaton);
	}

	// $88<[$37<^Type>,^[$37...]]>
	private static boolean typeof_807(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_808(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$37<^Type>...]>
	private static boolean typeof_808(int index, Automaton automaton) {
		return typeof_809(automaton.get(index),automaton);
	}

	// $84<[$37<^Type>...]>
	private static boolean typeof_809(Automaton.State _state, Automaton automaton) {
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

	// ^{^Meta($39<^Type>)>>,^Not(^Meta($39)>>)$39...}
	private static boolean typeof_122(int index, Automaton automaton) {
		return typeof_810(automaton.get(index),automaton);
	}

	// {^Meta($39<^Type>)>>,^Not(^Meta($39)>>)$39...}
	private static boolean typeof_810(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_726(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_811(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_727(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^Not(^$46<Meta($39<^Type>)>)
	private static boolean typeof_811(int index, Automaton automaton) {
		return typeof_812(automaton.get(index),automaton);
	}

	// Not(^$46<Meta($39<^Type>)>)
	private static boolean typeof_812(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_726(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$46<Meta($39<^Type>)>
	private static boolean typeof_726(int index, Automaton automaton) {
		return typeof_813(automaton.get(index),automaton);
	}

	// $46<Meta($39<^Type>)>
	private static boolean typeof_813(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_727(data,automaton)) { return true; }
		}
		return false;
	}

	// $5<Type>
	private static boolean typeof_607(Automaton.State state, Automaton automaton) {
		return typeof_814(state,automaton);
	}

	// $2<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_814(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_815(state,automaton)
			|| typeof_816(state,automaton)
			|| typeof_80(state,automaton)
			|| typeof_817(state,automaton)
			|| typeof_818(state,automaton)
			|| typeof_819(state,automaton)
			|| typeof_820(state,automaton)
			|| typeof_821(state,automaton)
			|| typeof_822(state,automaton)
			|| typeof_823(state,automaton);
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_821(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_824(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_820(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_825(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_823(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_826(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_822(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_824(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_817(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_827(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_816(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_126(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_819(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_825(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_818(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_828(data,automaton)) { return true; }
		}
		return false;
	}

	// $58<^[^string>$38<^Type>...]>
	private static boolean typeof_828(int index, Automaton automaton) {
		return typeof_829(automaton.get(index),automaton);
	}

	// $57<[^string>$38<^Type>...]>
	private static boolean typeof_829(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_126(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_825(int index, Automaton automaton) {
		return typeof_830(automaton.get(index),automaton);
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_830(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_126(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_824(int index, Automaton automaton) {
		return typeof_831(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_831(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_126(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_832(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<^[^string>,$38<^Type>]>
	private static boolean typeof_827(int index, Automaton automaton) {
		return typeof_833(automaton.get(index),automaton);
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_826(int index, Automaton automaton) {
		return typeof_834(automaton.get(index),automaton);
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_815(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_126(data,automaton)) { return true; }
		}
		return false;
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_834(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_126(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_835(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_835(int index, Automaton automaton) {
		return typeof_836(automaton.get(index),automaton);
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_832(int index, Automaton automaton) {
		return typeof_837(automaton.get(index),automaton);
	}

	// $50<[^string>,$38<^Type>]>
	private static boolean typeof_833(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_126(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_836(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_126(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_837(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_126(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<Type>
	private static boolean typeof_483(Automaton.State state, Automaton automaton) {
		return typeof_838(state,automaton);
	}

	// $2<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_838(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_839(state,automaton)
			|| typeof_840(state,automaton)
			|| typeof_841(state,automaton)
			|| typeof_842(state,automaton)
			|| typeof_843(state,automaton)
			|| typeof_58(state,automaton)
			|| typeof_844(state,automaton)
			|| typeof_845(state,automaton)
			|| typeof_846(state,automaton)
			|| typeof_847(state,automaton);
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_839(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_251(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_842(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_848(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_848(int index, Automaton automaton) {
		return typeof_849(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_849(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_251(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_843(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_850(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_850(int index, Automaton automaton) {
		return typeof_851(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_851(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_251(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_840(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_251(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_841(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_251(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_846(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_852(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_852(int index, Automaton automaton) {
		return typeof_853(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_853(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_251(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_854(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_854(int index, Automaton automaton) {
		return typeof_855(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_855(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_251(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_847(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_856(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_856(int index, Automaton automaton) {
		return typeof_857(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_857(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_251(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_858(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_858(int index, Automaton automaton) {
		return typeof_859(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_859(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_251(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_844(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_103(data,automaton)) { return true; }
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_362(int index, Automaton automaton) {
		return typeof_860(automaton.get(index),automaton);
	}

	// $8<Type>
	private static boolean typeof_860(Automaton.State state, Automaton automaton) {
		return typeof_861(state,automaton);
	}

	// $5<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_861(Automaton.State state, Automaton automaton) {
		return typeof_324(state,automaton)
			|| typeof_862(state,automaton)
			|| typeof_863(state,automaton)
			|| typeof_864(state,automaton)
			|| typeof_865(state,automaton)
			|| typeof_866(state,automaton)
			|| typeof_867(state,automaton)
			|| typeof_868(state,automaton)
			|| typeof_869(state,automaton)
			|| typeof_870(state,automaton)
			|| typeof_871(state,automaton);
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_864(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_362(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_865(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_872(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_866(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_873(data,automaton)) { return true; }
		}
		return false;
	}

	// $66<Or($64<^{$38<^Type>...}>)>
	private static boolean typeof_867(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_874(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$38<^Type>...}>)>
	private static boolean typeof_868(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_874(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_869(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_875(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_870(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_875(data,automaton)) { return true; }
		}
		return false;
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_871(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_876(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_872(int index, Automaton automaton) {
		return typeof_877(automaton.get(index),automaton);
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_873(int index, Automaton automaton) {
		return typeof_878(automaton.get(index),automaton);
	}

	// $64<^{$38<^Type>...}>
	private static boolean typeof_874(int index, Automaton automaton) {
		return typeof_879(automaton.get(index),automaton);
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_875(int index, Automaton automaton) {
		return typeof_880(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_880(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_362(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_881(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_881(int index, Automaton automaton) {
		return typeof_882(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_882(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_362(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_876(int index, Automaton automaton) {
		return typeof_883(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_883(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_362(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_884(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_884(int index, Automaton automaton) {
		return typeof_885(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_885(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_362(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<[^string>,$38<^Type>]>
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
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_362(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_878(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_362(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $63<{$38<^Type>...}>
	private static boolean typeof_879(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_362(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_863(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_362(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_862(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_362(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($64<^{$37<^Type>...}>)>
	private static boolean typeof_240(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_244(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[$38<^Type>,^{|^Void>$38...|}[^Void>$38...]]
	private static boolean typeof_121(int index, Automaton automaton) {
		return typeof_886(automaton.get(index),automaton);
	}

	// [$38<^Type>,^{|^Void>$38...|}[^Void>$38...]]
	private static boolean typeof_886(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_888(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_887(int index, Automaton automaton) {
		return typeof_889(automaton.get(index),automaton);
	}

	// $5<Type>
	private static boolean typeof_889(Automaton.State state, Automaton automaton) {
		return typeof_890(state,automaton);
	}

	// ^{|^Void>$38<^Type>...|}[^Void>$38<^Type>...]
	private static boolean typeof_888(int index, Automaton automaton) {
		return typeof_891(automaton.get(index),automaton);
	}

	// {|^Void>$38<^Type>...|}[^Void>$38<^Type>...]
	private static boolean typeof_891(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_887(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $2<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_890(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_892(state,automaton)
			|| typeof_893(state,automaton)
			|| typeof_894(state,automaton)
			|| typeof_895(state,automaton)
			|| typeof_896(state,automaton)
			|| typeof_897(state,automaton)
			|| typeof_898(state,automaton)
			|| typeof_899(state,automaton)
			|| typeof_900(state,automaton)
			|| typeof_901(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_901(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_902(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_902(int index, Automaton automaton) {
		return typeof_903(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_903(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_904(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_904(int index, Automaton automaton) {
		return typeof_905(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_905(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_900(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_906(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_906(int index, Automaton automaton) {
		return typeof_907(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_907(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_908(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_908(int index, Automaton automaton) {
		return typeof_909(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_909(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_899(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_906(data,automaton)) { return true; }
		}
		return false;
	}

	// $70<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_898(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_910(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_910(int index, Automaton automaton) {
		return typeof_911(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_911(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_897(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_910(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_896(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_912(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_912(int index, Automaton automaton) {
		return typeof_913(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_913(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_893(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_887(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_892(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_887(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_895(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_914(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_914(int index, Automaton automaton) {
		return typeof_915(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_915(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_887(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_894(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_887(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_845(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_852(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_727(int index, Automaton automaton) {
		return typeof_916(automaton.get(index),automaton);
	}

	// $6<Type>
	private static boolean typeof_916(Automaton.State state, Automaton automaton) {
		return typeof_917(state,automaton);
	}

	// $3<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_917(Automaton.State state, Automaton automaton) {
		return typeof_136(state,automaton)
			|| typeof_918(state,automaton)
			|| typeof_919(state,automaton)
			|| typeof_813(state,automaton)
			|| typeof_920(state,automaton)
			|| typeof_921(state,automaton)
			|| typeof_922(state,automaton)
			|| typeof_923(state,automaton)
			|| typeof_924(state,automaton)
			|| typeof_925(state,automaton)
			|| typeof_926(state,automaton);
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_926(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_927(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_927(int index, Automaton automaton) {
		return typeof_928(automaton.get(index),automaton);
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_928(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_727(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_929(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_929(int index, Automaton automaton) {
		return typeof_930(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_930(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_727(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_924(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_931(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_931(int index, Automaton automaton) {
		return typeof_932(automaton.get(index),automaton);
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_932(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_727(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_933(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_933(int index, Automaton automaton) {
		return typeof_934(automaton.get(index),automaton);
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_934(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_727(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_925(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_931(data,automaton)) { return true; }
		}
		return false;
	}

	// $67<Or($65<^{$39<^Type>...}>)>
	private static boolean typeof_922(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_935(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$39<^Type>...}>
	private static boolean typeof_935(int index, Automaton automaton) {
		return typeof_936(automaton.get(index),automaton);
	}

	// $64<{$39<^Type>...}>
	private static boolean typeof_936(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_727(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($65<^{$39<^Type>...}>)>
	private static boolean typeof_923(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_935(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_920(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_937(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$39<^Type>]>
	private static boolean typeof_937(int index, Automaton automaton) {
		return typeof_938(automaton.get(index),automaton);
	}

	// $51<[^string>,$39<^Type>]>
	private static boolean typeof_938(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_727(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_921(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_939(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$39<^Type>...]>
	private static boolean typeof_939(int index, Automaton automaton) {
		return typeof_940(automaton.get(index),automaton);
	}

	// $58<[^string>$39<^Type>...]>
	private static boolean typeof_940(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_727(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_918(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_727(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type>)>
	private static boolean typeof_919(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_727(data,automaton)) { return true; }
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_606(int index, Automaton automaton) {
		return typeof_941(automaton.get(index),automaton);
	}

	// $29<Type>
	private static boolean typeof_941(Automaton.State state, Automaton automaton) {
		return typeof_942(state,automaton);
	}

	// $26<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_942(Automaton.State state, Automaton automaton) {
		return typeof_293(state,automaton)
			|| typeof_943(state,automaton)
			|| typeof_944(state,automaton)
			|| typeof_945(state,automaton)
			|| typeof_946(state,automaton)
			|| typeof_947(state,automaton)
			|| typeof_948(state,automaton)
			|| typeof_949(state,automaton)
			|| typeof_950(state,automaton)
			|| typeof_951(state,automaton)
			|| typeof_952(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_952(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_953(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_953(int index, Automaton automaton) {
		return typeof_954(automaton.get(index),automaton);
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_954(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_606(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_955(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_955(int index, Automaton automaton) {
		return typeof_956(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_956(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_606(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_948(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_957(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_957(int index, Automaton automaton) {
		return typeof_958(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_958(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_606(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_949(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_957(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_950(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_959(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_959(int index, Automaton automaton) {
		return typeof_960(automaton.get(index),automaton);
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_951(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_959(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_944(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_606(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_945(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_606(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_946(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_961(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_947(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_962(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_943(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_606(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_962(int index, Automaton automaton) {
		return typeof_963(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
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
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_606(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_961(int index, Automaton automaton) {
		return typeof_964(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_960(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_606(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_965(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_965(int index, Automaton automaton) {
		return typeof_966(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_966(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_606(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $51<[^string>,$38<^Type>]>
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
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_606(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $38<^Type>
	private static boolean typeof_482(int index, Automaton automaton) {
		return typeof_967(automaton.get(index),automaton);
	}

	// $8<Type>
	private static boolean typeof_967(Automaton.State state, Automaton automaton) {
		return typeof_968(state,automaton);
	}

	// $5<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_968(Automaton.State state, Automaton automaton) {
		return typeof_371(state,automaton)
			|| typeof_969(state,automaton)
			|| typeof_970(state,automaton)
			|| typeof_971(state,automaton)
			|| typeof_972(state,automaton)
			|| typeof_973(state,automaton)
			|| typeof_974(state,automaton)
			|| typeof_975(state,automaton)
			|| typeof_976(state,automaton)
			|| typeof_977(state,automaton)
			|| typeof_978(state,automaton);
	}

	// $91<List(^[$38<^Type>,^[$38...]])>
	private static boolean typeof_978(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_979(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$38<^Type>,^[$38...]]>
	private static boolean typeof_979(int index, Automaton automaton) {
		return typeof_980(automaton.get(index),automaton);
	}

	// $78<Set($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_976(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_981(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$38<^Type>,^{|$38...|}[$38...]]>)>
	private static boolean typeof_977(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_981(data,automaton)) { return true; }
		}
		return false;
	}

	// $88<[$38<^Type>,^[$38...]]>
	private static boolean typeof_980(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_482(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_982(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$38<^Type>...]>
	private static boolean typeof_982(int index, Automaton automaton) {
		return typeof_983(automaton.get(index),automaton);
	}

	// $84<[$38<^Type>...]>
	private static boolean typeof_983(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_482(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $76<^[$38<^Type>,^{|$38...|}[$38...]]>
	private static boolean typeof_981(int index, Automaton automaton) {
		return typeof_984(automaton.get(index),automaton);
	}

	// $75<[$38<^Type>,^{|$38...|}[$38...]]>
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
					if(!typeof_482(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_985(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_985(int index, Automaton automaton) {
		return typeof_986(automaton.get(index),automaton);
	}

	// $71<{|$38<^Type>...|}[$38<^Type>...]>
	private static boolean typeof_986(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_482(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<Meta($38<^Type>)>
	private static boolean typeof_971(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_482(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Ref($38<^Type>)>
	private static boolean typeof_970(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_482(data,automaton)) { return true; }
		}
		return false;
	}

	// $40<Not($38<^Type>)>
	private static boolean typeof_969(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_482(data,automaton)) { return true; }
		}
		return false;
	}

	// $69<And($65<^{$38<^Type>...}>)>
	private static boolean typeof_975(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_987(data,automaton)) { return true; }
		}
		return false;
	}

	// $65<^{$38<^Type>...}>
	private static boolean typeof_987(int index, Automaton automaton) {
		return typeof_988(automaton.get(index),automaton);
	}

	// $64<{$38<^Type>...}>
	private static boolean typeof_988(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_482(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<Or($65<^{$38<^Type>...}>)>
	private static boolean typeof_974(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_987(data,automaton)) { return true; }
		}
		return false;
	}

	// $61<Term(^[^string>$38<^Type>...])>
	private static boolean typeof_973(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_989(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$38<^Type>...]>
	private static boolean typeof_989(int index, Automaton automaton) {
		return typeof_990(automaton.get(index),automaton);
	}

	// $58<[^string>$38<^Type>...]>
	private static boolean typeof_990(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_482(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $54<Nominal(^[^string>,$38<^Type>])>
	private static boolean typeof_972(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_991(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$38<^Type>]>
	private static boolean typeof_991(int index, Automaton automaton) {
		return typeof_992(automaton.get(index),automaton);
	}

	// $51<[^string>,$38<^Type>]>
	private static boolean typeof_992(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_482(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {^Ref(^Any>),^Ref($43<^Type>)>$43...}
	private static boolean typeof_363(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_993(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_994(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^$43<Ref($39<^Type>)>
	private static boolean typeof_993(int index, Automaton automaton) {
		return typeof_995(automaton.get(index),automaton);
	}

	// $43<Ref($39<^Type>)>
	private static boolean typeof_995(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_994(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<^Type>
	private static boolean typeof_994(int index, Automaton automaton) {
		return typeof_996(automaton.get(index),automaton);
	}

	// $9<Type>
	private static boolean typeof_996(Automaton.State state, Automaton automaton) {
		return typeof_997(state,automaton);
	}

	// $6<Atom|Not(^Type>)|Ref(^Type>)|Meta(^Type>)|Nominal(^[^string>,^Type>])|Term(^[^string>^Type>...])|Or(^{^Type>...}>)|And(^{^Type>...}>)|Set(^[^Type>,^{|^Type>...|}[^Type>...]]>)|Bag(^[^Type>,^{|^Type>...|}[^Type>...]]>)|List(^[^Type>,^[^Type>...]])>
	private static boolean typeof_997(Automaton.State state, Automaton automaton) {
		return typeof_324(state,automaton)
			|| typeof_998(state,automaton)
			|| typeof_995(state,automaton)
			|| typeof_999(state,automaton)
			|| typeof_1000(state,automaton)
			|| typeof_1001(state,automaton)
			|| typeof_1002(state,automaton)
			|| typeof_1003(state,automaton)
			|| typeof_1004(state,automaton)
			|| typeof_1005(state,automaton)
			|| typeof_1006(state,automaton);
	}

	// $61<Term(^[^string>$39<^Type>...])>
	private static boolean typeof_1001(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Term) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1007(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string>,$39<^Type>])>
	private static boolean typeof_1000(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1008(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string>,$39<^Type>]>
	private static boolean typeof_1008(int index, Automaton automaton) {
		return typeof_1009(automaton.get(index),automaton);
	}

	// $51<[^string>,$39<^Type>]>
	private static boolean typeof_1009(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_994(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $69<And($64<^{$39<^Type>...}>)>
	private static boolean typeof_1003(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1010(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<^{$39<^Type>...}>
	private static boolean typeof_1010(int index, Automaton automaton) {
		return typeof_1011(automaton.get(index),automaton);
	}

	// $63<{$39<^Type>...}>
	private static boolean typeof_1011(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_994(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $66<Or($64<^{$39<^Type>...}>)>
	private static boolean typeof_1002(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1010(data,automaton)) { return true; }
		}
		return false;
	}

	// $81<Bag($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_1005(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1012(data,automaton)) { return true; }
		}
		return false;
	}

	// $76<^[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_1012(int index, Automaton automaton) {
		return typeof_1013(automaton.get(index),automaton);
	}

	// $75<[$39<^Type>,^{|$39...|}[$39...]]>
	private static boolean typeof_1013(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_994(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_1014(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<^{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_1014(int index, Automaton automaton) {
		return typeof_1015(automaton.get(index),automaton);
	}

	// $71<{|$39<^Type>...|}[$39<^Type>...]>
	private static boolean typeof_1015(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_994(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<Set($76<^[$39<^Type>,^{|$39...|}[$39...]]>)>
	private static boolean typeof_1004(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1012(data,automaton)) { return true; }
		}
		return false;
	}

	// $59<^[^string>$39<^Type>...]>
	private static boolean typeof_1007(int index, Automaton automaton) {
		return typeof_1016(automaton.get(index),automaton);
	}

	// $58<[^string>$39<^Type>...]>
	private static boolean typeof_1016(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
				else {
					if(!typeof_994(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $91<List(^[$39<^Type>,^[$39...]])>
	private static boolean typeof_1006(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_1017(data,automaton)) { return true; }
		}
		return false;
	}

	// $89<^[$39<^Type>,^[$39...]]>
	private static boolean typeof_1017(int index, Automaton automaton) {
		return typeof_1018(automaton.get(index),automaton);
	}

	// $88<[$39<^Type>,^[$39...]]>
	private static boolean typeof_1018(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_994(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_1019(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<^[$39<^Type>...]>
	private static boolean typeof_1019(int index, Automaton automaton) {
		return typeof_1020(automaton.get(index),automaton);
	}

	// $84<[$39<^Type>...]>
	private static boolean typeof_1020(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_994(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $46<Meta($39<^Type>)>
	private static boolean typeof_999(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_994(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type>)>
	private static boolean typeof_998(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_994(data,automaton)) { return true; }
		}
		return false;
	}

	// $78<Set($76<^[$37<^Type>,^{|$37...|}[$37...]]>)>
	private static boolean typeof_241(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_752(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Ref($39<^Type>)>>,^Not(^Ref($39)>>)$39...}
	private static boolean typeof_120(int index, Automaton automaton) {
		return typeof_1021(automaton.get(index),automaton);
	}

	// {^Ref($39<^Type>)>>,^Not(^Ref($39)>>)$39...}
	private static boolean typeof_1021(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_558(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_1022(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_559(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^Not(^$43<Ref($39<^Type>)>)
	private static boolean typeof_1022(int index, Automaton automaton) {
		return typeof_1023(automaton.get(index),automaton);
	}

	// Not(^$43<Ref($39<^Type>)>)
	private static boolean typeof_1023(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_558(data,automaton)) { return true; }
		}
		return false;
	}

	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// $3<Not($1<^Type>)>
		Schema.Term("Not",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
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
		Schema.Term("Ref",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $3<Meta($1<^Type>)>
		Schema.Term("Meta",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true))))),
		// $8<Term(^[^string>$2<^Type>...])>
		Schema.Term("Term",Schema.List(true,Schema.String)),
		// $8<Nominal(^[^string>,$2<^Type>])>
		Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))))),
		// Fun(^[$1<^Type>,$1])
		Schema.Term("Fun",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Any)),
		// $10<Set($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
		Schema.Term("Set",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Bag",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Bag(true))),
		// $10<Bag($8<^[$1<^Type>,^{|$1...|}[$1...]]>)>
		Schema.Term("Bag",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.Any), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true)))),Schema.Bag(true))),
		// $10<List(^[$1<^Type>,^[$1...]])>
		Schema.Term("List",Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Term",Schema.List(true,Schema.Any)), Schema.Term("Or",Schema.Set(true)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Bag",Schema.Any)),Schema.List(true)))
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
