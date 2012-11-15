package wyone.util.type;

import java.io.*;
import java.util.*;
import java.math.BigInteger;

import wyautl.core.Automaton;
import wyautl.io.PrettyAutomataReader;
import wyautl.io.PrettyAutomataWriter;
import wyautl.util.BigRational;
import wyone.core.*;
import static wyone.util.Runtime.*;

public final class Types {
	// term Not(^Type)
	public final static int K_Not = 0;
	public final static int Not(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Not, r0));
	}

	// Not(Any)
	public static boolean reduce_5e544e6f745e54416e79(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
	
		Automaton.Term r4 = Void;
		int r5 = automaton.add(r4);
		if(r0 != r5) {
			automaton.rewrite(r0, r5);
			numReductions++;
			return true;
		}
		return false;
	}

	// Not(Or({Type es...}))
	public static boolean reduce_5e544e6f745e544f725e7b5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
		Automaton.Set r5 = (Automaton.Set) automaton.get(r4);
		int j6 = 0;
		int[] t6 = new int[r5.size()-0];
		for(int i6=0;i6!=r5.size();++i6) {
			int r6 = r5.get(i6);
			if(!typeof_5e5454797065(r6,automaton)) { continue; }
			
			t6[j6++] = r6;
		}
		Automaton.Set r7 = new Automaton.Set(t6);
	
		Automaton.List t8 = new Automaton.List();
		for(int i9=0;i9<r7.size();i9++) {
			int r9 = r7.get(i9);
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

	// Not(And({Type es...}))
	public static boolean reduce_5e544e6f745e54416e645e7b5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
		Automaton.Set r5 = (Automaton.Set) automaton.get(r4);
		int j6 = 0;
		int[] t6 = new int[r5.size()-0];
		for(int i6=0;i6!=r5.size();++i6) {
			int r6 = r5.get(i6);
			if(!typeof_5e5454797065(r6,automaton)) { continue; }
			
			t6[j6++] = r6;
		}
		Automaton.Set r7 = new Automaton.Set(t6);
	
		Automaton.List t8 = new Automaton.List();
		for(int i9=0;i9<r7.size();i9++) {
			int r9 = r7.get(i9);
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

	// term And(^{^Type...})
	public final static int K_And = 1;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// And({Type t})
	public static boolean reduce_5e54416e645e7b5e54547970657d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e5454797065(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({And({Type xs...}), Type ys...})
	public static boolean reduce_5e54416e645e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e54416e645e7b5e54547970652e7d(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_5e5454797065(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_5e5454797065(r10,automaton)) { continue; }
				
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

	// And({Or({Type xs...}), Type ys...})
	public static boolean reduce_5e54416e645e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e544f725e7b5e54547970652e7d(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_5e5454797065(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_5e5454797065(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Set r11 = new Automaton.Set(t10);
	
		Automaton.List t12 = new Automaton.List();
		for(int i13=0;i13<r9.size();i13++) {
			int r13 = r9.get(i13);
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

	// term Or(^{^Type...})
	public final static int K_Or = 2;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// Or({Type t})
	public static boolean reduce_5e544f725e7b5e54547970657d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e5454797065(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Or({Or({Type xs...}), Type ys...})
	public static boolean reduce_5e544f725e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e544f725e7b5e54547970652e7d(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_5e5454797065(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_5e5454797065(r10,automaton)) { continue; }
				
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

	// Proton as Any | Void | Bool | Int | Real | String

	// Atom as Proton

	// And({Void, Type xs...})
	public static boolean reduce_5e54416e645e7b5e54566f69645e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e54566f6964(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int j6 = 0;
			int[] t6 = new int[r3.size()-1];
			for(int i6=0;i6!=r3.size();++i6) {
				int r6 = r3.get(i6);
				if(i6 == i4 || !typeof_5e5454797065(r6,automaton)) { continue; }
				
				t6[j6++] = r6;
			}
			Automaton.Set r7 = new Automaton.Set(t6);
	
			Automaton.Term r8 = Void;
			int r9 = automaton.add(r8);
			if(r0 != r9) {
				automaton.rewrite(r0, r9);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Any, Type xs...})
	public static boolean reduce_5e54416e645e7b5e54416e795e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e54416e79(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int j6 = 0;
			int[] t6 = new int[r3.size()-1];
			for(int i6=0;i6!=r3.size();++i6) {
				int r6 = r3.get(i6);
				if(i6 == i4 || !typeof_5e5454797065(r6,automaton)) { continue; }
				
				t6[j6++] = r6;
			}
			Automaton.Set r7 = new Automaton.Set(t6);
	
			int r8 = automaton.add(r7);
			Automaton.Term r9 = new Automaton.Term(K_And,r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				automaton.rewrite(r0, r10);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Proton a1, Proton a2, Type ts...})
	public static boolean reduce_5e54416e645e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e5450726f746f6e(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			for(int i6=0;i6!=r3.size();++i6) {
				int r6 = r3.get(i6);
				if(i6 == i4 || !typeof_5e5450726f746f6e(r6,automaton)) { continue; }
				
				Automaton.Term r7 = (Automaton.Term) automaton.get(r6);
				int j8 = 0;
				int[] t8 = new int[r3.size()-2];
				for(int i8=0;i8!=r3.size();++i8) {
					int r8 = r3.get(i8);
					if(i8 == i4 || i8 == i6 || !typeof_5e5454797065(r8,automaton)) { continue; }
					
					t8[j8++] = r8;
				}
				Automaton.Set r9 = new Automaton.Set(t8);
	
				boolean r10 = r4 != r6;        // a1 neq a2
				boolean r11 = false;           // a1 neq a2 && !a2 is ^Any
				if(r10) {
					boolean r12 = typeof_5e54416e79(r6,automaton); // a2 is ^Any
					boolean r13 = !r12;            // !a2 is ^Any
					r11 = r13;
				}
				if(r11) {
					Automaton.Term r14 = Void;
					int r15 = automaton.add(r14);
					if(r0 != r15) {
						automaton.rewrite(r0, r15);
						numReductions++;
						return true;
					}
				}
			}
		}
		return false;
	}

	// And({Proton a1, Not(Proton a2), Type ts...})
	public static boolean reduce_5e54416e645e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e5450726f746f6e(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			for(int i6=0;i6!=r3.size();++i6) {
				int r6 = r3.get(i6);
				if(i6 == i4 || !typeof_5e544e6f745e5450726f746f6e(r6,automaton)) { continue; }
				
				Automaton.Term r7 = (Automaton.Term) automaton.get(r6);
				int r8 = r7.contents;
				Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
				int j10 = 0;
				int[] t10 = new int[r3.size()-2];
				for(int i10=0;i10!=r3.size();++i10) {
					int r10 = r3.get(i10);
					if(i10 == i4 || i10 == i6 || !typeof_5e5454797065(r10,automaton)) { continue; }
					
					t10[j10++] = r10;
				}
				Automaton.Set r11 = new Automaton.Set(t10);
	
				boolean r12 = r4 == r8;        // a1 eq a2
				if(r12) {
					Automaton.Term r13 = Void;
					int r14 = automaton.add(r13);
					if(r0 != r14) {
						automaton.rewrite(r0, r14);
						numReductions++;
						return true;
					}
				}
				Automaton.Term r15 = Any;
				Automaton.Term r16 = (Automaton.Term) automaton.get(r8);
				boolean r17 = !r16.equals(r15); // a2 neq Any
				if(r17) {
					Automaton.Set r18 = r11.appendFront(r4); // a1 append ts
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
		}
		return false;
	}

	// Or({Any, Type xs...})
	public static boolean reduce_5e544f725e7b5e54416e795e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e54416e79(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int j6 = 0;
			int[] t6 = new int[r3.size()-1];
			for(int i6=0;i6!=r3.size();++i6) {
				int r6 = r3.get(i6);
				if(i6 == i4 || !typeof_5e5454797065(r6,automaton)) { continue; }
				
				t6[j6++] = r6;
			}
			Automaton.Set r7 = new Automaton.Set(t6);
	
			Automaton.Term r8 = Any;
			int r9 = automaton.add(r8);
			if(r0 != r9) {
				automaton.rewrite(r0, r9);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Or({Void, Type xs...})
	public static boolean reduce_5e544f725e7b5e54566f69645e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e54566f6964(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int j6 = 0;
			int[] t6 = new int[r3.size()-1];
			for(int i6=0;i6!=r3.size();++i6) {
				int r6 = r3.get(i6);
				if(i6 == i4 || !typeof_5e5454797065(r6,automaton)) { continue; }
				
				t6[j6++] = r6;
			}
			Automaton.Set r7 = new Automaton.Set(t6);
	
			int r8 = automaton.add(r7);
			Automaton.Term r9 = new Automaton.Term(K_Or,r8);
			int r10 = automaton.add(r9);
			if(r0 != r10) {
				automaton.rewrite(r0, r10);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term Ref(^Type)
	public final static int K_Ref = 9;
	public final static int Ref(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Ref, r0));
	}

	// Ref(Void)
	public static boolean reduce_5e545265665e54566f6964(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
	
		Automaton.Term r4 = Void;
		int r5 = automaton.add(r4);
		if(r0 != r5) {
			automaton.rewrite(r0, r5);
			numReductions++;
			return true;
		}
		return false;
	}

	// And({Ref(Type t1), Ref(Type t2), Type ts...})
	public static boolean reduce_5e54416e645e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e545265665e5454797065(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Term r7 = (Automaton.Term) automaton.get(r6);
			for(int i8=0;i8!=r3.size();++i8) {
				int r8 = r3.get(i8);
				if(i8 == i4 || !typeof_5e545265665e5454797065(r8,automaton)) { continue; }
				
				Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
				int r10 = r9.contents;
				Automaton.Term r11 = (Automaton.Term) automaton.get(r10);
				int j12 = 0;
				int[] t12 = new int[r3.size()-2];
				for(int i12=0;i12!=r3.size();++i12) {
					int r12 = r3.get(i12);
					if(i12 == i4 || i12 == i8 || !typeof_5e5454797065(r12,automaton)) { continue; }
					
					t12[j12++] = r12;
				}
				Automaton.Set r13 = new Automaton.Set(t12);
	
				Automaton.Set r14 = new Automaton.Set(r6, r10); // {t1t2}
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_And,r15);
				int r17 = automaton.add(r16);
				Automaton.Term r18 = new Automaton.Term(K_Ref,r17);
				int r19 = automaton.add(r18);
				Automaton.Set r20 = r13.appendFront(r19); // Ref(And({t1t2})) append ts
				int r21 = automaton.add(r20);
				Automaton.Term r22 = new Automaton.Term(K_And,r21);
				int r23 = automaton.add(r22);
				if(r0 != r23) {
					automaton.rewrite(r0, r23);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// Or({Ref(Any) t, Ref(Type), Type ts...})
	public static boolean reduce_5e544f725e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e545265665e54416e79(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Term r7 = (Automaton.Term) automaton.get(r6);
			for(int i8=0;i8!=r3.size();++i8) {
				int r8 = r3.get(i8);
				if(i8 == i4 || !typeof_5e545265665e5454797065(r8,automaton)) { continue; }
				
				Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
				int r10 = r9.contents;
				Automaton.Term r11 = (Automaton.Term) automaton.get(r10);
				int j12 = 0;
				int[] t12 = new int[r3.size()-2];
				for(int i12=0;i12!=r3.size();++i12) {
					int r12 = r3.get(i12);
					if(i12 == i4 || i12 == i8 || !typeof_5e5454797065(r12,automaton)) { continue; }
					
					t12[j12++] = r12;
				}
				Automaton.Set r13 = new Automaton.Set(t12);
	
				Automaton.Set r14 = r13.appendFront(r4); // t append ts
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_Or,r15);
				int r17 = automaton.add(r16);
				if(r0 != r17) {
					automaton.rewrite(r0, r17);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// term Meta(^Type)
	public final static int K_Meta = 10;
	public final static int Meta(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Meta, r0));
	}

	// And({Meta(Type t1), Meta(Type t2), Type ts...})
	public static boolean reduce_5e54416e645e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e544d6574615e5454797065(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Term r7 = (Automaton.Term) automaton.get(r6);
			for(int i8=0;i8!=r3.size();++i8) {
				int r8 = r3.get(i8);
				if(i8 == i4 || !typeof_5e544d6574615e5454797065(r8,automaton)) { continue; }
				
				Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
				int r10 = r9.contents;
				Automaton.Term r11 = (Automaton.Term) automaton.get(r10);
				int j12 = 0;
				int[] t12 = new int[r3.size()-2];
				for(int i12=0;i12!=r3.size();++i12) {
					int r12 = r3.get(i12);
					if(i12 == i4 || i12 == i8 || !typeof_5e5454797065(r12,automaton)) { continue; }
					
					t12[j12++] = r12;
				}
				Automaton.Set r13 = new Automaton.Set(t12);
	
				Automaton.Set r14 = new Automaton.Set(r6, r10); // {t1t2}
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_And,r15);
				int r17 = automaton.add(r16);
				Automaton.Term r18 = new Automaton.Term(K_Meta,r17);
				int r19 = automaton.add(r18);
				Automaton.Set r20 = r13.appendFront(r19); // Meta(And({t1t2})) append ts
				int r21 = automaton.add(r20);
				Automaton.Term r22 = new Automaton.Term(K_And,r21);
				int r23 = automaton.add(r22);
				if(r0 != r23) {
					automaton.rewrite(r0, r23);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// Or({Meta(Any) t, Meta(Type), Type ts...})
	public static boolean reduce_5e544f725e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e544d6574615e54416e79(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Term r7 = (Automaton.Term) automaton.get(r6);
			for(int i8=0;i8!=r3.size();++i8) {
				int r8 = r3.get(i8);
				if(i8 == i4 || !typeof_5e544d6574615e5454797065(r8,automaton)) { continue; }
				
				Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
				int r10 = r9.contents;
				Automaton.Term r11 = (Automaton.Term) automaton.get(r10);
				int j12 = 0;
				int[] t12 = new int[r3.size()-2];
				for(int i12=0;i12!=r3.size();++i12) {
					int r12 = r3.get(i12);
					if(i12 == i4 || i12 == i8 || !typeof_5e5454797065(r12,automaton)) { continue; }
					
					t12[j12++] = r12;
				}
				Automaton.Set r13 = new Automaton.Set(t12);
	
				Automaton.Set r14 = r13.appendFront(r4); // t append ts
				int r15 = automaton.add(r14);
				Automaton.Term r16 = new Automaton.Term(K_Or,r15);
				int r17 = automaton.add(r16);
				if(r0 != r17) {
					automaton.rewrite(r0, r17);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// term Term(^[^string,^Type])
	public final static int K_Term = 11;
	public final static int Term(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}
	public final static int Term(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}

	// And({Term([string s1, Type t1]), Term([string s2, Type t2]), Type ts...})
	public static boolean reduce_5e54416e645e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_5e545465726d5e5b5e535e54547970655d(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.List r7 = (Automaton.List) automaton.get(r6);
			int r8 = r7.get(0);
			int r9 = r7.get(1);
			Automaton.Term r10 = (Automaton.Term) automaton.get(r9);
			for(int i11=0;i11!=r3.size();++i11) {
				int r11 = r3.get(i11);
				if(i11 == i4 || !typeof_5e545465726d5e5b5e535e54547970655d(r11,automaton)) { continue; }
				
				Automaton.Term r12 = (Automaton.Term) automaton.get(r11);
				int r13 = r12.contents;
				Automaton.List r14 = (Automaton.List) automaton.get(r13);
				int r15 = r14.get(0);
				int r16 = r14.get(1);
				Automaton.Term r17 = (Automaton.Term) automaton.get(r16);
				int j18 = 0;
				int[] t18 = new int[r3.size()-2];
				for(int i18=0;i18!=r3.size();++i18) {
					int r18 = r3.get(i18);
					if(i18 == i4 || i18 == i11 || !typeof_5e5454797065(r18,automaton)) { continue; }
					
					t18[j18++] = r18;
				}
				Automaton.Set r19 = new Automaton.Set(t18);
	
				boolean r20 = r8 == r15;       // s1 eq s2
				if(r20) {
					Automaton.Set r21 = new Automaton.Set(r9, r16); // {t1t2}
					int r22 = automaton.add(r21);
					Automaton.Term r23 = new Automaton.Term(K_And,r22);
					int r24 = automaton.add(r23);
					Automaton.List r25 = new Automaton.List(r8, r24); // [s1And({t1t2})]
					int r26 = automaton.add(r25);
					Automaton.Term r27 = new Automaton.Term(K_Term,r26);
					int r28 = automaton.add(r27);
					Automaton.Set r29 = r19.appendFront(r28); // Term([s1And({t1t2})]) append ts
					int r30 = automaton.add(r29);
					Automaton.Term r31 = new Automaton.Term(K_And,r30);
					int r32 = automaton.add(r31);
					if(r0 != r32) {
						automaton.rewrite(r0, r32);
						numReductions++;
						return true;
					}
				}
				Automaton.Term r33 = Void;
				int r34 = automaton.add(r33);
				if(r0 != r34) {
					automaton.rewrite(r0, r34);
					numReductions++;
					return true;
				}
			}
		}
		return false;
	}

	// term Fun(^[^Type,^Type])
	public final static int K_Fun = 12;
	public final static int Fun(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}
	public final static int Fun(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}

	// term True
	public final static int K_True = 13;
	public final static Automaton.Term True = new Automaton.Term(K_True);

	// term False
	public final static int K_False = 14;
	public final static Automaton.Term False = new Automaton.Term(K_False);

	// Bool as True | False

	// term Set(^[^Bool,^{^Type...}])
	public final static int K_Set = 15;
	public final static int Set(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}
	public final static int Set(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}

	// Set([Bool b, {Void, Type ts...}])
	public static boolean reduce_5e545365745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
		int r6 = r3.get(1);
		Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
		for(int i8=0;i8!=r7.size();++i8) {
			int r8 = r7.get(i8);
			if(!typeof_5e54566f6964(r8,automaton)) { continue; }
			
			Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
			int j10 = 0;
			int[] t10 = new int[r7.size()-1];
			for(int i10=0;i10!=r7.size();++i10) {
				int r10 = r7.get(i10);
				if(i10 == i8 || !typeof_5e5454797065(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Set r11 = new Automaton.Set(t10);
	
			int r12 = automaton.add(r11);
			Automaton.List r13 = new Automaton.List(r4, r12); // [bts]
			int r14 = automaton.add(r13);
			Automaton.Term r15 = new Automaton.Term(K_Set,r14);
			int r16 = automaton.add(r15);
			if(r0 != r16) {
				automaton.rewrite(r0, r16);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term Bag(^[^Bool,^{|^Type...|}])
	public final static int K_Bag = 16;
	public final static int Bag(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}
	public final static int Bag(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}

	// Bag([Bool b, {|Void, Type ts...|}])
	public static boolean reduce_5e544261675e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
		int r6 = r3.get(1);
		Automaton.Bag r7 = (Automaton.Bag) automaton.get(r6);
		for(int i8=0;i8!=r7.size();++i8) {
			int r8 = r7.get(i8);
			if(!typeof_5e54566f6964(r8,automaton)) { continue; }
			
			Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
			int j10 = 0;
			int[] t10 = new int[r7.size()-1];
			for(int i10=0;i10!=r7.size();++i10) {
				int r10 = r7.get(i10);
				if(i10 == i8 || !typeof_5e5454797065(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Bag r11 = new Automaton.Bag(t10);
	
			int r12 = automaton.add(r11);
			Automaton.List r13 = new Automaton.List(r4, r12); // [bts]
			int r14 = automaton.add(r13);
			Automaton.Term r15 = new Automaton.Term(K_Bag,r14);
			int r16 = automaton.add(r15);
			if(r0 != r16) {
				automaton.rewrite(r0, r16);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term List(^[^Bool,^[^Type...]])
	public final static int K_List = 17;
	public final static int List(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}
	public final static int List(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}

	// List([Bool b, {Void, Type ts...}])
	public static boolean reduce_5e544c6973745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
		int r6 = r3.get(1);
		Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
		for(int i8=0;i8!=r7.size();++i8) {
			int r8 = r7.get(i8);
			if(!typeof_5e54566f6964(r8,automaton)) { continue; }
			
			Automaton.Term r9 = (Automaton.Term) automaton.get(r8);
			int j10 = 0;
			int[] t10 = new int[r7.size()-1];
			for(int i10=0;i10!=r7.size();++i10) {
				int r10 = r7.get(i10);
				if(i10 == i8 || !typeof_5e5454797065(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Set r11 = new Automaton.Set(t10);
	
			Automaton.Term r12 = Void;
			int r13 = automaton.add(r12);
			if(r0 != r13) {
				automaton.rewrite(r0, r13);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Type as Or | And | Not | Ref | Meta | Atom

	public static boolean reduce(Automaton automaton) {
		boolean result = false;
		boolean changed = true;
		while(changed) {
			changed = false;
			for(int i=0;i<automaton.nStates();++i) {
				if(numSteps++ > MAX_STEPS) { return result; } // bail out
				if(automaton.get(i) == null) { continue; }
				
				if(typeof_5e544e6f745e54416e79(i,automaton)) {
					changed |= reduce_5e544e6f745e54416e79(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544e6f745e544f725e7b5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e544e6f745e544f725e7b5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544e6f745e54416e645e7b5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e544e6f745e54416e645e7b5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e54547970657d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e54547970657d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544f725e7b5e54547970657d(i,automaton)) {
					changed |= reduce_5e544f725e7b5e54547970657d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544f725e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e544f725e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e54566f69645e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e54566f69645e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e54416e795e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e54416e795e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544f725e7b5e54416e795e54547970652e7d(i,automaton)) {
					changed |= reduce_5e544f725e7b5e54416e795e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544f725e7b5e54566f69645e54547970652e7d(i,automaton)) {
					changed |= reduce_5e544f725e7b5e54566f69645e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e545265665e54566f6964(i,automaton)) {
					changed |= reduce_5e545265665e54566f6964(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544f725e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(i,automaton)) {
					changed |= reduce_5e544f725e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544f725e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(i,automaton)) {
					changed |= reduce_5e544f725e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e54416e645e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(i,automaton)) {
					changed |= reduce_5e54416e645e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e545365745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(i,automaton)) {
					changed |= reduce_5e545365745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544261675e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(i,automaton)) {
					changed |= reduce_5e544261675e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(i,automaton);
					automaton.compact();
					if(changed) { break; } // reset
				}
				
				if(typeof_5e544c6973745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(i,automaton)) {
					changed |= reduce_5e544c6973745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(i,automaton);
					automaton.compact();
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

	// ^Any
	private static boolean typeof_5e54416e79(int index, Automaton automaton) {
		return typeof_54416e79(automaton.get(index),automaton);
	}

	// Any
	private static boolean typeof_54416e79(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Any)) {
			return true;
		}
		return false;
	}

	// ^Ref(^Void)
	private static boolean typeof_5e545265665e54566f6964(int index, Automaton automaton) {
		return typeof_545265665e54566f6964(automaton.get(index),automaton);
	}

	// Ref(^Void)
	private static boolean typeof_545265665e54566f6964(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Ref)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e54566f6964(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Or(^{^Ref(^Any),^Ref(^Type),^Type...})
	private static boolean typeof_5e544f725e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_544f725e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// Or(^{^Ref(^Any),^Ref(^Type),^Type...})
	private static boolean typeof_544f725e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Or)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Type
	private static boolean typeof_5e5454797065(int index, Automaton automaton) {
		return typeof_5454797065(automaton.get(index),automaton);
	}

	// Type
	private static boolean typeof_5454797065(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Ref
		   || state.kind == K_Real
		   || state.kind == K_Any
		   || state.kind == K_String
		   || state.kind == K_Void
		   || state.kind == K_Or
		   || state.kind == K_True
		   || state.kind == K_Meta
		   || state.kind == K_Int
		   || state.kind == K_False
		   || state.kind == K_And
		   || state.kind == K_Not)) {
			return true;
		}
		return false;
	}

	// ^Void
	private static boolean typeof_5e54566f6964(int index, Automaton automaton) {
		return typeof_54566f6964(automaton.get(index),automaton);
	}

	// Void
	private static boolean typeof_54566f6964(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Void)) {
			return true;
		}
		return false;
	}

	// ^Ref(^Type)
	private static boolean typeof_5e545265665e5454797065(int index, Automaton automaton) {
		return typeof_545265665e5454797065(automaton.get(index),automaton);
	}

	// Ref(^Type)
	private static boolean typeof_545265665e5454797065(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Ref)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e5454797065(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{^Any,^Type...})
	private static boolean typeof_5e54416e645e7b5e54416e795e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e54416e795e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Any,^Type...})
	private static boolean typeof_54416e645e7b5e54416e795e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54416e795e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{^Void,^Type...})
	private static boolean typeof_5e54416e645e7b5e54566f69645e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e54566f69645e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Void,^Type...})
	private static boolean typeof_54416e645e7b5e54566f69645e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54566f69645e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{^Term(^[^string,^Type]),^Term(^[^string,^Type]),^Type...})
	private static boolean typeof_5e54416e645e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Term(^[^string,^Type]),^Term(^[^string,^Type]),^Type...})
	private static boolean typeof_54416e645e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Ref(^Any),^Ref(^Type),^Type...}
	private static boolean typeof_5e7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Ref(^Any),^Ref(^Type),^Type...}
	private static boolean typeof_7b5e545265665e54416e795e545265665e54547970655e54547970652e7d(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_5e545265665e54416e79(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_5e545265665e5454797065(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^And(^{^Type})
	private static boolean typeof_5e54416e645e7b5e54547970657d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e54547970657d(automaton.get(index),automaton);
	}

	// And(^{^Type})
	private static boolean typeof_54416e645e7b5e54547970657d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54547970657d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^Or(^{^Type...}))
	private static boolean typeof_5e544e6f745e544f725e7b5e54547970652e7d(int index, Automaton automaton) {
		return typeof_544e6f745e544f725e7b5e54547970652e7d(automaton.get(index),automaton);
	}

	// Not(^Or(^{^Type...}))
	private static boolean typeof_544e6f745e544f725e7b5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Not)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e544f725e7b5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{^Type...})
	private static boolean typeof_5e54416e645e7b5e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Type...})
	private static boolean typeof_54416e645e7b5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton
	private static boolean typeof_5e5450726f746f6e(int index, Automaton automaton) {
		return typeof_5450726f746f6e(automaton.get(index),automaton);
	}

	// Proton
	private static boolean typeof_5450726f746f6e(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Real
		   || state.kind == K_Any
		   || state.kind == K_String
		   || state.kind == K_Void
		   || state.kind == K_True
		   || state.kind == K_Int
		   || state.kind == K_False)) {
			return true;
		}
		return false;
	}

	// ^Or(^{^Meta(^Any),^Meta(^Type),^Type...})
	private static boolean typeof_5e544f725e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_544f725e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// Or(^{^Meta(^Any),^Meta(^Type),^Type...})
	private static boolean typeof_544f725e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Or)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Meta(^Any),^Meta(^Type),^Type...}
	private static boolean typeof_5e7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Meta(^Any),^Meta(^Type),^Type...}
	private static boolean typeof_7b5e544d6574615e54416e795e544d6574615e54547970655e54547970652e7d(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_5e544d6574615e54416e79(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_5e544d6574615e5454797065(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^Or(^{^Void,^Type...})
	private static boolean typeof_5e544f725e7b5e54566f69645e54547970652e7d(int index, Automaton automaton) {
		return typeof_544f725e7b5e54566f69645e54547970652e7d(automaton.get(index),automaton);
	}

	// Or(^{^Void,^Type...})
	private static boolean typeof_544f725e7b5e54566f69645e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Or)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54566f69645e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Or(^{^Or(^{^Type...}),^Type...})
	private static boolean typeof_5e544f725e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(int index, Automaton automaton) {
		return typeof_544f725e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(automaton.get(index),automaton);
	}

	// Or(^{^Or(^{^Type...}),^Type...})
	private static boolean typeof_544f725e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Or)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^List(^[^Bool,^{^Void,^Type...}])
	private static boolean typeof_5e544c6973745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(int index, Automaton automaton) {
		return typeof_544c6973745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(automaton.get(index),automaton);
	}

	// List(^[^Bool,^{^Void,^Type...}])
	private static boolean typeof_544c6973745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_List)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^Bool,^{^Void,^Type...}]
	private static boolean typeof_5e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(int index, Automaton automaton) {
		return typeof_5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(automaton.get(index),automaton);
	}

	// [^Bool,^{^Void,^Type...}]
	private static boolean typeof_5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_5e54426f6f6c(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_5e7b5e54566f69645e54547970652e7d(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^Bool
	private static boolean typeof_5e54426f6f6c(int index, Automaton automaton) {
		return typeof_54426f6f6c(automaton.get(index),automaton);
	}

	// Bool
	private static boolean typeof_54426f6f6c(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_True
		   || state.kind == K_False)) {
			return true;
		}
		return false;
	}

	// ^Or(^{^Type})
	private static boolean typeof_5e544f725e7b5e54547970657d(int index, Automaton automaton) {
		return typeof_544f725e7b5e54547970657d(automaton.get(index),automaton);
	}

	// Or(^{^Type})
	private static boolean typeof_544f725e7b5e54547970657d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Or)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54547970657d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Or(^{^Type...})
	private static boolean typeof_5e544f725e7b5e54547970652e7d(int index, Automaton automaton) {
		return typeof_544f725e7b5e54547970652e7d(automaton.get(index),automaton);
	}

	// Or(^{^Type...})
	private static boolean typeof_544f725e7b5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Or)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Meta(^Any)
	private static boolean typeof_5e544d6574615e54416e79(int index, Automaton automaton) {
		return typeof_544d6574615e54416e79(automaton.get(index),automaton);
	}

	// Meta(^Any)
	private static boolean typeof_544d6574615e54416e79(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Meta)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e54416e79(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{^Proton,^Proton,^Type...})
	private static boolean typeof_5e54416e645e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Proton,^Proton,^Type...})
	private static boolean typeof_54416e645e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Set(^[^Bool,^{^Void,^Type...}])
	private static boolean typeof_5e545365745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(int index, Automaton automaton) {
		return typeof_545365745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(automaton.get(index),automaton);
	}

	// Set(^[^Bool,^{^Void,^Type...}])
	private static boolean typeof_545365745e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Set)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e5b5e54426f6f6c5e7b5e54566f69645e54547970652e7d5d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Any,^Type...}
	private static boolean typeof_5e7b5e54416e795e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e54416e795e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Any,^Type...}
	private static boolean typeof_7b5e54416e795e54547970652e7d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_5e54416e79(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^{^Type...}
	private static boolean typeof_5e7b5e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Type...}
	private static boolean typeof_7b5e54547970652e7d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Type}
	private static boolean typeof_5e7b5e54547970657d(int index, Automaton automaton) {
		return typeof_7b5e54547970657d(automaton.get(index),automaton);
	}

	// {^Type}
	private static boolean typeof_7b5e54547970657d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^{^Void,^Type...}
	private static boolean typeof_5e7b5e54566f69645e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e54566f69645e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Void,^Type...}
	private static boolean typeof_7b5e54566f69645e54547970652e7d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_5e54566f6964(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^And(^{^Meta(^Type),^Meta(^Type),^Type...})
	private static boolean typeof_5e54416e645e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Meta(^Type),^Meta(^Type),^Type...})
	private static boolean typeof_54416e645e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Meta(^Type),^Meta(^Type),^Type...}
	private static boolean typeof_5e7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Meta(^Type),^Meta(^Type),^Type...}
	private static boolean typeof_7b5e544d6574615e54547970655e544d6574615e54547970655e54547970652e7d(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_5e544d6574615e5454797065(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_5e544d6574615e5454797065(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^{^Term(^[^string,^Type]),^Term(^[^string,^Type]),^Type...}
	private static boolean typeof_5e7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Term(^[^string,^Type]),^Term(^[^string,^Type]),^Type...}
	private static boolean typeof_7b5e545465726d5e5b5e535e54547970655d5e545465726d5e5b5e535e54547970655d5e54547970652e7d(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_5e545465726d5e5b5e535e54547970655d(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_5e545465726d5e5b5e535e54547970655d(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^Term(^[^string,^Type])
	private static boolean typeof_5e545465726d5e5b5e535e54547970655d(int index, Automaton automaton) {
		return typeof_545465726d5e5b5e535e54547970655d(automaton.get(index),automaton);
	}

	// Term(^[^string,^Type])
	private static boolean typeof_545465726d5e5b5e535e54547970655d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Term)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e5b5e535e54547970655d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{^Ref(^Type),^Ref(^Type),^Type...})
	private static boolean typeof_5e54416e645e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Ref(^Type),^Ref(^Type),^Type...})
	private static boolean typeof_54416e645e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Ref(^Type),^Ref(^Type),^Type...}
	private static boolean typeof_5e7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Ref(^Type),^Ref(^Type),^Type...}
	private static boolean typeof_7b5e545265665e54547970655e545265665e54547970655e54547970652e7d(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_5e545265665e5454797065(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_5e545265665e5454797065(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^Or(^{^Any,^Type...})
	private static boolean typeof_5e544f725e7b5e54416e795e54547970652e7d(int index, Automaton automaton) {
		return typeof_544f725e7b5e54416e795e54547970652e7d(automaton.get(index),automaton);
	}

	// Or(^{^Any,^Type...})
	private static boolean typeof_544f725e7b5e54416e795e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Or)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54416e795e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^Proton)
	private static boolean typeof_5e544e6f745e5450726f746f6e(int index, Automaton automaton) {
		return typeof_544e6f745e5450726f746f6e(automaton.get(index),automaton);
	}

	// Not(^Proton)
	private static boolean typeof_544e6f745e5450726f746f6e(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Not)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e5450726f746f6e(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^Any)
	private static boolean typeof_5e544e6f745e54416e79(int index, Automaton automaton) {
		return typeof_544e6f745e54416e79(automaton.get(index),automaton);
	}

	// Not(^Any)
	private static boolean typeof_544e6f745e54416e79(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Not)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e54416e79(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Or(^{^Type...}),^Type...}
	private static boolean typeof_5e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e544f725e7b5e54547970652e7d5e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Or(^{^Type...}),^Type...}
	private static boolean typeof_7b5e544f725e7b5e54547970652e7d5e54547970652e7d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_5e544f725e7b5e54547970652e7d(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^And(^{^And(^{^Type...}),^Type...})
	private static boolean typeof_5e54416e645e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^And(^{^Type...}),^Type...})
	private static boolean typeof_54416e645e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^And(^{^Type...}),^Type...}
	private static boolean typeof_5e7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(automaton.get(index),automaton);
	}

	// {^And(^{^Type...}),^Type...}
	private static boolean typeof_7b5e54416e645e7b5e54547970652e7d5e54547970652e7d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_5e54416e645e7b5e54547970652e7d(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^And(^{^Or(^{^Type...}),^Type...})
	private static boolean typeof_5e54416e645e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Or(^{^Type...}),^Type...})
	private static boolean typeof_54416e645e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e544f725e7b5e54547970652e7d5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{^Proton,^Not(^Proton),^Type...})
	private static boolean typeof_5e54416e645e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(int index, Automaton automaton) {
		return typeof_54416e645e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(automaton.get(index),automaton);
	}

	// And(^{^Proton,^Not(^Proton),^Type...})
	private static boolean typeof_54416e645e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_And)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Proton,^Not(^Proton),^Type...}
	private static boolean typeof_5e7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Proton,^Not(^Proton),^Type...}
	private static boolean typeof_7b5e5450726f746f6e5e544e6f745e5450726f746f6e5e54547970652e7d(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_5e5450726f746f6e(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_5e544e6f745e5450726f746f6e(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^{^Proton,^Proton,^Type...}
	private static boolean typeof_5e7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(int index, Automaton automaton) {
		return typeof_7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(automaton.get(index),automaton);
	}

	// {^Proton,^Proton,^Type...}
	private static boolean typeof_7b5e5450726f746f6e5e5450726f746f6e5e54547970652e7d(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_5e5450726f746f6e(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_5e5450726f746f6e(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// ^Meta(^Type)
	private static boolean typeof_5e544d6574615e5454797065(int index, Automaton automaton) {
		return typeof_544d6574615e5454797065(automaton.get(index),automaton);
	}

	// Meta(^Type)
	private static boolean typeof_544d6574615e5454797065(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Meta)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e5454797065(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^And(^{^Type...}))
	private static boolean typeof_5e544e6f745e54416e645e7b5e54547970652e7d(int index, Automaton automaton) {
		return typeof_544e6f745e54416e645e7b5e54547970652e7d(automaton.get(index),automaton);
	}

	// Not(^And(^{^Type...}))
	private static boolean typeof_544e6f745e54416e645e7b5e54547970652e7d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Not)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e54416e645e7b5e54547970652e7d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Ref(^Any)
	private static boolean typeof_5e545265665e54416e79(int index, Automaton automaton) {
		return typeof_545265665e54416e79(automaton.get(index),automaton);
	}

	// Ref(^Any)
	private static boolean typeof_545265665e54416e79(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Ref)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e54416e79(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^string,^Type]
	private static boolean typeof_5e5b5e535e54547970655d(int index, Automaton automaton) {
		return typeof_5b5e535e54547970655d(automaton.get(index),automaton);
	}

	// [^string,^Type]
	private static boolean typeof_5b5e535e54547970655d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_5e53(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^string
	private static boolean typeof_5e53(int index, Automaton automaton) {
		return typeof_53(automaton.get(index),automaton);
	}

	// string
	private static boolean typeof_53(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_STRING;
	}

	// ^Bag(^[^Bool,^{|^Void,^Type...|}])
	private static boolean typeof_5e544261675e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(int index, Automaton automaton) {
		return typeof_544261675e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(automaton.get(index),automaton);
	}

	// Bag(^[^Bool,^{|^Void,^Type...|}])
	private static boolean typeof_544261675e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && (
		 state.kind == K_Bag)) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_5e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^Bool,^{|^Void,^Type...|}]
	private static boolean typeof_5e5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(int index, Automaton automaton) {
		return typeof_5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(automaton.get(index),automaton);
	}

	// [^Bool,^{|^Void,^Type...|}]
	private static boolean typeof_5b5e54426f6f6c5e7c5e54566f69645e54547970652e7c5d(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_5e54426f6f6c(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_5e7c5e54566f69645e54547970652e7c(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Void,^Type...|}
	private static boolean typeof_5e7c5e54566f69645e54547970652e7c(int index, Automaton automaton) {
		return typeof_7c5e54566f69645e54547970652e7c(automaton.get(index),automaton);
	}

	// {|^Void,^Type...|}
	private static boolean typeof_7c5e54566f69645e54547970652e7c(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_5e54566f6964(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_5e5454797065(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// =========================================================================
	// Schema
	// =========================================================================

	public static final Type.Term[] SCHEMA = new Type.Term[]{
		Type.T_TERM("Not",Type.T_REF(Type.T_TERM("Type",null))),
		Type.T_TERM("And",Type.T_REF(Type.T_SET(true,Type.T_REF(Type.T_TERM("Type",null))))),
		Type.T_TERM("Or",Type.T_REF(Type.T_SET(true,Type.T_REF(Type.T_TERM("Type",null))))),
		Type.T_TERM("Any",null),
		Type.T_TERM("Void",null),
		Type.T_TERM("Bool",null),
		Type.T_TERM("Int",null),
		Type.T_TERM("Real",null),
		Type.T_TERM("String",null),
		Type.T_TERM("Ref",Type.T_REF(Type.T_TERM("Type",null))),
		Type.T_TERM("Meta",Type.T_REF(Type.T_TERM("Type",null))),
		Type.T_TERM("Term",Type.T_REF(Type.T_LIST(false,Type.T_REF(Type.T_STRING()),Type.T_REF(Type.T_TERM("Type",null))))),
		Type.T_TERM("Fun",Type.T_REF(Type.T_LIST(false,Type.T_REF(Type.T_TERM("Type",null)),Type.T_REF(Type.T_TERM("Type",null))))),
		Type.T_TERM("True",null),
		Type.T_TERM("False",null),
		Type.T_TERM("Set",Type.T_REF(Type.T_LIST(false,Type.T_REF(Type.T_TERM("Bool",null)),Type.T_REF(Type.T_SET(true,Type.T_REF(Type.T_TERM("Type",null))))))),
		Type.T_TERM("Bag",Type.T_REF(Type.T_LIST(false,Type.T_REF(Type.T_TERM("Bool",null)),Type.T_REF(Type.T_SET(true,Type.T_REF(Type.T_TERM("Type",null))))))),
		Type.T_TERM("List",Type.T_REF(Type.T_LIST(false,Type.T_REF(Type.T_TERM("Bool",null)),Type.T_REF(Type.T_LIST(true,Type.T_REF(Type.T_TERM("Type",null)))))))
	};
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
			System.out.println();
			System.out.println("(Reductions=" + numReductions + ", Inferences=" + numInferences + ", Misinferences=" + numMisinferences + ", steps = " + numSteps + ")");
		} catch(PrettyAutomataReader.SyntaxError ex) {
			System.err.println(ex.getMessage());
		}
	}
}
