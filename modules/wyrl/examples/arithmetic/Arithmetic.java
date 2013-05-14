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

	// term Inequality($1<^AExpr>)
	public final static int K_Inequality = 8;
	public final static int Inequality(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Inequality, r0));
	}

	// Inequality(Num(real v))
	public static boolean reduce_24(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
	
		Automaton.Real r5 = new Automaton.Real(0); // 0.0
		Automaton.Real r6 = (Automaton.Real) automaton.get(r4);
		boolean r7 = r6.compareTo(r5)<0; // v lt 0.0
		if(r7) {
			Automaton.Term r8 = False;
			int r9 = automaton.add(r8);
			if(r0 != r9) {
				automaton.rewrite(r0, r9);
				numReductions++;
				return true;
			}
		}
		Automaton.Term r10 = True;
		int r11 = automaton.add(r10);
		if(r0 != r11) {
			automaton.rewrite(r0, r11);
			numReductions++;
			return true;
		}
		return false;
	}

	// And({Inequality(Sum([real x1, {|Mul([real x2, {|$4<AExpr> v1|}]), $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)> xs...|}]) s1) eq1, Inequality(Sum([real y1, {|Mul([real y2, {|$4<AExpr> v2|}]), $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)> ys...|}]) s2) eq2, $4<BExpr> rest...})
	public static boolean infer_25(int r0, Automaton automaton) {
	Automaton original = new Automaton(automaton);
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_26(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Term r7 = (Automaton.Term) automaton.get(r6);
			int r8 = r7.contents;
			Automaton.List r9 = (Automaton.List) automaton.get(r8);
			int r10 = r9.get(0);
			int r11 = r9.get(1);
			Automaton.Bag r12 = (Automaton.Bag) automaton.get(r11);
			for(int i13=0;i13!=r12.size();++i13) {
				int r13 = r12.get(i13);
				if(!typeof_27(r13,automaton)) { continue; }
				
				Automaton.Term r14 = (Automaton.Term) automaton.get(r13);
				int r15 = r14.contents;
				Automaton.List r16 = (Automaton.List) automaton.get(r15);
				int r17 = r16.get(0);
				int r18 = r16.get(1);
				Automaton.Bag r19 = (Automaton.Bag) automaton.get(r18);
				for(int i20=0;i20!=r19.size();++i20) {
					int r20 = r19.get(i20);
					if(!typeof_1(r20,automaton)) { continue; }
					
					int j21 = 0;
					int[] t21 = new int[r12.size()-1];
					for(int i21=0;i21!=r12.size();++i21) {
						int r21 = r12.get(i21);
						if(i21 == i13 || !typeof_28(r21,automaton)) { continue; }
						
						t21[j21++] = r21;
					}
					Automaton.Bag r22 = new Automaton.Bag(t21);
					for(int i23=0;i23!=r3.size();++i23) {
						int r23 = r3.get(i23);
						if(i23 == i4 || !typeof_26(r23,automaton)) { continue; }
						
						Automaton.Term r24 = (Automaton.Term) automaton.get(r23);
						int r25 = r24.contents;
						Automaton.Term r26 = (Automaton.Term) automaton.get(r25);
						int r27 = r26.contents;
						Automaton.List r28 = (Automaton.List) automaton.get(r27);
						int r29 = r28.get(0);
						int r30 = r28.get(1);
						Automaton.Bag r31 = (Automaton.Bag) automaton.get(r30);
						for(int i32=0;i32!=r31.size();++i32) {
							int r32 = r31.get(i32);
							if(!typeof_27(r32,automaton)) { continue; }
							
							Automaton.Term r33 = (Automaton.Term) automaton.get(r32);
							int r34 = r33.contents;
							Automaton.List r35 = (Automaton.List) automaton.get(r34);
							int r36 = r35.get(0);
							int r37 = r35.get(1);
							Automaton.Bag r38 = (Automaton.Bag) automaton.get(r37);
							for(int i39=0;i39!=r38.size();++i39) {
								int r39 = r38.get(i39);
								if(!typeof_1(r39,automaton)) { continue; }
								
								int j40 = 0;
								int[] t40 = new int[r31.size()-1];
								for(int i40=0;i40!=r31.size();++i40) {
									int r40 = r31.get(i40);
									if(i40 == i32 || !typeof_28(r40,automaton)) { continue; }
									
									t40[j40++] = r40;
								}
								Automaton.Bag r41 = new Automaton.Bag(t40);
								int j42 = 0;
								int[] t42 = new int[r3.size()-2];
								for(int i42=0;i42!=r3.size();++i42) {
									int r42 = r3.get(i42);
									if(i42 == i4 || i42 == i23 || !typeof_14(r42,automaton)) { continue; }
									
									t42[j42++] = r42;
								}
								Automaton.Set r43 = new Automaton.Set(t42);
	
		int r44 = automaton.add(r22);
		Automaton.List r45 = new Automaton.List(r10, r44); // [x1xs]
		int r46 = automaton.add(r45);
		Automaton.Term r47 = new Automaton.Term(K_Sum,r46);
		int r48 = automaton.add(r47);
		Automaton.Bag r49 = new Automaton.Bag(r48); // {|Sum([x1xs])|}
		int r50 = automaton.add(r49);
		Automaton.List r51 = new Automaton.List(r36, r50); // [y2{|Sum([x1xs])|}]
		int r52 = automaton.add(r51);
		Automaton.Term r53 = new Automaton.Term(K_Mul,r52);
		Automaton.Real r54 = (Automaton.Real) automaton.get(r17);
		Automaton.Real r55 = r54.negate(); // -x2
		int r56 = automaton.add(r55);
		int r57 = automaton.add(r41);
		Automaton.List r58 = new Automaton.List(r29, r57); // [y1ys]
		int r59 = automaton.add(r58);
		Automaton.Term r60 = new Automaton.Term(K_Sum,r59);
		int r61 = automaton.add(r60);
		Automaton.Bag r62 = new Automaton.Bag(r61); // {|Sum([y1ys])|}
		int r63 = automaton.add(r62);
		Automaton.List r64 = new Automaton.List(r56, r63); // [-x2{|Sum([y1ys])|}]
		int r65 = automaton.add(r64);
		Automaton.Term r66 = new Automaton.Term(K_Mul,r65);
		Automaton.Real r67 = new Automaton.Real(0); // 0.0
		int r68 = automaton.add(r67);
		int r69 = automaton.add(r53);
		int r70 = automaton.add(r66);
		Automaton.Bag r71 = new Automaton.Bag(r69, r70); // {|s3s4|}
		int r72 = automaton.add(r71);
		Automaton.List r73 = new Automaton.List(r68, r72); // [0.0{|s3s4|}]
		int r74 = automaton.add(r73);
		Automaton.Term r75 = new Automaton.Term(K_Sum,r74);
		int r76 = automaton.add(r75);
		Automaton.Term r77 = new Automaton.Term(K_Inequality,r76);
								boolean r78 = r20 == r39;      // v1 eq v2
								boolean r79 = false;           // v1 eq v2 && x2 lt 0.0 && y2 gt 0.0
								if(r78) {
									Automaton.Real r80 = new Automaton.Real(0); // 0.0
									Automaton.Real r81 = (Automaton.Real) automaton.get(r17);
									boolean r82 = r81.compareTo(r80)<0; // x2 lt 0.0
									boolean r83 = false;           // x2 lt 0.0 && y2 gt 0.0
									if(r82) {
										Automaton.Real r84 = new Automaton.Real(0); // 0.0
										Automaton.Real r85 = (Automaton.Real) automaton.get(r36);
										boolean r86 = r85.compareTo(r84)>0; // y2 gt 0.0
										r83 = r86;
									}
									r79 = r83;
								}
								if(r79) {
									int r87 = automaton.add(r77);
									Automaton.Set r88 = new Automaton.Set(r4, r23, r87); // {eq1eq2eq3}
									Automaton.Set r89 = r88.append(r43); // {eq1eq2eq3} append rest
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
	public final static int K_Num = 9;
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
		automaton.canonicalise(automaton.getRoot(0));
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
				
				if(typeof_25(i,automaton) &&
					infer_25(i,automaton)) {
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
			 return typeof_29(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 0);
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

	// $10<^AExpr>
	private static boolean typeof_1(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_30(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 1);
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

	// ^Mul(^[^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]])
	private static boolean typeof_2(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_31(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 2);
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

	// ^Num(^real)
	private static boolean typeof_3(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_32(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 3);
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

	// ^Mul(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_4(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_33(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 4);
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

	// ^Mul(^[^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_5(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_34(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 5);
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

	// ^$19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_6(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_35(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 6);
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

	// ^Sum(^[^real,^{||}[]])
	private static boolean typeof_7(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_36(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 7);
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

	// ^Sum(^[^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]])
	private static boolean typeof_8(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_37(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 8);
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

	// ^NumSumMul
	private static boolean typeof_9(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_38(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 9);
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

	// ^Sum(^[^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]])
	private static boolean typeof_10(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_39(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 10);
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

	// ^Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]])
	private static boolean typeof_11(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_40(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 11);
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

	// ^Sum(^[^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]])
	private static boolean typeof_12(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_41(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 12);
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

	// ^And(^{$21<^BExpr>})
	private static boolean typeof_13(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_42(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 13);
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

	// $21<^BExpr>
	private static boolean typeof_14(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_43(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 14);
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

	// ^And(^{^Bool$22<^BExpr>...})
	private static boolean typeof_15(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_44(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 15);
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

	// ^And(^{^And(^{$21<^BExpr>...})$21...})
	private static boolean typeof_17(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_45(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 17);
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

	// ^Bool
	private static boolean typeof_16(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_46(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 16);
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

	// ^And(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_19(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_47(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 19);
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

	// ^$26<And($24<^{^BExpr...}>)>
	private static boolean typeof_18(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_48(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 18);
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

	// ^Or(^{$22<^BExpr>})
	private static boolean typeof_21(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_49(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 21);
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

	// ^$29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_20(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_50(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 20);
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

	// ^Or(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_23(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_51(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 23);
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

	// ^Or(^{^Bool$23<^BExpr>...})
	private static boolean typeof_22(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_52(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 22);
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

	// ^And(^{^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...]])),^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...]]))$78<^BExpr>...})
	private static boolean typeof_25(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_53(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 25);
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

	// ^Inequality(^Num(^real))
	private static boolean typeof_24(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_54(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 24);
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

	// ^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_27(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_55(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 27);
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

	// ^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$12<^AExpr>|}[$12<^AExpr>]])^$23<Mul($19<^[^real,^{|$12...|}[$12...]]>)>...|}[^Mul(^[^real,^{|$12<^AExpr>|}[$12<^AExpr>]])^$23<Mul($19<^[^real,^{|$12...|}[$12...]]>)>...]]))
	private static boolean typeof_26(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_56(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 26);
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

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_29(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_57(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	private static boolean typeof_28(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_58(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 28);
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

	// Mul(^[^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]])
	private static boolean typeof_31(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_59(data,automaton)) { return true; }
		}
		return false;
	}

	// $4<AExpr>
	private static boolean typeof_30(Automaton.State state, Automaton automaton) {
		return typeof_60(state,automaton);
	}

	// Mul(^[^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_34(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_61(data,automaton)) { return true; }
		}
		return false;
	}

	// $19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_35(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_62(data,automaton)) { return true; }
		}
		return false;
	}

	// Num(^real)
	private static boolean typeof_32(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Num) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_63(data,automaton)) { return true; }
		}
		return false;
	}

	// Mul(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]])
	private static boolean typeof_33(Automaton.State state, Automaton automaton) {
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
			|| typeof_29(state,automaton)
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
			if(typeof_57(data,automaton)) { return true; }
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

	// NumSumMul
	private static boolean typeof_38(Automaton.State state, Automaton automaton) {
		return typeof_79(state,automaton);
	}

	// Num(^real)|$16<Sum($14<^[^real,^{|$7<^AExpr>...|}[$7<^AExpr>...]]>)>|Mul($14)
	private static boolean typeof_79(Automaton.State state, Automaton automaton) {
		return typeof_32(state,automaton)
			|| typeof_80(state,automaton)
			|| typeof_81(state,automaton);
	}

	// $22<Mul($12<^[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>)>
	private static boolean typeof_81(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_82(data,automaton)) { return true; }
		}
		return false;
	}

	// $14<Sum($12<^[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>)>
	private static boolean typeof_80(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_82(data,automaton)) { return true; }
		}
		return false;
	}

	// $12<^[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>
	private static boolean typeof_82(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_83(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 82);
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

	// $11<[^real,^{|$5<^AExpr>...|}[$5<^AExpr>...]]>
	private static boolean typeof_83(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_84(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $8<^{|$5<^AExpr>...|}[$5<^AExpr>...]>
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

	// $7<{|$5<^AExpr>...|}[$5<^AExpr>...]>
	private static boolean typeof_85(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_86(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $5<^AExpr>
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

	// $19<AExpr>
	private static boolean typeof_87(Automaton.State state, Automaton automaton) {
		return typeof_88(state,automaton);
	}

	// $16<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_88(Automaton.State state, Automaton automaton) {
		return typeof_32(state,automaton)
			|| typeof_80(state,automaton)
			|| typeof_81(state,automaton)
			|| typeof_89(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $29<Div(^[$5<^AExpr>,$5])>
	private static boolean typeof_89(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_90(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$5<^AExpr>,$5]>
	private static boolean typeof_90(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_91(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 90);
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

	// $26<[$5<^AExpr>,$5]>
	private static boolean typeof_91(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_86(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_86(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Sum(^[^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]])
	private static boolean typeof_39(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_92(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]]
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

	// [^real,^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]]
	private static boolean typeof_93(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_94(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]
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

	// {|^Num(^real)$11<^AExpr>...|}[^Num(^real)$11<^AExpr>...]
	private static boolean typeof_95(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_96(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_97(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^Num(^real)
	private static boolean typeof_96(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_71(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 96);
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

	// Sum(^[^real,^{||}[]])
	private static boolean typeof_36(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_98(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{||}[]]
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

	// [^real,^{||}[]]
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

	// ^{||}[]
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

	// {||}[]
	private static boolean typeof_101(Automaton.State _state, Automaton automaton) {
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

	// $10<^AExpr>
	private static boolean typeof_97(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_102(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 97);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_102(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<AExpr>
	private static boolean typeof_102(Automaton.State state, Automaton automaton) {
		return typeof_103(state,automaton);
	}

	// $6<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_103(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_104(state,automaton)
			|| typeof_105(state,automaton)
			|| typeof_106(state,automaton)
			|| typeof_74(state,automaton);
	}

	// Sum(^[^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]])
	private static boolean typeof_37(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_107(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{$21<^BExpr>})
	private static boolean typeof_42(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_108(data,automaton)) { return true; }
		}
		return false;
	}

	// $4<BExpr>
	private static boolean typeof_43(Automaton.State state, Automaton automaton) {
		return typeof_109(state,automaton);
	}

	// ^{$21<^BExpr>}
	private static boolean typeof_108(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_110(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 108);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_110(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {$21<^BExpr>}
	private static boolean typeof_110(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_111(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $21<^BExpr>
	private static boolean typeof_111(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_112(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 111);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_112(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]])
	private static boolean typeof_40(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_113(data,automaton)) { return true; }
		}
		return false;
	}

	// $1<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality($31<^AExpr>)>
	private static boolean typeof_109(Automaton.State state, Automaton automaton) {
		return typeof_46(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_114(state,automaton)
			|| typeof_115(state,automaton)
			|| typeof_116(state,automaton);
	}

	// Sum(^[^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]])
	private static boolean typeof_41(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_117(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Div(^[$10<^AExpr>,$10])>
	private static boolean typeof_106(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_118(data,automaton)) { return true; }
		}
		return false;
	}

	// Bool
	private static boolean typeof_46(Automaton.State state, Automaton automaton) {
		return typeof_119(state,automaton);
	}

	// ^[^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]]
	private static boolean typeof_107(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_120(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 107);
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

	// And(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_47(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_121(data,automaton)) { return true; }
		}
		return false;
	}

	// $19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_104(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_122(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^Bool$22<^BExpr>...})
	private static boolean typeof_44(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_123(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<Mul($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_105(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_122(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^And(^{$21<^BExpr>...})$21...})
	private static boolean typeof_45(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_124(data,automaton)) { return true; }
		}
		return false;
	}

	// True|False
	private static boolean typeof_119(Automaton.State state, Automaton automaton) {
		return typeof_125(state,automaton)
			|| typeof_126(state,automaton);
	}

	// Or(^{^Or(^{$22<^BExpr>...})$22...})
	private static boolean typeof_51(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_127(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$10<^AExpr>,$10]>
	private static boolean typeof_118(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_128(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 118);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_128(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $26<[$10<^AExpr>,$10]>
	private static boolean typeof_128(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_97(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_97(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_50(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_129(data,automaton)) { return true; }
		}
		return false;
	}

	// $25<^{^BExpr...}>
	private static boolean typeof_129(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_130(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 129);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_130(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $24<{^BExpr...}>
	private static boolean typeof_130(Automaton.State _state, Automaton automaton) {
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

	// $22<^BExpr>
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

	// $5<BExpr>
	private static boolean typeof_132(Automaton.State state, Automaton automaton) {
		return typeof_133(state,automaton);
	}

	// $2<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality($31<^AExpr>)>
	private static boolean typeof_133(Automaton.State state, Automaton automaton) {
		return typeof_46(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_134(state,automaton)
			|| typeof_50(state,automaton)
			|| typeof_116(state,automaton);
	}

	// $27<And($25<^{^BExpr...}>)>
	private static boolean typeof_134(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_129(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]]
	private static boolean typeof_117(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_135(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 117);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_135(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^real,^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]]
	private static boolean typeof_135(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_136(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]
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

	// {|^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...|}[^Sum(^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]])$10...]
	private static boolean typeof_137(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_138(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $10<^AExpr>
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

	// $6<AExpr>
	private static boolean typeof_139(Automaton.State state, Automaton automaton) {
		return typeof_140(state,automaton);
	}

	// $3<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_140(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_35(state,automaton)
			|| typeof_141(state,automaton)
			|| typeof_142(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $22<Mul($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_141(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_62(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Div(^[$10<^AExpr>,$10])>
	private static boolean typeof_142(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_143(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$10<^AExpr>,$10]>
	private static boolean typeof_143(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_144(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 143);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_144(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $26<[$10<^AExpr>,$10]>
	private static boolean typeof_144(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_138(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_138(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Or(^{$22<^BExpr>})
	private static boolean typeof_49(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_145(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$22<^BExpr>}
	private static boolean typeof_145(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_146(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 145);
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

	// {$22<^BExpr>}
	private static boolean typeof_146(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_131(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// Inequality($5<^AExpr>)
	private static boolean typeof_116(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_147(data,automaton)) { return true; }
		}
		return false;
	}

	// $4<^AExpr>
	private static boolean typeof_147(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_148(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 147);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_148(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<AExpr>
	private static boolean typeof_148(Automaton.State state, Automaton automaton) {
		return typeof_149(state,automaton);
	}

	// $6<Var(^string)|Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])>
	private static boolean typeof_149(Automaton.State state, Automaton automaton) {
		return typeof_74(state,automaton)
			|| typeof_32(state,automaton)
			|| typeof_150(state,automaton)
			|| typeof_151(state,automaton)
			|| typeof_152(state,automaton);
	}

	// $33<Div(^[$4<^AExpr>,$4])>
	private static boolean typeof_152(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_153(data,automaton)) { return true; }
		}
		return false;
	}

	// $31<^[$4<^AExpr>,$4]>
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

	// $30<[$4<^AExpr>,$4]>
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
					if(!typeof_147(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_147(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $23<Sum($21<^[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>)>
	private static boolean typeof_150(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_155(data,automaton)) { return true; }
		}
		return false;
	}

	// $21<^[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>
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

	// $20<[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>
	private static boolean typeof_156(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_157(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $17<^{|$4<^AExpr>...|}[$4<^AExpr>...]>
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

	// $16<{|$4<^AExpr>...|}[$4<^AExpr>...]>
	private static boolean typeof_158(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_147(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $26<Mul($21<^[^real,^{|$4<^AExpr>...|}[$4<^AExpr>...]]>)>
	private static boolean typeof_151(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_155(data,automaton)) { return true; }
		}
		return false;
	}

	// $26<And($24<^{^BExpr...}>)>
	private static boolean typeof_48(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_159(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{^BExpr...}>
	private static boolean typeof_159(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_160(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 159);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_160(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $23<{^BExpr...}>
	private static boolean typeof_160(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_111(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $29<Or($24<^{^BExpr...}>)>
	private static boolean typeof_115(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_161(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{^BExpr...}>
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

	// $23<{^BExpr...}>
	private static boolean typeof_162(Automaton.State _state, Automaton automaton) {
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

	// Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_55(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_163(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
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

	// [^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
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
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_165(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|$11<^AExpr>|}[$11<^AExpr>]
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

	// {|$11<^AExpr>|}[$11<^AExpr>]
	private static boolean typeof_166(Automaton.State _state, Automaton automaton) {
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

	// $26<And($24<^{^BExpr...}>)>
	private static boolean typeof_114(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_161(data,automaton)) { return true; }
		}
		return false;
	}

	// Inequality(^Num(^real))
	private static boolean typeof_54(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]]
	private static boolean typeof_113(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_167(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 113);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_167(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^real,^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]]
	private static boolean typeof_167(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_168(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]
	private static boolean typeof_168(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_169(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 168);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_169(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...|}[^Mul(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]),^Mul(^[^real,^{|$11...|}[$11...]])$11...]
	private static boolean typeof_169(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_170(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_170(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_171(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $11<^AExpr>
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

	// ^$22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_170(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_173(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 170);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_173(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_173(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_174(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_174(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_175(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 174);
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

	// $17<[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_175(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_176(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $7<AExpr>
	private static boolean typeof_172(Automaton.State state, Automaton automaton) {
		return typeof_177(state,automaton);
	}

	// And(^{^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...]])),^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...]]))$78<^BExpr>...})
	private static boolean typeof_53(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_178(data,automaton)) { return true; }
		}
		return false;
	}

	// $5<BExpr>
	private static boolean typeof_112(Automaton.State state, Automaton automaton) {
		return typeof_179(state,automaton);
	}

	// Or(^{^Bool$23<^BExpr>...})
	private static boolean typeof_52(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_180(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...]])),^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...]]))$78<^BExpr>...}
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

	// ^{^Or(^{$22<^BExpr>...})$22...}
	private static boolean typeof_127(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_182(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 127);
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

	// ^[^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]]
	private static boolean typeof_59(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_183(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 59);
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

	// $2<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality($31<^AExpr>)>
	private static boolean typeof_179(Automaton.State state, Automaton automaton) {
		return typeof_46(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_48(state,automaton)
			|| typeof_184(state,automaton)
			|| typeof_116(state,automaton);
	}

	// $29<Or($24<^{^BExpr...}>)>
	private static boolean typeof_184(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_159(data,automaton)) { return true; }
		}
		return false;
	}

	// False
	private static boolean typeof_126(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_False) {
			return true;
		}
		return false;
	}

	// $11<Mul($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	private static boolean typeof_58(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_185(data,automaton)) { return true; }
		}
		return false;
	}

	// $9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>
	private static boolean typeof_185(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_186(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 185);
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

	// $8<[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>
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

	// $5<^{|$2<^AExpr>...|}[$2<^AExpr>...]>
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

	// $4<{|$2<^AExpr>...|}[$2<^AExpr>...]>
	private static boolean typeof_188(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_189(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $2<^AExpr>
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

	// $16<AExpr>
	private static boolean typeof_190(Automaton.State state, Automaton automaton) {
		return typeof_191(state,automaton);
	}

	// $13<Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_191(Automaton.State state, Automaton automaton) {
		return typeof_58(state,automaton)
			|| typeof_71(state,automaton)
			|| typeof_192(state,automaton)
			|| typeof_193(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $29<Div(^[$2<^AExpr>,$2])>
	private static boolean typeof_193(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_194(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<Sum($9<^[^real,^{|$2<^AExpr>...|}[$2<^AExpr>...]]>)>
	private static boolean typeof_192(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_185(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$2<^AExpr>,$2]>
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

	// $26<[$2<^AExpr>,$2]>
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
					if(!typeof_189(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_189(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $14<^{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_176(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_196(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 176);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_196(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $13<{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_196(Automaton.State _state, Automaton automaton) {
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

	// True
	private static boolean typeof_125(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_True) {
			return true;
		}
		return false;
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_57(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_197(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 57);
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
					if(!typeof_68(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $4<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_177(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_200(state,automaton)
			|| typeof_173(state,automaton)
			|| typeof_201(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $29<Div(^[$11<^AExpr>,$11])>
	private static boolean typeof_201(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_202(data,automaton)) { return true; }
		}
		return false;
	}

	// $20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_200(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_174(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$11<^AExpr>,$11]>
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

	// $26<[$11<^AExpr>,$11]>
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
					if(!typeof_171(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_171(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^And(^{$21<^BExpr>...})$21...}
	private static boolean typeof_124(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_204(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 124);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_204(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^And(^{$21<^BExpr>...})$21...}
	private static boolean typeof_204(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_111(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$12<^AExpr>|}[$12<^AExpr>]])^$23<Mul($19<^[^real,^{|$12...|}[$12...]]>)>...|}[^Mul(^[^real,^{|$12<^AExpr>|}[$12<^AExpr>]])^$23<Mul($19<^[^real,^{|$12...|}[$12...]]>)>...]]))
	private static boolean typeof_56(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_205(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]])
	private static boolean typeof_205(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_206(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 205);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_206(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Sum(^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]])
	private static boolean typeof_206(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_207(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]]
	private static boolean typeof_207(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_208(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 207);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_208(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^real,^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]]
	private static boolean typeof_208(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_209(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]
	private static boolean typeof_209(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_210(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 209);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_210(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...|}[^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])^$22<Mul($18<^[^real,^{|$11...|}[$11...]]>)>...]
	private static boolean typeof_210(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_211(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_170(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_211(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_212(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 211);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_212(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// Mul(^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]])
	private static boolean typeof_212(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_213(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
	private static boolean typeof_213(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_214(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 213);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_214(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^real,^{|$11<^AExpr>|}[$11<^AExpr>]]
	private static boolean typeof_214(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_215(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|$11<^AExpr>|}[$11<^AExpr>]
	private static boolean typeof_215(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_216(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 215);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_216(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|$11<^AExpr>|}[$11<^AExpr>]
	private static boolean typeof_216(Automaton.State _state, Automaton automaton) {
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

	// {^Or(^{$22<^BExpr>...})$22...}
	private static boolean typeof_182(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_131(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^{^Bool$22<^BExpr>...}
	private static boolean typeof_123(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_217(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 123);
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

	// {^Bool$22<^BExpr>...}
	private static boolean typeof_217(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_218(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $21<^BExpr>
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

	// $16<BExpr>
	private static boolean typeof_219(Automaton.State state, Automaton automaton) {
		return typeof_220(state,automaton);
	}

	// $13<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality($31<^AExpr>)>
	private static boolean typeof_220(Automaton.State state, Automaton automaton) {
		return typeof_46(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_221(state,automaton)
			|| typeof_222(state,automaton)
			|| typeof_116(state,automaton);
	}

	// $26<And($24<^{^BExpr...}>)>
	private static boolean typeof_221(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_223(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Or($24<^{^BExpr...}>)>
	private static boolean typeof_222(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_223(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<^{^BExpr...}>
	private static boolean typeof_223(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_224(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 223);
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

	// $23<{^BExpr...}>
	private static boolean typeof_224(Automaton.State _state, Automaton automaton) {
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

	// ^real
	private static boolean typeof_63(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_225(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 63);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_225(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// real
	private static boolean typeof_225(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_REAL;
	}

	// [^real,^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]]
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
					if(!typeof_63(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_226(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]
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

	// {|^Num(^real)$12<^AExpr>...|}[^Num(^real)$12<^AExpr>...]
	private static boolean typeof_227(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_96(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_228(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $11<^AExpr>
	private static boolean typeof_228(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_229(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 228);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_229(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $9<AExpr>
	private static boolean typeof_229(Automaton.State state, Automaton automaton) {
		return typeof_230(state,automaton);
	}

	// $6<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_230(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_231(state,automaton)
			|| typeof_232(state,automaton)
			|| typeof_233(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $29<Div(^[$11<^AExpr>,$11])>
	private static boolean typeof_233(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_234(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$11<^AExpr>,$11]>
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

	// $26<[$11<^AExpr>,$11]>
	private static boolean typeof_235(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_228(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_228(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_232(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_236(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
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

	// $17<[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_237(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_238(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $14<^{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_238(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_239(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 238);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_239(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $13<{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_239(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_228(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_231(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_236(data,automaton)) { return true; }
		}
		return false;
	}

	// $17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_122(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_240(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 122);
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

	// $17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_62(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_241(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 62);
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

	// $16<[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_240(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_242(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $13<^{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_242(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_243(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 242);
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

	// $12<{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_243(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_97(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^Bool$23<^BExpr>...}
	private static boolean typeof_180(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_244(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 180);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_244(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Bool$23<^BExpr>...}
	private static boolean typeof_244(Automaton.State _state, Automaton automaton) {
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
						if(!typeof_245(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $22<^BExpr>
	private static boolean typeof_245(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_246(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 245);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_246(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<BExpr>
	private static boolean typeof_246(Automaton.State state, Automaton automaton) {
		return typeof_247(state,automaton);
	}

	// $13<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality($31<^AExpr>)>
	private static boolean typeof_247(Automaton.State state, Automaton automaton) {
		return typeof_46(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_248(state,automaton)
			|| typeof_249(state,automaton)
			|| typeof_116(state,automaton);
	}

	// $27<And($25<^{^BExpr...}>)>
	private static boolean typeof_248(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_250(data,automaton)) { return true; }
		}
		return false;
	}

	// $25<^{^BExpr...}>
	private static boolean typeof_250(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_251(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 250);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_251(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $24<{^BExpr...}>
	private static boolean typeof_251(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_245(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_249(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_250(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^Or(^{$22<^BExpr>...})$22...}
	private static boolean typeof_121(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_252(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 121);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_252(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {^Or(^{$22<^BExpr>...})$22...}
	private static boolean typeof_252(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_253(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_254(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $22<^BExpr>
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

	// $6<BExpr>
	private static boolean typeof_255(Automaton.State state, Automaton automaton) {
		return typeof_256(state,automaton);
	}

	// $3<Bool|Var(^string)|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality($31<^AExpr>)>
	private static boolean typeof_256(Automaton.State state, Automaton automaton) {
		return typeof_46(state,automaton)
			|| typeof_74(state,automaton)
			|| typeof_257(state,automaton)
			|| typeof_258(state,automaton)
			|| typeof_116(state,automaton);
	}

	// $29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_258(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_259(data,automaton)) { return true; }
		}
		return false;
	}

	// $25<^{^BExpr...}>
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

	// $27<And($25<^{^BExpr...}>)>
	private static boolean typeof_257(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_259(data,automaton)) { return true; }
		}
		return false;
	}

	// $24<{^BExpr...}>
	private static boolean typeof_260(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_254(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^$29<Or($25<^{^BExpr...}>)>
	private static boolean typeof_253(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_258(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 253);
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

	// ^[^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]]
	private static boolean typeof_61(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_261(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 61);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_261(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// [^real,^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]]
	private static boolean typeof_261(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_262(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]
	private static boolean typeof_262(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_263(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 262);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_263(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...|}[^Sum(^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]])$11...]
	private static boolean typeof_263(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_264(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_265(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// ^$20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_264(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_266(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 264);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_266(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $20<Sum($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_266(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_267(data,automaton)) { return true; }
		}
		return false;
	}

	// $18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_267(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_268(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 267);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_268(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $11<^AExpr>
	private static boolean typeof_265(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_269(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 265);
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

	// $17<[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>
	private static boolean typeof_268(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_270(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $14<^{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_270(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_271(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 270);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_271(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $13<{|$11<^AExpr>...|}[$11<^AExpr>...]>
	private static boolean typeof_271(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_265(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $7<AExpr>
	private static boolean typeof_269(Automaton.State state, Automaton automaton) {
		return typeof_272(state,automaton);
	}

	// $4<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_272(Automaton.State state, Automaton automaton) {
		return typeof_71(state,automaton)
			|| typeof_266(state,automaton)
			|| typeof_273(state,automaton)
			|| typeof_274(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $29<Div(^[$11<^AExpr>,$11])>
	private static boolean typeof_274(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_275(data,automaton)) { return true; }
		}
		return false;
	}

	// $27<^[$11<^AExpr>,$11]>
	private static boolean typeof_275(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_276(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 275);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_276(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $22<Mul($18<^[^real,^{|$11<^AExpr>...|}[$11<^AExpr>...]]>)>
	private static boolean typeof_273(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_267(data,automaton)) { return true; }
		}
		return false;
	}

	// $26<[$11<^AExpr>,$11]>
	private static boolean typeof_276(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_265(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_265(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $16<[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_241(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_277(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $13<^{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_277(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_278(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 277);
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

	// $12<{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_278(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_138(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// {^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13<^AExpr>|}[$13<^AExpr>]])^Mul(^[^real,^{|$13...|}[$13...]])...]])),^Inequality(^Sum(^[^real,^{|^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...|}[^Mul(^[^real,^{|$13|}[$13]])^Mul(^[^real,^{|$13...|}[$13...]])...]]))$78<^BExpr>...}
	private static boolean typeof_181(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_26(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_279(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $52<^BExpr>
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

	// $40<BExpr>
	private static boolean typeof_280(Automaton.State state, Automaton automaton) {
		return typeof_281(state,automaton);
	}

	// $37<Var(^string)|Bool|And(^{^BExpr...})|Or(^{^BExpr...})|Inequality($13<^AExpr>)>
	private static boolean typeof_281(Automaton.State state, Automaton automaton) {
		return typeof_74(state,automaton)
			|| typeof_46(state,automaton)
			|| typeof_282(state,automaton)
			|| typeof_283(state,automaton)
			|| typeof_284(state,automaton);
	}

	// $60<Or($55<^{^BExpr...}>)>
	private static boolean typeof_283(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_285(data,automaton)) { return true; }
		}
		return false;
	}

	// $57<And($55<^{^BExpr...}>)>
	private static boolean typeof_282(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_285(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<^{^BExpr...}>
	private static boolean typeof_285(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_286(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 285);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_286(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $54<{^BExpr...}>
	private static boolean typeof_286(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_279(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// Inequality($12<^AExpr>)
	private static boolean typeof_284(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Inequality) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_171(data,automaton)) { return true; }
		}
		return false;
	}

	// [^real,^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]]
	private static boolean typeof_120(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_287(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|$10<^AExpr>$10...|}[$10<^AExpr>$10...]
	private static boolean typeof_287(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_288(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 287);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_288(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// {|$10<^AExpr>$10...|}[$10<^AExpr>$10...]
	private static boolean typeof_288(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_138(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_138(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $1<Num(^real)|Sum(^[^real,^{|^AExpr...|}[^AExpr...]])|Mul(^[^real,^{|^AExpr...|}[^AExpr...]])|Div(^[^AExpr,^AExpr])|Var(^string)>
	private static boolean typeof_60(Automaton.State state, Automaton automaton) {
		return typeof_32(state,automaton)
			|| typeof_289(state,automaton)
			|| typeof_290(state,automaton)
			|| typeof_291(state,automaton)
			|| typeof_74(state,automaton);
	}

	// $19<Sum($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_289(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Sum) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_292(data,automaton)) { return true; }
		}
		return false;
	}

	// $22<Mul($17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>)>
	private static boolean typeof_290(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Mul) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_292(data,automaton)) { return true; }
		}
		return false;
	}

	// $29<Div(^[$10<^AExpr>,$10])>
	private static boolean typeof_291(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Div) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_293(data,automaton)) { return true; }
		}
		return false;
	}

	// $17<^[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_292(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_294(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 292);
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

	// $27<^[$10<^AExpr>,$10]>
	private static boolean typeof_293(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_295(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 293);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_295(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $16<[^real,^{|$10<^AExpr>...|}[$10<^AExpr>...]]>
	private static boolean typeof_294(Automaton.State _state, Automaton automaton) {
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
					if(!typeof_296(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $26<[$10<^AExpr>,$10]>
	private static boolean typeof_295(Automaton.State _state, Automaton automaton) {
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

	// $13<^{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_296(int index, Automaton automaton) {
		if(index < 0) {
			 return typeof_297(automaton.get(index),automaton);
		} else {
			int tmp = index + (automaton.nStates() * 296);
			if(visited.get(tmp)) {
				return true;
			} else {
				visited.set(tmp);
				boolean r = typeof_297(automaton.get(index),automaton);
				visited.clear(tmp);
				return r;
			}
		}
	}

	// $12<{|$10<^AExpr>...|}[$10<^AExpr>...]>
	private static boolean typeof_297(Automaton.State _state, Automaton automaton) {
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
		// Inequality($1<^AExpr>)
		Schema.Term("Inequality",Schema.Or(Schema.Term("Num",Schema.Real), Schema.Term("Sum",Schema.List(true,Schema.Any,Schema.Bag(true))), Schema.Term("Mul",Schema.Any), Schema.Term("Div",Schema.List(true,Schema.Any,Schema.Any)), Schema.Term("Var",Schema.String))),
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
