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

public final class Arithmetic {
	// term Var(^string)
	public final static int K_Var = 0;
	public final static int Var(Automaton automaton, String r0) {
		int r1 = automaton.add(new Automaton.Strung(r0));
		return automaton.add(new Automaton.Term(K_Var, r1));
	}

	// term $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	public final static int K_Mul = 1;
	public final static int Mul(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Mul, r1));
	}
	public final static int Mul(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Mul, r1));
	}

	// Mul([real n, {|$4<AExpr> rest...|}])
	public static boolean reduce_0(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		int j7 = 0;
		int[] t7 = new int[r6.size()-0];
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_1(r7,automaton)) { continue; }
			
			t7[j7++] = r7;
		}
		Automaton.Bag r8 = new Automaton.Bag(t7);
	
		Automaton.Real r9 = new Automaton.Real(0); // 0.0
		Automaton.Real r10 = (Automaton.Real) automaton.get(r4);
		boolean r11 = r10.equals(r9);  // n eq 0.0
		Automaton.Int r12 = r8.lengthOf(); // |rest|
		Automaton.Int r13 = new Automaton.Int(0); // 0
		boolean r14 = r12.equals(r13); // |rest| eq 0
		boolean r15 = r11 || r14;      // n eq 0.0 || |rest| eq 0
		if(r15) {
			Automaton.Term r16 = new Automaton.Term(K_Num,r4);
			int r17 = automaton.add(r16);
			if(r0 != r17) {
				automaton.rewrite(r0, r17);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Mul([real x, {|Num(real y), $4<AExpr> rest...|}])
	public static boolean reduce_2(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_3(r7,automaton)) { continue; }
			
			Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
			int r9 = r8.contents;
			int j10 = 0;
			int[] t10 = new int[r6.size()-1];
			for(int i10=0;i10!=r6.size();++i10) {
				int r10 = r6.get(i10);
				if(i10 == i7 || !typeof_1(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Bag r11 = new Automaton.Bag(t10);
	
			Automaton.Real r12 = (Automaton.Real) automaton.get(r4);
			Automaton.Real r13 = (Automaton.Real) automaton.get(r9);
			Automaton.Real r14 = r12.multiply(r13); // x mul y
			int r15 = automaton.add(r14);
			int r16 = automaton.add(r11);
			Automaton.List r17 = new Automaton.List(r15, r16); // [x mul yrest]
			int r18 = automaton.add(r17);
			Automaton.Term r19 = new Automaton.Term(K_Mul,r18);
			int r20 = automaton.add(r19);
			if(r0 != r20) {
				automaton.rewrite(r0, r20);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Mul([real n1, {|Mul([real n2, {|$4<AExpr> xs...|}]), $4<AExpr> ys...|}])
	public static boolean reduce_4(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_0(r7,automaton)) { continue; }
			
			Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
			int r9 = r8.contents;
			Automaton.List r10 = (Automaton.List) automaton.get(r9);
			int r11 = r10.get(0);
			int r12 = r10.get(1);
			Automaton.Bag r13 = (Automaton.Bag) automaton.get(r12);
			int j14 = 0;
			int[] t14 = new int[r13.size()-0];
			for(int i14=0;i14!=r13.size();++i14) {
				int r14 = r13.get(i14);
				if(!typeof_1(r14,automaton)) { continue; }
				
				t14[j14++] = r14;
			}
			Automaton.Bag r15 = new Automaton.Bag(t14);
			int j16 = 0;
			int[] t16 = new int[r6.size()-1];
			for(int i16=0;i16!=r6.size();++i16) {
				int r16 = r6.get(i16);
				if(i16 == i7 || !typeof_1(r16,automaton)) { continue; }
				
				t16[j16++] = r16;
			}
			Automaton.Bag r17 = new Automaton.Bag(t16);
	
			Automaton.Real r18 = (Automaton.Real) automaton.get(r4);
			Automaton.Real r19 = (Automaton.Real) automaton.get(r11);
			Automaton.Real r20 = r18.multiply(r19); // n1 mul n2
			int r21 = automaton.add(r20);
			Automaton.Bag r22 = r15.append(r17); // xs append ys
			int r23 = automaton.add(r22);
			Automaton.List r24 = new Automaton.List(r21, r23); // [n1 mul n2xs append ys]
			int r25 = automaton.add(r24);
			Automaton.Term r26 = new Automaton.Term(K_Mul,r25);
			int r27 = automaton.add(r26);
			if(r0 != r27) {
				automaton.rewrite(r0, r27);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Mul([real n1, {|Sum([real n2, {|$4<AExpr> xs...|}]), $4<AExpr> ys...|}])
	public static boolean reduce_5(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_6(r7,automaton)) { continue; }
			
			Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
			int r9 = r8.contents;
			Automaton.List r10 = (Automaton.List) automaton.get(r9);
			int r11 = r10.get(0);
			int r12 = r10.get(1);
			Automaton.Bag r13 = (Automaton.Bag) automaton.get(r12);
			int j14 = 0;
			int[] t14 = new int[r13.size()-0];
			for(int i14=0;i14!=r13.size();++i14) {
				int r14 = r13.get(i14);
				if(!typeof_1(r14,automaton)) { continue; }
				
				t14[j14++] = r14;
			}
			Automaton.Bag r15 = new Automaton.Bag(t14);
			int j16 = 0;
			int[] t16 = new int[r6.size()-1];
			for(int i16=0;i16!=r6.size();++i16) {
				int r16 = r6.get(i16);
				if(i16 == i7 || !typeof_1(r16,automaton)) { continue; }
				
				t16[j16++] = r16;
			}
			Automaton.Bag r17 = new Automaton.Bag(t16);
	
		Automaton.List t18 = new Automaton.List();
		for(int i19=0;i19<r15.size();i19++) {
			int r19 = (int) r15.get(i19);
			Automaton.Bag r20 = r17.appendFront(r19); // x append ys
			int r21 = automaton.add(r20);
			Automaton.List r22 = new Automaton.List(r4, r21); // [n1x append ys]
			int r23 = automaton.add(r22);
			Automaton.Term r24 = new Automaton.Term(K_Mul,r23);
			int r25 = automaton.add(r24);
			t18.add(r25);
		}
		Automaton.Bag r18 = new Automaton.Bag(t18.toArray());
			Automaton.Real r26 = (Automaton.Real) automaton.get(r4);
			Automaton.Real r27 = (Automaton.Real) automaton.get(r11);
			Automaton.Real r28 = r26.multiply(r27); // n1 mul n2
			int r29 = automaton.add(r28);
			int r30 = automaton.add(r18);
			Automaton.List r31 = new Automaton.List(r29, r30); // [n1 mul n2ys]
			int r32 = automaton.add(r31);
			Automaton.Term r33 = new Automaton.Term(K_Sum,r32);
			int r34 = automaton.add(r33);
			if(r0 != r34) {
				automaton.rewrite(r0, r34);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term $7<Div(^[$1<^AExpr>,$1])>
	public final static int K_Div = 2;
	public final static int Div(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Div, r1));
	}
	public final static int Div(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Div, r1));
	}

	// term $11<Sum($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	public final static int K_Sum = 3;
	public final static int Sum(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Sum, r1));
	}
	public final static int Sum(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Sum, r1));
	}

	// Sum([real n, {||}])
	public static boolean reduce_7(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
	
		Automaton.Term r7 = new Automaton.Term(K_Num,r4);
		int r8 = automaton.add(r7);
		if(r0 != r8) {
			automaton.rewrite(r0, r8);
			numReductions++;
			return true;
		}
		return false;
	}

	// Sum([real n, {|$4<AExpr> x, $4<AExpr> rest...|}])
	public static boolean reduce_8(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_1(r7,automaton)) { continue; }
			
			int j8 = 0;
			int[] t8 = new int[r6.size()-1];
			for(int i8=0;i8!=r6.size();++i8) {
				int r8 = r6.get(i8);
				if(i8 == i7 || !typeof_1(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Bag r9 = new Automaton.Bag(t8);
	
			boolean r10 = typeof_9(r7,automaton); // x is ^NumSumMul
			boolean r11 = !r10;            // !x is ^NumSumMul
			if(r11) {
				Automaton.Real r12 = new Automaton.Real(1); // 1.0
				int r13 = automaton.add(r12);
				Automaton.Bag r14 = new Automaton.Bag(r7); // {|x|}
				int r15 = automaton.add(r14);
				Automaton.List r16 = new Automaton.List(r13, r15); // [1.0{|x|}]
				int r17 = automaton.add(r16);
				Automaton.Term r18 = new Automaton.Term(K_Mul,r17);
				int r19 = automaton.add(r18);
				Automaton.Bag r20 = r9.appendFront(r19); // Mul([1.0{|x|}]) append rest
				int r21 = automaton.add(r20);
				Automaton.List r22 = new Automaton.List(r4, r21); // [nMul([1.0{|x|}]) append rest]
				int r23 = automaton.add(r22);
				Automaton.Term r24 = new Automaton.Term(K_Sum,r23);
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

	// Sum([real x, {|Num(real y), $4<AExpr> rest...|}])
	public static boolean reduce_10(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_3(r7,automaton)) { continue; }
			
			Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
			int r9 = r8.contents;
			int j10 = 0;
			int[] t10 = new int[r6.size()-1];
			for(int i10=0;i10!=r6.size();++i10) {
				int r10 = r6.get(i10);
				if(i10 == i7 || !typeof_1(r10,automaton)) { continue; }
				
				t10[j10++] = r10;
			}
			Automaton.Bag r11 = new Automaton.Bag(t10);
	
			Automaton.Real r12 = (Automaton.Real) automaton.get(r4);
			Automaton.Real r13 = (Automaton.Real) automaton.get(r9);
			Automaton.Real r14 = r12.add(r13); // x add y
			int r15 = automaton.add(r14);
			int r16 = automaton.add(r11);
			Automaton.List r17 = new Automaton.List(r15, r16); // [x add yrest]
			int r18 = automaton.add(r17);
			Automaton.Term r19 = new Automaton.Term(K_Sum,r18);
			int r20 = automaton.add(r19);
			if(r0 != r20) {
				automaton.rewrite(r0, r20);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Sum([real n, {|Mul([real x, {|$4<AExpr>...|} xs]), Mul([real y, {|$4<AExpr>...|} ys]), $4<AExpr> zs...|}])
	public static boolean reduce_11(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_0(r7,automaton)) { continue; }
			
			Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
			int r9 = r8.contents;
			Automaton.List r10 = (Automaton.List) automaton.get(r9);
			int r11 = r10.get(0);
			int r12 = r10.get(1);
			Automaton.Bag r13 = (Automaton.Bag) automaton.get(r12);
			int j14 = 0;
			int[] t14 = new int[r13.size()-0];
			for(int i14=0;i14!=r13.size();++i14) {
				int r14 = r13.get(i14);
				if(!typeof_1(r14,automaton)) { continue; }
				
				t14[j14++] = r14;
			}
			Automaton.Bag r15 = new Automaton.Bag(t14);
			for(int i16=0;i16!=r6.size();++i16) {
				int r16 = r6.get(i16);
				if(i16 == i7 || !typeof_0(r16,automaton)) { continue; }
				
				Automaton.Term r17 = (Automaton.Term) automaton.get(r16);
				int r18 = r17.contents;
				Automaton.List r19 = (Automaton.List) automaton.get(r18);
				int r20 = r19.get(0);
				int r21 = r19.get(1);
				Automaton.Bag r22 = (Automaton.Bag) automaton.get(r21);
				int j23 = 0;
				int[] t23 = new int[r22.size()-0];
				for(int i23=0;i23!=r22.size();++i23) {
					int r23 = r22.get(i23);
					if(!typeof_1(r23,automaton)) { continue; }
					
					t23[j23++] = r23;
				}
				Automaton.Bag r24 = new Automaton.Bag(t23);
				int j25 = 0;
				int[] t25 = new int[r6.size()-2];
				for(int i25=0;i25!=r6.size();++i25) {
					int r25 = r6.get(i25);
					if(i25 == i7 || i25 == i16 || !typeof_1(r25,automaton)) { continue; }
					
					t25[j25++] = r25;
				}
				Automaton.Bag r26 = new Automaton.Bag(t25);
	
				boolean r27 = r12 == r21;      // xs eq ys
				if(r27) {
					Automaton.Real r28 = (Automaton.Real) automaton.get(r11);
					Automaton.Real r29 = (Automaton.Real) automaton.get(r20);
					Automaton.Real r30 = r28.add(r29); // x add y
					int r31 = automaton.add(r30);
					Automaton.List r32 = new Automaton.List(r31, r12); // [x add yxs]
					int r33 = automaton.add(r32);
					Automaton.Term r34 = new Automaton.Term(K_Mul,r33);
					int r35 = automaton.add(r34);
					Automaton.Bag r36 = r26.appendFront(r35); // Mul([x add yxs]) append zs
					int r37 = automaton.add(r36);
					Automaton.List r38 = new Automaton.List(r4, r37); // [nMul([x add yxs]) append zs]
					int r39 = automaton.add(r38);
					Automaton.Term r40 = new Automaton.Term(K_Sum,r39);
					int r41 = automaton.add(r40);
					if(r0 != r41) {
						automaton.rewrite(r0, r41);
						numReductions++;
						return true;
					}
				}
			}
		}
		return false;
	}

	// Sum([real x, {|Sum([real y, {|$4<AExpr> ys...|}]), $4<AExpr> xs...|}])
	public static boolean reduce_12(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_6(r7,automaton)) { continue; }
			
			Automaton.Term r8 = (Automaton.Term) automaton.get(r7);
			int r9 = r8.contents;
			Automaton.List r10 = (Automaton.List) automaton.get(r9);
			int r11 = r10.get(0);
			int r12 = r10.get(1);
			Automaton.Bag r13 = (Automaton.Bag) automaton.get(r12);
			int j14 = 0;
			int[] t14 = new int[r13.size()-0];
			for(int i14=0;i14!=r13.size();++i14) {
				int r14 = r13.get(i14);
				if(!typeof_1(r14,automaton)) { continue; }
				
				t14[j14++] = r14;
			}
			Automaton.Bag r15 = new Automaton.Bag(t14);
			int j16 = 0;
			int[] t16 = new int[r6.size()-1];
			for(int i16=0;i16!=r6.size();++i16) {
				int r16 = r6.get(i16);
				if(i16 == i7 || !typeof_1(r16,automaton)) { continue; }
				
				t16[j16++] = r16;
			}
			Automaton.Bag r17 = new Automaton.Bag(t16);
	
			Automaton.Real r18 = (Automaton.Real) automaton.get(r4);
			Automaton.Real r19 = (Automaton.Real) automaton.get(r11);
			Automaton.Real r20 = r18.add(r19); // x add y
			int r21 = automaton.add(r20);
			Automaton.Bag r22 = r17.append(r15); // xs append ys
			int r23 = automaton.add(r22);
			Automaton.List r24 = new Automaton.List(r21, r23); // [x add yxs append ys]
			int r25 = automaton.add(r24);
			Automaton.Term r26 = new Automaton.Term(K_Sum,r25);
			int r27 = automaton.add(r26);
			if(r0 != r27) {
				automaton.rewrite(r0, r27);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// term True
	public final static int K_True = 4;
	public final static Automaton.Term True = new Automaton.Term(K_True);

	// term False
	public final static int K_False = 5;
	public final static Automaton.Term False = new Automaton.Term(K_False);

	// term $6<And($4<^{^BExpr...}>)>
	public final static int K_And = 6;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// And({$4<BExpr> x})
	public static boolean reduce_13(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_14(r4,automaton)) { continue; }
			
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Bool b, $4<BExpr> xs...})
	public static boolean reduce_15(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_16(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_14(r5,automaton)) { continue; }
				
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
	public static boolean reduce_17(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_18(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_14(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_14(r10,automaton)) { continue; }
				
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

	// And({Or({$4<BExpr> xs...}), $4<BExpr> ys...})
	public static boolean reduce_19(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_20(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_14(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_14(r10,automaton)) { continue; }
				
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

	// term $6<Or($4<^{^BExpr...}>)>
	public final static int K_Or = 7;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// Or({$4<BExpr> x})
	public static boolean reduce_21(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_14(r4,automaton)) { continue; }
			
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Or({Bool b, $4<BExpr> xs...})
	public static boolean reduce_22(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_16(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_14(r5,automaton)) { continue; }
				
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

	// Or({Or({$4<BExpr> xs...}), $4<BExpr> ys...})
	public static boolean reduce_23(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_20(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_14(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_14(r10,automaton)) { continue; }
				
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

	// term LE
	public final static int K_LE = 8;
	public final static Automaton.Term LE = new Automaton.Term(K_LE);

	// term LT
	public final static int K_LT = 9;
	public final static Automaton.Term LT = new Automaton.Term(K_LT);

	// term Inequality(^[^Op,$2<^AExpr>])
	public final static int K_Inequality = 10;
	public final static int Inequality(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Inequality, r1));
	}
	public final static int Inequality(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Inequality, r1));
	}

	// Inequality([Op op, Num(real v)])
	public static boolean reduce_24(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Term r6 = (Automaton.Term) automaton.get(r5);
		int r7 = r6.contents;
	
		Automaton.Real r8 = new Automaton.Real(0); // 0.0
		Automaton.Real r9 = (Automaton.Real) automaton.get(r7);
		boolean r10 = r9.equals(r8);   // v eq 0.0
		boolean r11 = false;           // v eq 0.0 && op is ^LT
		if(r10) {
			boolean r12 = typeof_25(r4,automaton); // op is ^LT
			r11 = r12;
		}
		if(r11) {
			Automaton.Term r13 = False;
			int r14 = automaton.add(r13);
			if(r0 != r14) {
				automaton.rewrite(r0, r14);
				numReductions++;
				return true;
			}
		}
		Automaton.Real r15 = new Automaton.Real(0); // 0.0
		Automaton.Real r16 = (Automaton.Real) automaton.get(r7);
		boolean r17 = r16.compareTo(r15)<0; // v lt 0.0
		boolean r18 = false;           // v lt 0.0 && op is ^LT || op is ^LE
		if(r17) {
			boolean r19 = typeof_25(r4,automaton); // op is ^LT
			boolean r20 = typeof_26(r4,automaton); // op is ^LE
			boolean r21 = r19 || r20;      // op is ^LT || op is ^LE
			r18 = r21;
		}
		if(r18) {
			Automaton.Term r22 = False;
			int r23 = automaton.add(r22);
			if(r0 != r23) {
				automaton.rewrite(r0, r23);
				numReductions++;
				return true;
			}
		}
		Automaton.Real r24 = new Automaton.Real(0); // 0.0
		Automaton.Real r25 = (Automaton.Real) automaton.get(r7);
		boolean r26 = r25.equals(r24); // v eq 0.0
		boolean r27 = false;           // v eq 0.0 && op is ^LE
		if(r26) {
			boolean r28 = typeof_26(r4,automaton); // op is ^LE
			r27 = r28;
		}
		if(r27) {
			Automaton.Term r29 = True;
			int r30 = automaton.add(r29);
			if(r0 != r30) {
				automaton.rewrite(r0, r30);
				numReductions++;
				return true;
			}
		}
		Automaton.Real r31 = new Automaton.Real(0); // 0.0
		Automaton.Real r32 = (Automaton.Real) automaton.get(r7);
		boolean r33 = r32.compareTo(r31)>0; // v gt 0.0
		boolean r34 = false;           // v gt 0.0 && op is ^LT || op is ^LE
		if(r33) {
			boolean r35 = typeof_25(r4,automaton); // op is ^LT
			boolean r36 = typeof_26(r4,automaton); // op is ^LE
			boolean r37 = r35 || r36;      // op is ^LT || op is ^LE
			r34 = r37;
		}
		if(r34) {
			Automaton.Term r38 = True;
			int r39 = automaton.add(r38);
			if(r0 != r39) {
				automaton.rewrite(r0, r39);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({Inequality([Op op1, Sum([real x1, {|Mul([real x2, {|$4<AExpr> v1|}]), $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)> xs...|}]) s1]) eq1, Inequality([Op op2, Sum([real y1, {|Mul([real y2, {|$4<AExpr> v2|}]), $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)> ys...|}]) s2]) eq2, $4<BExpr> rest...})
	public static boolean infer_27(int r0, Automaton automaton) {
	Automaton original = new Automaton(automaton);
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_28(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.List r7 = (Automaton.List) automaton.get(r6);
			int r8 = r7.get(0);
			int r9 = r7.get(1);
			Automaton.Term r10 = (Automaton.Term) automaton.get(r9);
			int r11 = r10.contents;
			Automaton.List r12 = (Automaton.List) automaton.get(r11);
			int r13 = r12.get(0);
			int r14 = r12.get(1);
			Automaton.Bag r15 = (Automaton.Bag) automaton.get(r14);
			for(int i16=0;i16!=r15.size();++i16) {
				int r16 = r15.get(i16);
				if(!typeof_29(r16,automaton)) { continue; }
				
				Automaton.Term r17 = (Automaton.Term) automaton.get(r16);
				int r18 = r17.contents;
				Automaton.List r19 = (Automaton.List) automaton.get(r18);
				int r20 = r19.get(0);
				int r21 = r19.get(1);
				Automaton.Bag r22 = (Automaton.Bag) automaton.get(r21);
				for(int i23=0;i23!=r22.size();++i23) {
					int r23 = r22.get(i23);
					if(!typeof_1(r23,automaton)) { continue; }
					
					int j24 = 0;
					int[] t24 = new int[r15.size()-1];
					for(int i24=0;i24!=r15.size();++i24) {
						int r24 = r15.get(i24);
						if(i24 == i16 || !typeof_30(r24,automaton)) { continue; }
						
						t24[j24++] = r24;
					}
					Automaton.Bag r25 = new Automaton.Bag(t24);
					for(int i26=0;i26!=r3.size();++i26) {
						int r26 = r3.get(i26);
						if(i26 == i4 || !typeof_28(r26,automaton)) { continue; }
						
						Automaton.Term r27 = (Automaton.Term) automaton.get(r26);
						int r28 = r27.contents;
						Automaton.List r29 = (Automaton.List) automaton.get(r28);
						int r30 = r29.get(0);
						int r31 = r29.get(1);
						Automaton.Term r32 = (Automaton.Term) automaton.get(r31);
						int r33 = r32.contents;
						Automaton.List r34 = (Automaton.List) automaton.get(r33);
						int r35 = r34.get(0);
						int r36 = r34.get(1);
						Automaton.Bag r37 = (Automaton.Bag) automaton.get(r36);
						for(int i38=0;i38!=r37.size();++i38) {
							int r38 = r37.get(i38);
							if(!typeof_29(r38,automaton)) { continue; }
							
							Automaton.Term r39 = (Automaton.Term) automaton.get(r38);
							int r40 = r39.contents;
							Automaton.List r41 = (Automaton.List) automaton.get(r40);
							int r42 = r41.get(0);
							int r43 = r41.get(1);
							Automaton.Bag r44 = (Automaton.Bag) automaton.get(r43);
							for(int i45=0;i45!=r44.size();++i45) {
								int r45 = r44.get(i45);
								if(!typeof_1(r45,automaton)) { continue; }
								
								int j46 = 0;
								int[] t46 = new int[r37.size()-1];
								for(int i46=0;i46!=r37.size();++i46) {
									int r46 = r37.get(i46);
									if(i46 == i38 || !typeof_30(r46,automaton)) { continue; }
									
									t46[j46++] = r46;
								}
								Automaton.Bag r47 = new Automaton.Bag(t46);
								int j48 = 0;
								int[] t48 = new int[r3.size()-2];
								for(int i48=0;i48!=r3.size();++i48) {
									int r48 = r3.get(i48);
									if(i48 == i4 || i48 == i26 || !typeof_14(r48,automaton)) { continue; }
									
									t48[j48++] = r48;
								}
								Automaton.Set r49 = new Automaton.Set(t48);
	
		Automaton.Real r50 = new Automaton.Real(0); // 0.0
		int r51 = automaton.add(r50);
		Automaton.Bag r52 = new Automaton.Bag(r9); // {|s1|}
		int r53 = automaton.add(r52);
		Automaton.List r54 = new Automaton.List(r42, r53); // [y2{|s1|}]
		int r55 = automaton.add(r54);
		Automaton.Term r56 = new Automaton.Term(K_Mul,r55);
		int r57 = automaton.add(r56);
		Automaton.Real r58 = (Automaton.Real) automaton.get(r20);
		Automaton.Real r59 = r58.negate(); // -x2
		int r60 = automaton.add(r59);
		Automaton.Bag r61 = new Automaton.Bag(r31); // {|s2|}
		int r62 = automaton.add(r61);
		Automaton.List r63 = new Automaton.List(r60, r62); // [-x2{|s2|}]
		int r64 = automaton.add(r63);
		Automaton.Term r65 = new Automaton.Term(K_Mul,r64);
		int r66 = automaton.add(r65);
		Automaton.Bag r67 = new Automaton.Bag(r57, r66); // {|Mul([y2{|s1|}])Mul([-x2{|s2|}])|}
		int r68 = automaton.add(r67);
		Automaton.List r69 = new Automaton.List(r51, r68); // [0.0{|Mul([y2{|s1|}])Mul([-x2{|s2|}])|}]
		int r70 = automaton.add(r69);
		Automaton.Term r71 = new Automaton.Term(K_Sum,r70);
		int r72 = automaton.add(r71);
		Automaton.List r73 = new Automaton.List(r8, r72); // [op1Sum([0.0{|Mul([y2{|s1|}])Mul([-x2{|s2|}])|}])]
		int r74 = automaton.add(r73);
		Automaton.Term r75 = new Automaton.Term(K_Inequality,r74);
								boolean r76 = r8 == r30;       // op1 eq op2
								boolean r77 = false;           // op1 eq op2 && v1 eq v2 && x2 lt 0.0 && y2 gt 0.0
								if(r76) {
									boolean r78 = r23 == r45;      // v1 eq v2
									boolean r79 = false;           // v1 eq v2 && x2 lt 0.0 && y2 gt 0.0
									if(r78) {
										Automaton.Real r80 = new Automaton.Real(0); // 0.0
										Automaton.Real r81 = (Automaton.Real) automaton.get(r20);
										boolean r82 = r81.compareTo(r80)<0; // x2 lt 0.0
										boolean r83 = false;           // x2 lt 0.0 && y2 gt 0.0
										if(r82) {
											Automaton.Real r84 = new Automaton.Real(0); // 0.0
											Automaton.Real r85 = (Automaton.Real) automaton.get(r42);
											boolean r86 = r85.compareTo(r84)>0; // y2 gt 0.0
											r83 = r86;
										}
										r79 = r83;
									}
									r77 = r79;
								}
								if(r77) {
									int r87 = automaton.add(r75);
									Automaton.Set r88 = new Automaton.Set(r4, r26, r87); // {eq1eq2eq3}
									Automaton.Set r89 = r88.append(r49); // {eq1eq2eq3} append rest
									int r90 = automaton.add(r89);
									Automaton.Term r91 = new Automaton.Term(K_And,r90);
									int r92 = automaton.add(r91);
									if(r0 != r92) {
										automaton.rewrite(r0, r92);
										reduce(automaton);
										if(!automaton.equals(original)) { numInferences++; return true; }
										else { numMisinferences++; }
									}
								}
		Automaton.Term r93 = LT;
		int r94 = automaton.add(r93);
		Automaton.Real r95 = new Automaton.Real(0); // 0.0
		int r96 = automaton.add(r95);
		Automaton.Bag r97 = new Automaton.Bag(r9); // {|s1|}
		int r98 = automaton.add(r97);
		Automaton.List r99 = new Automaton.List(r42, r98); // [y2{|s1|}]
		int r100 = automaton.add(r99);
		Automaton.Term r101 = new Automaton.Term(K_Mul,r100);
		int r102 = automaton.add(r101);
		Automaton.Real r103 = (Automaton.Real) automaton.get(r20);
		Automaton.Real r104 = r103.negate(); // -x2
		int r105 = automaton.add(r104);
		Automaton.Bag r106 = new Automaton.Bag(r31); // {|s2|}
		int r107 = automaton.add(r106);
		Automaton.List r108 = new Automaton.List(r105, r107); // [-x2{|s2|}]
		int r109 = automaton.add(r108);
		Automaton.Term r110 = new Automaton.Term(K_Mul,r109);
		int r111 = automaton.add(r110);
		Automaton.Bag r112 = new Automaton.Bag(r102, r111); // {|Mul([y2{|s1|}])Mul([-x2{|s2|}])|}
		int r113 = automaton.add(r112);
		Automaton.List r114 = new Automaton.List(r96, r113); // [0.0{|Mul([y2{|s1|}])Mul([-x2{|s2|}])|}]
		int r115 = automaton.add(r114);
		Automaton.Term r116 = new Automaton.Term(K_Sum,r115);
		int r117 = automaton.add(r116);
		Automaton.List r118 = new Automaton.List(r94, r117); // [LTSum([0.0{|Mul([y2{|s1|}])Mul([-x2{|s2|}])|}])]
		int r119 = automaton.add(r118);
		Automaton.Term r120 = new Automaton.Term(K_Inequality,r119);
								boolean r121 = typeof_25(r8,automaton); // op1 is ^LT
								boolean r122 = typeof_25(r30,automaton); // op2 is ^LT
								boolean r123 = r121 || r122;   // op1 is ^LT || op2 is ^LT
								boolean r124 = false;          // op1 is ^LT || op2 is ^LT && v1 eq v2 && x2 lt 0.0 && y2 gt 0.0
								if(r123) {
									boolean r125 = r23 == r45;     // v1 eq v2
									boolean r126 = false;          // v1 eq v2 && x2 lt 0.0 && y2 gt 0.0
									if(r125) {
										Automaton.Real r127 = new Automaton.Real(0); // 0.0
										Automaton.Real r128 = (Automaton.Real) automaton.get(r20);
										boolean r129 = r128.compareTo(r127)<0; // x2 lt 0.0
										boolean r130 = false;          // x2 lt 0.0 && y2 gt 0.0
										if(r129) {
											Automaton.Real r131 = new Automaton.Real(0); // 0.0
											Automaton.Real r132 = (Automaton.Real) automaton.get(r42);
											boolean r133 = r132.compareTo(r131)>0; // y2 gt 0.0
											r130 = r133;
										}
										r126 = r130;
									}
									r124 = r126;
								}
								if(r124) {
									int r134 = automaton.add(r120);
									Automaton.Set r135 = new Automaton.Set(r4, r26, r134); // {eq1eq2eq3}
									Automaton.Set r136 = r135.append(r49); // {eq1eq2eq3} append rest
									int r137 = automaton.add(r136);
									Automaton.Term r138 = new Automaton.Term(K_And,r137);
									int r139 = automaton.add(r138);
									if(r0 != r139) {
										automaton.rewrite(r0, r139);
										reduce(automaton);
										if(!automaton.equals(original)) { numInferences++; return true; }
										else { numMisinferences++; }
									}
								}
							}
						}
					}
				}
			}
		}
	reduce(automaton);
		return false;
	}

	// term Num(^real)
	public final static int K_Num = 11;
	public final static int Num(Automaton automaton, long r0) {
		int r1 = automaton.add(new Automaton.Real(r0));
		return automaton.add(new Automaton.Term(K_Num, r1));
	}
	public final static int Num(Automaton automaton, BigRational r0) {
		int r1 = automaton.add(new Automaton.Real(r0));
		return automaton.add(new Automaton.Term(K_Num, r1));
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
				
				if(typeof_7(i,automaton)) {
					changed |= reduce_7(i,automaton);
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
				
				if(typeof_13(i,automaton)) {
					changed |= reduce_13(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_15(i,automaton)) {
					changed |= reduce_15(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_17(i,automaton)) {
					changed |= reduce_17(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_19(i,automaton)) {
					changed |= reduce_19(i,automaton);
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
				
				if(typeof_24(i,automaton)) {
					changed |= reduce_24(i,automaton);
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
				
				if(typeof_27(i,automaton) &&
					infer_27(i,automaton)) {
					changed = true; break; // reset
				}
			}
			result |= changed;
		}
		return result;
	}
	// =========================================================================
	// Type Tests
	// =========================================================================

	private final static BitSet visited = new BitSet();

	// ^$22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_0(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_31(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 0);
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

	// $10<^AExpr>
	private static boolean typeof_1(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_32(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1);
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

	// ^Mul(^[^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]])
	private static boolean typeof_2(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_33(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 2);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_33(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Num(^real)
	private static boolean typeof_3(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_34(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 3);
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

	// ^Mul(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_4(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_35(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 4);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_35(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Mul(^[^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_5(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_36(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 5);
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

	// ^$19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_6(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_37(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 6);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_37(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Sum(^[^real,^{||}[]])
	private static boolean typeof_7(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_38(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 7);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_38(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Sum(^[^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]])
	private static boolean typeof_8(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_39(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 8);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_39(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^NumSumMul
	private static boolean typeof_9(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_40(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 9);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_40(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Sum(^[^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]])
	private static boolean typeof_10(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_41(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 10);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_41(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]])
	private static boolean typeof_11(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_42(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 11);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_42(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^Sum(^[^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]])
	private static boolean typeof_12(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_43(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 12);
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

	// ^And(^{$21<^BExpr>})
	private static boolean typeof_13(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_44(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 13);
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

	// $21<^BExpr>
	private static boolean typeof_14(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_45(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 14);
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

	// ^And(^{^Bool$22<^BExpr>...})
	private static boolean typeof_15(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_46(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 15);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_46(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^And(^{$21<^BExpr>...})$21...})
	private static boolean typeof_17(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_47(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 17);
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

	// ^Bool
	private static boolean typeof_16(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_48(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 16);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_48(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^And(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_19(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_49(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 19);
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

	// ^$26<And($24<^{^BExpr...}>)>
	private static boolean typeof_18(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_50(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 18);
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

	// ^Or(^{$22<^BExpr>})
	private static boolean typeof_21(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_51(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 21);
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

	// ^$29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_20(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_52(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 20);
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

	// ^Or(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_23(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_53(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 23);
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

	// ^Or(^{^Bool$23<^BExpr>...})
	private static boolean typeof_22(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_54(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 22);
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

	// ^LT
	private static boolean typeof_25(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_55(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 25);
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

	// ^Inequality(^[^Op,^Num(^real)])
	private static boolean typeof_24(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_56(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 24);
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

	// ^And(^{^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...]])]),^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...]])])$94<^BExpr>...})
	private static boolean typeof_27(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_57(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 27);
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

	// ^LE
	private static boolean typeof_26(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_58(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 26);
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

	// ^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_29(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_59(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 29);
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

	// ^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$24<^AExpr>|}[$24<^AExpr>]])^$35<Mul($31<^[^real,^{|$24...|}[$24...]]>)>...|}[^Mul(^[^real,^{|$24<^AExpr>|}[$24<^AExpr>]])^$35<Mul($31<^[^real,^{|$24...|}[$24...]]>)>...]])])
	private static boolean typeof_28(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_60(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 28);
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

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_31(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_61(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	private static boolean typeof_30(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_62(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 30);
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

	// Num(^real)
	private static boolean typeof_34(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Num) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_63(data,automaton)) { return true; }
		}
		return false;
	}

	// Mul(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_35(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_64(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]]
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

	// [^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]]
	private static boolean typeof_65(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_66(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]
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

	// {|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]
	private static boolean typeof_67(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_0(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_68(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $11<^AExpr>
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

	// $6<AExpr>
	private static boolean typeof_69(Automaton.State state, Automaton automaton) {
		return typeof_70(state,automaton);
	}

	// $3<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_70(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_72(state,automaton)
			|| typeof_31(state,automaton)
			|| typeof_73(state,automaton)
			|| typeof_74(state,automaton);
	}

	// Num(^real)
	private static boolean typeof_71(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Num) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_63(data,automaton)) { return true; }
		}
		return false;
	}

	// $20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_72(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_61(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Div(^[$11<^AExpr>,$11])>
	private static boolean typeof_73(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_75(data,automaton)) { return true; }
		}
		return false;
	}

	// Var(^string)
	private static boolean typeof_74(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Var) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76(data,automaton)) { return true; }
		}
		return false;
	}

	// ^string
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

	// string
	private static boolean typeof_77(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_STRING;
	}

	// $27<^[$11<^AExpr>,$11]>
	private static boolean typeof_75(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_78(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 75);
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

	// $26<[$11<^AExpr>,$11]>
	private static boolean typeof_78(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_68(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_68(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $4<AExpr>
	private static boolean typeof_32(Automaton.State state, Automaton automaton) {
		return typeof_79(state,automaton);
	}

	// $1<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_79(Automaton.State state, Automaton automaton) {
		return typeof_34(state,automaton)
			|| typeof_80(state,automaton)
			|| typeof_81(state,automaton)
			|| typeof_82(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $22<Mul($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_81(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_83(data,automaton)) { return true; }
		}
		return false;
	}

	// $19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_80(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_83(data,automaton)) { return true; }
		}
		return false;
	}

	// $17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_83(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_84(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 83);
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

	// $16<[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_84(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_85(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $13<^{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_85(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_86(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 85);
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

	// $12<{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_86(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_1(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $29<Div(^[$10<^AExpr>,$10])>
	private static boolean typeof_82(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_87(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$10<^AExpr>,$10]>
	private static boolean typeof_87(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_88(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 87);
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

	// $26<[$10<^AExpr>,$10]>
	private static boolean typeof_88(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_1(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_1(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Mul(^[^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]])
	private static boolean typeof_33(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_89(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]]
	private static boolean typeof_89(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_90(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 89);
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

	// [^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]]
	private static boolean typeof_90(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_91(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]
	private static boolean typeof_91(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_92(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 91);
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

	// {|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]
	private static boolean typeof_92(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_93(child,automaton)) { result=false; break; }
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

	// ^Num(^real)
	private static boolean typeof_93(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_71(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 93);
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

	// $11<^AExpr>
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

	// $9<AExpr>
	private static boolean typeof_95(Automaton.State state, Automaton automaton) {
		return typeof_96(state,automaton);
	}

	// Sum(^[^real,^{||}[]])
	private static boolean typeof_38(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_97(data,automaton)) { return true; }
		}
		return false;
	}

	// Sum(^[^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]])
	private static boolean typeof_39(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_98(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]]
	private static boolean typeof_98(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_99(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 98);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_99(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]]
	private static boolean typeof_99(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_100(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]
	private static boolean typeof_100(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_101(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 100);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_101(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|$10<^AExpr>$10...|}[$10<^AExpr>$10...]
	private static boolean typeof_101(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_102(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_102(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $10<^AExpr>
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

	// $6<AExpr>
	private static boolean typeof_103(Automaton.State state, Automaton automaton) {
		return typeof_104(state,automaton);
	}

	// $6<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_96(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_105(state,automaton)
			|| typeof_106(state,automaton)
			|| typeof_107(state,automaton)
			|| typeof_74(state,automaton);
	}

	// Mul(^[^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_36(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_108(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{||}[]]
	private static boolean typeof_97(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_109(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 97);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_109(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_37(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_110(data,automaton)) { return true; }
		}
		return false;
	}

	// $17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_110(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_111(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 110);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_111(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]])
	private static boolean typeof_42(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_112(data,automaton)) { return true; }
		}
		return false;
	}

	// $16<[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_111(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_113(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Sum(^[^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]])
	private static boolean typeof_43(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_114(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]]
	private static boolean typeof_108(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_115(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 108);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_115(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// NumSumMul
	private static boolean typeof_40(Automaton.State state, Automaton automaton) {
		return typeof_116(state,automaton);
	}

	// [^real,^{||}[]]
	private static boolean typeof_109(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_117(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Sum(^[^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]])
	private static boolean typeof_41(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_118(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_106(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Bool$22<^BExpr>...})
	private static boolean typeof_46(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_120(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Div(^[$11<^AExpr>,$11])>
	private static boolean typeof_107(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_121(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^And(^{$21<^BExpr>...})$21...})
	private static boolean typeof_47(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_122(data,automaton)) { return true; }
		}
		return false;
	}

	// $3<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_104(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_37(state,automaton)
			|| typeof_123(state,automaton)
			|| typeof_124(state,automaton)
			|| typeof_74(state,automaton);
	}

	// And(^{$21<^BExpr>})
	private static boolean typeof_44(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_125(data,automaton)) { return true; }
		}
		return false;
	}

	// $20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_105(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_119(data,automaton)) { return true; }
		}
		return false;
	}

	// $4<BExpr>
	private static boolean typeof_45(Automaton.State state, Automaton automaton) {
		return typeof_126(state,automaton);
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_119(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_127(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 119);
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

	// Or(^{$22<^BExpr>})
	private static boolean typeof_51(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_128(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$22<^BExpr>}
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

	// {$22<^BExpr>}
	private static boolean typeof_129(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_130(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $22<^BExpr>
	private static boolean typeof_130(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_131(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 130);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_131(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<BExpr>
	private static boolean typeof_131(Automaton.State state, Automaton automaton) {
		return typeof_132(state,automaton);
	}

	// $2<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality(^[^Op,$32<^AExpr>])>
	private static boolean typeof_132(Automaton.State state, Automaton automaton) {
		return typeof_48(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_133(state,automaton)
			|| typeof_52(state,automaton)
			|| typeof_134(state,automaton);
	}

	// $27<And($25<^{^BExpr...}>)>
	private static boolean typeof_133(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_135(data,automaton)) { return true; }
		}
		return false;
	}

	// $25<^{^BExpr...}>
	private static boolean typeof_135(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_136(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 135);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_136(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $24<{^BExpr...}>
	private static boolean typeof_136(Automaton.State _state, Automaton automaton) {
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

	// Inequality(^[^Op,$6<^AExpr>])
	private static boolean typeof_134(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_137(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^Op,$5<^AExpr>]
	private static boolean typeof_137(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_138(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 137);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_138(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^Op,$5<^AExpr>]
	private static boolean typeof_138(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_139(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_140(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^Op
	private static boolean typeof_139(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_141(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 139);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_141(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Op
	private static boolean typeof_141(Automaton.State state, Automaton automaton) {
		return typeof_142(state,automaton);
	}

	// $4<^AExpr>
	private static boolean typeof_140(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_143(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 140);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_143(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<AExpr>
	private static boolean typeof_143(Automaton.State state, Automaton automaton) {
		return typeof_144(state,automaton);
	}

	// LE|LT
	private static boolean typeof_142(Automaton.State state, Automaton automaton) {
		return typeof_58(state,automaton)
			|| typeof_55(state,automaton);
	}

	// $6<Var(^string)|Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])>
	private static boolean typeof_144(Automaton.State state, Automaton automaton) {
		return typeof_74(state,automaton)
			|| typeof_34(state,automaton)
			|| typeof_145(state,automaton)
			|| typeof_146(state,automaton)
			|| typeof_147(state,automaton);
	}

	// $23<Sum($21<^[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>)>
	private static boolean typeof_145(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_148(data,automaton)) { return true; }
		}
		return false;
	}

	// $26<Mul($21<^[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>)>
	private static boolean typeof_146(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_148(data,automaton)) { return true; }
		}
		return false;
	}

	// $33<Div(^[$4<^AExpr>,$4])>
	private static boolean typeof_147(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_149(data,automaton)) { return true; }
		}
		return false;
	}

	// $21<^[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>
	private static boolean typeof_148(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_150(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 148);
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

	// $31<^[$4<^AExpr>,$4]>
	private static boolean typeof_149(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_151(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 149);
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

	// $20<[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>
	private static boolean typeof_150(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_152(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $17<^{|$4<^AExpr>...|}[$4<^AExpr>...]>
	private static boolean typeof_152(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_153(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 152);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_153(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<{|$4<^AExpr>...|}[$4<^AExpr>...]>
	private static boolean typeof_153(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_140(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $30<[$4<^AExpr>,$4]>
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
					if(!typeof_140(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_140(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^[^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]]
	private static boolean typeof_118(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_154(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 118);
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

	// [^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]]
	private static boolean typeof_154(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_155(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]
	private static boolean typeof_155(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_156(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 155);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_156(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]
	private static boolean typeof_156(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_93(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_157(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $10<^AExpr>
	private static boolean typeof_157(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_158(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 157);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_158(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<AExpr>
	private static boolean typeof_158(Automaton.State state, Automaton automaton) {
		return typeof_159(state,automaton);
	}

	// $6<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_159(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_160(state,automaton)
			|| typeof_161(state,automaton)
			|| typeof_162(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $29<Div(^[$10<^AExpr>,$10])>
	private static boolean typeof_162(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_163(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$10<^AExpr>,$10]>
	private static boolean typeof_163(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_164(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 163);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_164(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $22<Mul($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_161(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_165(data,automaton)) { return true; }
		}
		return false;
	}

	// $19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_160(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_165(data,automaton)) { return true; }
		}
		return false;
	}

	// $17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_165(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_166(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 165);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_166(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_166(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_167(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $13<^{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_167(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_168(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 167);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_168(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $12<{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_168(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_157(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $26<[$10<^AExpr>,$10]>
	private static boolean typeof_164(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_157(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_157(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $26<And($24<^{^BExpr...}>)>
	private static boolean typeof_50(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_169(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{^BExpr...}>
	private static boolean typeof_169(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_170(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 169);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_170(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{^BExpr...}>
	private static boolean typeof_170(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_171(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $21<^BExpr>
	private static boolean typeof_171(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_172(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 171);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_172(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $5<BExpr>
	private static boolean typeof_172(Automaton.State state, Automaton automaton) {
		return typeof_173(state,automaton);
	}

	// $2<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality(^[^Op,$32<^AExpr>])>
	private static boolean typeof_173(Automaton.State state, Automaton automaton) {
		return typeof_48(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_50(state,automaton)
			|| typeof_174(state,automaton)
			|| typeof_134(state,automaton);
	}

	// $29<Or($24<^{^BExpr...}>)>
	private static boolean typeof_174(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_169(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{||}[]
	private static boolean typeof_117(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_175(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 117);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_175(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {||}[]
	private static boolean typeof_175(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// And(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_49(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_176(data,automaton)) { return true; }
		}
		return false;
	}

	// Num(^real)|$16<Sum($14<^[^real,^{|$7<^AExpr>...|}[$7<^AExpr>...]]>)>|Mul($14)
	private static boolean typeof_116(Automaton.State state, Automaton automaton) {
		return typeof_34(state,automaton)
			|| typeof_177(state,automaton)
			|| typeof_178(state,automaton);
	}

	// Bool
	private static boolean typeof_48(Automaton.State state, Automaton automaton) {
		return typeof_179(state,automaton);
	}

	// [^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]]
	private static boolean typeof_115(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_180(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// LT
	private static boolean typeof_55(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_LT) {
			return true;
		}
		return false;
	}

	// ^[^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]]
	private static boolean typeof_114(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_181(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 114);
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

	// Or(^{^Bool$23<^BExpr>...})
	private static boolean typeof_54(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_182(data,automaton)) { return true; }
		}
		return false;
	}

	// $13<^{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_113(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_183(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 113);
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

	// Or(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_53(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_184(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Or(^{$22<^BExpr>...})$22...}
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

	// {^Or(^{$22<^BExpr>...})$22...}
	private static boolean typeof_185(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_20(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_130(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]]
	private static boolean typeof_112(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_186(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 112);
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

	// [^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]]
	private static boolean typeof_186(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_187(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]
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

	// {|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]
	private static boolean typeof_188(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_189(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_189(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_190(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $11<^AExpr>
	private static boolean typeof_190(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_191(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 190);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_191(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $7<AExpr>
	private static boolean typeof_191(Automaton.State state, Automaton automaton) {
		return typeof_192(state,automaton);
	}

	// $4<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_192(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_193(state,automaton)
			|| typeof_194(state,automaton)
			|| typeof_195(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_193(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_196(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_196(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_197(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 196);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_197(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $17<[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_197(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_198(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $14<^{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_198(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_199(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 198);
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

	// $13<{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_199(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_190(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $29<Div(^[$11<^AExpr>,$11])>
	private static boolean typeof_195(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_200(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$11<^AExpr>,$11]>
	private static boolean typeof_200(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_201(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 200);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_201(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $26<[$11<^AExpr>,$11]>
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
					if(!typeof_190(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_190(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_194(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_196(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_189(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_194(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 189);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_194(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_52(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_135(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<Mul($12<^[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>)>
	private static boolean typeof_178(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_202(data,automaton)) { return true; }
		}
		return false;
	}

	// $12<^[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>
	private static boolean typeof_202(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_203(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 202);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_203(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $11<[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>
	private static boolean typeof_203(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_204(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $8<^{|$5<^AExpr>...|}[$5<^AExpr>...]>
	private static boolean typeof_204(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_205(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 204);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_205(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $7<{|$5<^AExpr>...|}[$5<^AExpr>...]>
	private static boolean typeof_205(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_206(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<^AExpr>
	private static boolean typeof_206(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_207(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 206);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_207(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $19<AExpr>
	private static boolean typeof_207(Automaton.State state, Automaton automaton) {
		return typeof_208(state,automaton);
	}

	// $16<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_208(Automaton.State state, Automaton automaton) {
		return typeof_34(state,automaton)
			|| typeof_177(state,automaton)
			|| typeof_178(state,automaton)
			|| typeof_209(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $29<Div(^[$5<^AExpr>,$5])>
	private static boolean typeof_209(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_210(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$5<^AExpr>,$5]>
	private static boolean typeof_210(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_211(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 210);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_211(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $26<[$5<^AExpr>,$5]>
	private static boolean typeof_211(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_206(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_206(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $17<[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_127(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_212(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $14<^{|$11<^AExpr>...|}[$11<^AExpr>...]>
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

	// $13<{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_213(Automaton.State _state, Automaton automaton) {
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

	// Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_59(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_214(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
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

	// [^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
	private static boolean typeof_215(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_216(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|$11<^AExpr>|}[$11<^AExpr>]
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

	// {|$11<^AExpr>|}[$11<^AExpr>]
	private static boolean typeof_217(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_68(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// True|False
	private static boolean typeof_179(Automaton.State state, Automaton automaton) {
		return typeof_218(state,automaton)
			|| typeof_219(state,automaton);
	}

	// True
	private static boolean typeof_218(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_True) {
			return true;
		}
		return false;
	}

	// False
	private static boolean typeof_219(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_False) {
			return true;
		}
		return false;
	}

	// $1<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality(^[^Op,$32<^AExpr>])>
	private static boolean typeof_126(Automaton.State state, Automaton automaton) {
		return typeof_48(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_220(state,automaton)
			|| typeof_221(state,automaton)
			|| typeof_134(state,automaton);
	}

	// $26<And($24<^{^BExpr...}>)>
	private static boolean typeof_220(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_222(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Or($24<^{^BExpr...}>)>
	private static boolean typeof_221(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_222(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{^BExpr...}>
	private static boolean typeof_222(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_223(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 222);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_223(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{^BExpr...}>
	private static boolean typeof_223(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_14(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// LE
	private static boolean typeof_58(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_LE) {
			return true;
		}
		return false;
	}

	// ^{^Or(^{$22<^BExpr>...})$22...}
	private static boolean typeof_176(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_224(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 176);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_224(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Or(^{$22<^BExpr>...})$22...}
	private static boolean typeof_224(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_225(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_226(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $22<^BExpr>
	private static boolean typeof_226(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_227(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 226);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_227(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $6<BExpr>
	private static boolean typeof_227(Automaton.State state, Automaton automaton) {
		return typeof_228(state,automaton);
	}

	// $3<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality(^[^Op,$32<^AExpr>])>
	private static boolean typeof_228(Automaton.State state, Automaton automaton) {
		return typeof_48(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_229(state,automaton)
			|| typeof_230(state,automaton)
			|| typeof_134(state,automaton);
	}

	// $29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_230(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_231(data,automaton)) { return true; }
		}
		return false;
	}

	// $25<^{^BExpr...}>
	private static boolean typeof_231(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_232(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 231);
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

	// $24<{^BExpr...}>
	private static boolean typeof_232(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_226(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $27<And($25<^{^BExpr...}>)>
	private static boolean typeof_229(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_231(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_225(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_230(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 225);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_230(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// ^{$21<^BExpr>}
	private static boolean typeof_125(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_233(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 125);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_233(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {$21<^BExpr>}
	private static boolean typeof_233(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_171(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// And(^{^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...]])]),^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...]])])$94<^BExpr>...})
	private static boolean typeof_57(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_234(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...]])]),^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...]])])$94<^BExpr>...}
	private static boolean typeof_234(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_235(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 234);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_235(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25<^AExpr>|}[$25<^AExpr>]])^Mul(^[^real,^{|$25...|}[$25...]])...]])]),^Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...|}[^Mul(^[^real,^{|$25|}[$25]])^Mul(^[^real,^{|$25...|}[$25...]])...]])])$94<^BExpr>...}
	private static boolean typeof_235(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_28(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_28(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_236(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $64<^BExpr>
	private static boolean typeof_236(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_237(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 236);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_237(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $52<BExpr>
	private static boolean typeof_237(Automaton.State state, Automaton automaton) {
		return typeof_238(state,automaton);
	}

	// $49<Var(^string)|Bool|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality(^[^Op,$25<^AExpr>])>
	private static boolean typeof_238(Automaton.State state, Automaton automaton) {
		return typeof_74(state,automaton)
			|| typeof_48(state,automaton)
			|| typeof_239(state,automaton)
			|| typeof_240(state,automaton)
			|| typeof_241(state,automaton);
	}

	// $69<And($67<^{^BExpr...}>)>
	private static boolean typeof_239(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_242(data,automaton)) { return true; }
		}
		return false;
	}

	// $14<Sum($12<^[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>)>
	private static boolean typeof_177(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_202(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Div(^[$10<^AExpr>,$10])>
	private static boolean typeof_124(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_243(data,automaton)) { return true; }
		}
		return false;
	}

	// Inequality(^[^Op,^Num(^real)])
	private static boolean typeof_56(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_244(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^Op,^Num(^real)]
	private static boolean typeof_244(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_245(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 244);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_245(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^Op,^Num(^real)]
	private static boolean typeof_245(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_246(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_3(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^Op
	private static boolean typeof_246(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_141(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 246);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_141(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $67<^{^BExpr...}>
	private static boolean typeof_242(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_247(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 242);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_247(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $66<{^BExpr...}>
	private static boolean typeof_247(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_236(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Bool$23<^BExpr>...}
	private static boolean typeof_182(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_248(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 182);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_248(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Bool$23<^BExpr>...}
	private static boolean typeof_248(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_16(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_249(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $22<^BExpr>
	private static boolean typeof_249(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_250(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 249);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_250(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<BExpr>
	private static boolean typeof_250(Automaton.State state, Automaton automaton) {
		return typeof_251(state,automaton);
	}

	// $13<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality(^[^Op,$32<^AExpr>])>
	private static boolean typeof_251(Automaton.State state, Automaton automaton) {
		return typeof_48(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_252(state,automaton)
			|| typeof_253(state,automaton)
			|| typeof_134(state,automaton);
	}

	// $27<And($25<^{^BExpr...}>)>
	private static boolean typeof_252(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_254(data,automaton)) { return true; }
		}
		return false;
	}

	// $25<^{^BExpr...}>
	private static boolean typeof_254(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_255(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 254);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_255(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $24<{^BExpr...}>
	private static boolean typeof_255(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_249(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_253(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_254(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<Mul($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_123(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_110(data,automaton)) { return true; }
		}
		return false;
	}

	// ^real
	private static boolean typeof_63(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_256(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 63);
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

	// real
	private static boolean typeof_256(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_REAL;
	}

	// $27<^[$10<^AExpr>,$10]>
	private static boolean typeof_243(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_257(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 243);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_257(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $26<[$10<^AExpr>,$10]>
	private static boolean typeof_257(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_102(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_102(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $12<{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_183(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_102(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^And(^{$21<^BExpr>...})$21...}
	private static boolean typeof_122(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_258(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 122);
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

	// {^And(^{$21<^BExpr>...})$21...}
	private static boolean typeof_258(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_18(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_171(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	private static boolean typeof_62(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_259(data,automaton)) { return true; }
		}
		return false;
	}

	// $9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>
	private static boolean typeof_259(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_260(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 259);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_260(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $8<[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>
	private static boolean typeof_260(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_261(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<^{|$2<^AExpr>...|}[$2<^AExpr>...]>
	private static boolean typeof_261(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_262(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 261);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_262(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $4<{|$2<^AExpr>...|}[$2<^AExpr>...]>
	private static boolean typeof_262(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_263(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $2<^AExpr>
	private static boolean typeof_263(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_264(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 263);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_264(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<AExpr>
	private static boolean typeof_264(Automaton.State state, Automaton automaton) {
		return typeof_265(state,automaton);
	}

	// $13<Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_265(Automaton.State state, Automaton automaton) {
		return typeof_62(state,automaton)
			|| typeof_71(state,automaton)
			|| typeof_266(state,automaton)
			|| typeof_267(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $22<Sum($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	private static boolean typeof_266(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_259(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Div(^[$2<^AExpr>,$2])>
	private static boolean typeof_267(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_268(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$2<^AExpr>,$2]>
	private static boolean typeof_268(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_269(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 268);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_269(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $26<[$2<^AExpr>,$2]>
	private static boolean typeof_269(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_263(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_263(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $72<Or($67<^{^BExpr...}>)>
	private static boolean typeof_240(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_242(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]
	private static boolean typeof_180(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_270(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 180);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_270(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]
	private static boolean typeof_270(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_271(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_272(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $11<^AExpr>
	private static boolean typeof_272(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_273(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 272);
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

	// $7<AExpr>
	private static boolean typeof_273(Automaton.State state, Automaton automaton) {
		return typeof_274(state,automaton);
	}

	// $4<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_274(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_275(state,automaton)
			|| typeof_276(state,automaton)
			|| typeof_277(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_275(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_278(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_278(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_279(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 278);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_279(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $17<[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_279(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_280(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $29<Div(^[$11<^AExpr>,$11])>
	private static boolean typeof_277(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_281(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_276(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_278(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$11<^AExpr>,$11]>
	private static boolean typeof_281(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_282(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 281);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_282(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $26<[$11<^AExpr>,$11]>
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
					if(!typeof_272(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_272(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $14<^{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_280(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_283(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 280);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_283(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $13<{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_283(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_272(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^$20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_271(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_275(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 271);
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

	// $27<^[$11<^AExpr>,$11]>
	private static boolean typeof_121(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_284(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 121);
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

	// $26<[$11<^AExpr>,$11]>
	private static boolean typeof_284(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_94(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_94(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_61(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_285(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 61);
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

	// $17<[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_285(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_286(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $14<^{|$11<^AExpr>...|}[$11<^AExpr>...]>
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

	// $13<{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_287(Automaton.State _state, Automaton automaton) {
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

	// Inequality(^[^Op,$24<^AExpr>])
	private static boolean typeof_241(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_288(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^Op,$23<^AExpr>]
	private static boolean typeof_288(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_289(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 288);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_289(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^Op,$23<^AExpr>]
	private static boolean typeof_289(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_246(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_190(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// [^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]]
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
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_290(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]
	private static boolean typeof_290(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_291(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 290);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_291(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]
	private static boolean typeof_291(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_6(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_102(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^{^Bool$22<^BExpr>...}
	private static boolean typeof_120(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_292(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 120);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_292(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Bool$22<^BExpr>...}
	private static boolean typeof_292(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_16(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_293(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $21<^BExpr>
	private static boolean typeof_293(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_294(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 293);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_294(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<BExpr>
	private static boolean typeof_294(Automaton.State state, Automaton automaton) {
		return typeof_295(state,automaton);
	}

	// $13<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality(^[^Op,$32<^AExpr>])>
	private static boolean typeof_295(Automaton.State state, Automaton automaton) {
		return typeof_48(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_296(state,automaton)
			|| typeof_297(state,automaton)
			|| typeof_134(state,automaton);
	}

	// $26<And($24<^{^BExpr...}>)>
	private static boolean typeof_296(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_298(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Or($24<^{^BExpr...}>)>
	private static boolean typeof_297(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_298(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{^BExpr...}>
	private static boolean typeof_298(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_299(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 298);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_299(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{^BExpr...}>
	private static boolean typeof_299(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_293(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Inequality(^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$24<^AExpr>|}[$24<^AExpr>]])^$35<Mul($31<^[^real,^{|$24...|}[$24...]]>)>...|}[^Mul(^[^real,^{|$24<^AExpr>|}[$24<^AExpr>]])^$35<Mul($31<^[^real,^{|$24...|}[$24...]]>)>...]])])
	private static boolean typeof_60(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_300(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$23<^AExpr>|}[$23<^AExpr>]])^$34<Mul($30<^[^real,^{|$23...|}[$23...]]>)>...|}[^Mul(^[^real,^{|$23<^AExpr>|}[$23<^AExpr>]])^$34<Mul($30<^[^real,^{|$23...|}[$23...]]>)>...]])]
	private static boolean typeof_300(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_301(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 300);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_301(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^Op,^Sum(^[^real,^{|^Mul(^[^real,^{|$23<^AExpr>|}[$23<^AExpr>]])^$34<Mul($30<^[^real,^{|$23...|}[$23...]]>)>...|}[^Mul(^[^real,^{|$23<^AExpr>|}[$23<^AExpr>]])^$34<Mul($30<^[^real,^{|$23...|}[$23...]]>)>...]])]
	private static boolean typeof_301(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_246(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_302(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]])
	private static boolean typeof_302(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_303(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 302);
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

	// Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]])
	private static boolean typeof_303(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_304(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]]
	private static boolean typeof_304(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_305(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 304);
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

	// [^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]]
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
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_306(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]
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

	// {|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]
	private static boolean typeof_307(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_308(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_189(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_308(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_309(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 308);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_309(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_309(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_310(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
	private static boolean typeof_310(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_311(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 310);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_311(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
	private static boolean typeof_311(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_312(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|$11<^AExpr>|}[$11<^AExpr>]
	private static boolean typeof_312(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_313(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 312);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_313(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|$11<^AExpr>|}[$11<^AExpr>]
	private static boolean typeof_313(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_190(child,automaton)) { result=false; break; }
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

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// Var(^string)
		Schema.Term("Var",Schema.String),
		// $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
		Schema.Term("Mul",Schema.List(true,Schema.Real,Schema.Bag(true))),
		// $7<Div(^[$1<^AExpr>,$1])>
		Schema.Term("Div",Schema.List(true,Schema.Or(Schema.Any, Schema.Term("Num",Schema.Real), Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Var",Schema.String)),Schema.Any)),
		// $11<Sum($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
		Schema.Term("Sum",Schema.List(true,Schema.Real,Schema.Bag(true))),
		// True
		Schema.Term("True"),
		// False
		Schema.Term("False"),
		// $6<And($4<^{^BExpr...}>)>
		Schema.Term("And",Schema.Set(true)),
		// $6<Or($4<^{^BExpr...}>)>
		Schema.Term("Or",Schema.Set(true)),
		// LE
		Schema.Term("LE"),
		// LT
		Schema.Term("LT"),
		// Inequality(^[^Op,$2<^AExpr>])
		Schema.Term("Inequality",Schema.List(true,Schema.Or(Schema.Term("LE"), Schema.Term("LT")),Schema.Or(Schema.Term("Num",Schema.Real), Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any)), Schema.Term("Var",Schema.String)))),
		// Num(^real)
		Schema.Term("Num",Schema.Real)
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
