package wyone.core;

import java.io.*;
import java.util.*;
import java.math.BigInteger;
import wyautl.util.BigRational;
import wyautl.io.*;
import wyautl.core.*;
import wyone.io.*;
import wyone.core.*;
import static wyone.util.Runtime.*;

public final class Types {
	// term $3<Not($1<^Type<$3|Atom<Not(^$39<Proton<Any|Void|Bool|Int|Real|String>>)|$39>|Ref($1)|Meta($1)|Nominal(^[^string,$1])|Or($60<^{$1...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$1...|}])|List(^[$67,^[$1...]])>>)>
	public final static int K_Not = 0;
	public final static int Not(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Not, r0));
	}

	// Not(Any)
	// ^Not(^Any)
	public static boolean reduce_NFYIjG6C0tLTIg2AwcHEYcYEgwY9748x(int r0, Automaton automaton) {
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
	// ^Not(^Void)
	public static boolean reduce_NFYIjG6GLxLPZClDgkY9xBXDykmEeVJZo2(int r0, Automaton automaton) {
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

	// Not(Or({$4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> es...}))
	// ^Not(^$9<Or($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>)
	public static boolean reduce_B6osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
		Automaton.Set r5 = (Automaton.Set) automaton.get(r4);
		int j6 = 0;
		int[] t6 = new int[r5.size()-0];
		for(int i6=0;i6!=r5.size();++i6) {
			int r6 = r5.get(i6);
			if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r6,automaton)) { continue; }
			
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

	// Not(And({$4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> es...}))
	// ^Not(^$9<And($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>)
	public static boolean reduce_B6osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Term r3 = (Automaton.Term) automaton.get(r2);
		int r4 = r3.contents;
		Automaton.Set r5 = (Automaton.Set) automaton.get(r4);
		int j6 = 0;
		int[] t6 = new int[r5.size()-0];
		for(int i6=0;i6!=r5.size();++i6) {
			int r6 = r5.get(i6);
			if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r6,automaton)) { continue; }
			
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

	// term $8<And($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	public final static int K_And = 1;
	public final static int And(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}
	public final static int And(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_And, r1));
	}

	// And({$4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> t})
	// ^And(^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>})
	public static boolean reduce_D6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v6Yc2z0Ax6esU3z62(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r4,automaton)) { continue; }
			
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// And({And({$4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> xs...}), $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> ys...})
	// ^$8<And($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	public static boolean reduce_86o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_86o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r10,automaton)) { continue; }
				
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

	// term $8<Or($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	public final static int K_Or = 2;
	public final static int Or(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}
	public final static int Or(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.Set(r0));
		return automaton.add(new Automaton.Term(K_Or, r1));
	}

	// Or({$4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> t})
	// ^Or(^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>})
	public static boolean reduce_D6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v6Yc2z0Ax6esU3z62(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r4,automaton)) { continue; }
			
	
			if(r0 != r4) {
				automaton.rewrite(r0, r4);
				numReductions++;
				return true;
			}
		}
		return false;
	}

	// Or({Or({$4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> xs...}), $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> ys...})
	// ^$8<Or($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	public static boolean reduce_86ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_86ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(r4,automaton)) { continue; }
			
			Automaton.Term r5 = (Automaton.Term) automaton.get(r4);
			int r6 = r5.contents;
			Automaton.Set r7 = (Automaton.Set) automaton.get(r6);
			int j8 = 0;
			int[] t8 = new int[r7.size()-0];
			for(int i8=0;i8!=r7.size();++i8) {
				int r8 = r7.get(i8);
				if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
			int j10 = 0;
			int[] t10 = new int[r3.size()-1];
			for(int i10=0;i10!=r3.size();++i10) {
				int r10 = r3.get(i10);
				if(i10 == i4 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r10,automaton)) { continue; }
				
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

	// And({Void, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> xs...})
	// ^And(^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	public static boolean reduce_N6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_3GLxLPZCWDggY9w3x$(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r5,automaton)) { continue; }
				
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

	// And({Any, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> xs...})
	// ^And(^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	public static boolean reduce_N6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_3C0tLTIc2Avc1EGE(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r5,automaton)) { continue; }
				
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

	// And({Proton<Any|Void|Bool|Int|Real|String> a1, Proton<Any|Void|Bool|Int|Real|String> a2, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> ts...})
	// ^And(^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,$39<^Type<Atom<$24|Not($25)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...})
	public static boolean reduce_E6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zl7uoUkXzW9z60XmE(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(r4,automaton)) { continue; }
			
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(r5,automaton)) { continue; }
				
				int j6 = 0;
				int[] t6 = new int[r3.size()-2];
				for(int i6=0;i6!=r3.size();++i6) {
					int r6 = r3.get(i6);
					if(i6 == i4 || i6 == i5 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r6,automaton)) { continue; }
					
					t6[j6++] = r6;
				}
				Automaton.Set r7 = new Automaton.Set(t6);
	
				boolean r8 = r4 != r5;         // a1 neq a2
				boolean r9 = false;            // a1 neq a2 && !a2 is ^Any
				if(r8) {
					boolean r10 = typeof_3C0tLTIc2Avc1EGE(r5,automaton); // a2 is ^Any
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

	// And({Proton<Any|Void|Bool|Int|Real|String> a1, Not(Proton<Any|Void|Bool|Int|Real|String> a2), $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> ts...})
	// ^And(^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,^$28<Not($25)>,$40<^Type<Atom<$24|$28>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	public static boolean reduce_N6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoUBXDy6gwUc1XHZcB(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(r4,automaton)) { continue; }
			
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_wFYIjG6C0tLTIg2AwFZKj_5OIs2AzFZFjx5QIZ3A9GIHiGr3BlHI38KOWlq3El1LZC4Sm_aQbCWLggKNxVoHD_4MQo4OF8rQoxaQYw_MhVa9dCXDelmPekLoq2(r5,automaton)) { continue; }
				
				Automaton.Term r6 = (Automaton.Term) automaton.get(r5);
				int r7 = r6.contents;
				int j8 = 0;
				int[] t8 = new int[r3.size()-2];
				for(int i8=0;i8!=r3.size();++i8) {
					int r8 = r3.get(i8);
					if(i8 == i4 || i8 == i5 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r8,automaton)) { continue; }
					
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

	// Or({Any, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> xs...})
	// ^Or(^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	public static boolean reduce_N6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_3C0tLTIc2Avc1EGE(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r5,automaton)) { continue; }
				
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

	// Or({Void, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> xs...})
	// ^Or(^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	public static boolean reduce_N6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.Set r3 = (Automaton.Set) automaton.get(r2);
		for(int i4=0;i4!=r3.size();++i4) {
			int r4 = r3.get(i4);
			if(!typeof_3GLxLPZCWDggY9w3x$(r4,automaton)) { continue; }
			
			int j5 = 0;
			int[] t5 = new int[r3.size()-1];
			for(int i5=0;i5!=r3.size();++i5) {
				int r5 = r3.get(i5);
				if(i5 == i4 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r5,automaton)) { continue; }
				
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

	// term $3<Ref($1<^Type<$3|Atom<Not(^$40<Proton<Any|Void|Bool|Int|Real|String>>)|$40>|Not($1)|Meta($1)|Nominal(^[^string,$1])|Or($60<^{$1...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$1...|}])|List(^[$67,^[$1...]])>>)>
	public final static int K_Ref = 9;
	public final static int Ref(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Ref, r0));
	}

	// Ref(Void)
	// ^Ref(^Void)
	public static boolean reduce_NFYJ_O5GLxLPZClDgkY9xBXDykmEeVJZo2(int r0, Automaton automaton) {
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

	// term $3<Meta($1<^Type<$3|Atom<Not(^$40<Proton<Any|Void|Bool|Int|Real|String>>)|$40>|Not($1)|Ref($1)|Nominal(^[^string,$1])|Or($60<^{$1...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$1...|}])|List(^[$67,^[$1...]])>>)>
	public final static int K_Meta = 10;
	public final static int Meta(Automaton automaton, int r0) {
		return automaton.add(new Automaton.Term(K_Meta, r0));
	}

	// term Term($7<^[^string,$3<^Type<Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Nominal($7)|Or($63<^{$3...}>)|And($63)|Set(^[$70<^bool>,$63])|Bag(^[$70,^{|$3...|}])|List(^[$70,^[$3...]])>>]>)
	public final static int K_Term = 11;
	public final static int Term(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}
	public final static int Term(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Term, r1));
	}

	// term $9<Nominal(^[^string,$3<^Type<$9|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>])>
	public final static int K_Nominal = 12;
	public final static int Nominal(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}
	public final static int Nominal(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Nominal, r1));
	}

	// Nominal([string, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> t])
	// ^$9<Nominal(^[^string,$3<^Type<$9|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>])>
	public static boolean reduce_86otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yW9A4u62(int r0, Automaton automaton) {
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

	// term Fun(^[$2<^Type<Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($67<^{$2...}>)|And($67)|Set(^[$74<^bool>,$67])|Bag(^[$74,^{|$2...|}])|List(^[$74,^[$2...]])>>,$2])
	public final static int K_Fun = 13;
	public final static int Fun(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}
	public final static int Fun(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Fun, r1));
	}

	// term $14<Set(^[$2<^bool>,$8<^{$4<^Type<$14|Atom<Not(^$51<Proton<Any|Void|Bool|Int|Real|String>>)|$51>|Not($4)|Ref($4)|Meta($4)|Nominal(^[^string,$4])|Or($8)|And($8)|Bag(^[$2,^{|$4...|}])|List(^[$2,^[$4...]])>>...}>])>
	public final static int K_Set = 14;
	public final static int Set(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}
	public final static int Set(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Set, r1));
	}

	// Set([bool b, {Void, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> ts...}])
	// ^Set(^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}])
	public static boolean reduce_R6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Set r6 = (Automaton.Set) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_3GLxLPZCWDggY9w3x$(r7,automaton)) { continue; }
			
			int j8 = 0;
			int[] t8 = new int[r6.size()-1];
			for(int i8=0;i8!=r6.size();++i8) {
				int r8 = r6.get(i8);
				if(i8 == i7 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
	
			int r10 = automaton.add(r9);
			Automaton.List r11 = new Automaton.List(r4, r10); // [bts]
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

	// term $14<Bag(^[$2<^bool>,^{|$4<^Type<$14|Atom<Not(^$51<Proton<Any|Void|Bool|Int|Real|String>>)|$51>|Not($4)|Ref($4)|Meta($4)|Nominal(^[^string,$4])|Or($72<^{$4...}>)|And($72)|Set(^[$2,$72])|List(^[$2,^[$4...]])>>...|}])>
	public final static int K_Bag = 15;
	public final static int Bag(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}
	public final static int Bag(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_Bag, r1));
	}

	// Bag([bool b, {|Void, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> ts...|}])
	// ^Bag(^[$2<^bool>,^{|^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...|}])
	public static boolean reduce_R6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Bag r6 = (Automaton.Bag) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_3GLxLPZCWDggY9w3x$(r7,automaton)) { continue; }
			
			int j8 = 0;
			int[] t8 = new int[r6.size()-1];
			for(int i8=0;i8!=r6.size();++i8) {
				int r8 = r6.get(i8);
				if(i8 == i7 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Bag r9 = new Automaton.Bag(t8);
	
			int r10 = automaton.add(r9);
			Automaton.List r11 = new Automaton.List(r4, r10); // [bts]
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

	// term $14<List(^[$2<^bool>,^[$4<^Type<$14|Atom<Not(^$51<Proton<Any|Void|Bool|Int|Real|String>>)|$51>|Not($4)|Ref($4)|Meta($4)|Nominal(^[^string,$4])|Or($72<^{$4...}>)|And($72)|Set(^[$2,$72])|Bag(^[$2,^{|$4...|}])>>...]])>
	public final static int K_List = 16;
	public final static int List(Automaton automaton, int... r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}
	public final static int List(Automaton automaton, List<Integer> r0) {
		int r1 = automaton.add(new Automaton.List(r0));
		return automaton.add(new Automaton.Term(K_List, r1));
	}

	// List([bool b, {Void, $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>> ts...}])
	// ^List(^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}])
	public static boolean reduce_R63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(int r0, Automaton automaton) {
		Automaton.Term r1 = (Automaton.Term) automaton.get(r0);
		int r2 = r1.contents;
		Automaton.List r3 = (Automaton.List) automaton.get(r2);
		int r4 = r3.get(0);
		int r5 = r3.get(1);
		Automaton.Set r6 = (Automaton.Set) automaton.get(r5);
		for(int i7=0;i7!=r6.size();++i7) {
			int r7 = r6.get(i7);
			if(!typeof_3GLxLPZCWDggY9w3x$(r7,automaton)) { continue; }
			
			int j8 = 0;
			int[] t8 = new int[r6.size()-1];
			for(int i8=0;i8!=r6.size();++i8) {
				int r8 = r6.get(i8);
				if(i8 == i7 || !typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(r8,automaton)) { continue; }
				
				t8[j8++] = r8;
			}
			Automaton.Set r9 = new Automaton.Set(t8);
	
			Automaton.Term r10 = Void;
			int r11 = automaton.add(r10);
			if(r0 != r11) {
				automaton.rewrite(r0, r11);
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
				
				if(typeof_NFYIjG6C0tLTIg2AwcHEYcYEgwY9748x(i,automaton)) {
					changed |= reduce_NFYIjG6C0tLTIg2AwcHEYcYEgwY9748x(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_NFYIjG6GLxLPZClDgkY9xBXDykmEeVJZo2(i,automaton)) {
					changed |= reduce_NFYIjG6GLxLPZClDgkY9xBXDykmEeVJZo2(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_B6osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(i,automaton)) {
					changed |= reduce_B6osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_B6osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(i,automaton)) {
					changed |= reduce_B6osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_D6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v6Yc2z0Ax6esU3z62(i,automaton)) {
					changed |= reduce_D6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v6Yc2z0Ax6esU3z62(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_86o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(i,automaton)) {
					changed |= reduce_86o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_D6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v6Yc2z0Ax6esU3z62(i,automaton)) {
					changed |= reduce_D6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v6Yc2z0Ax6esU3z62(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_86ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(i,automaton)) {
					changed |= reduce_86ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_N6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton)) {
					changed |= reduce_N6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_N6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton)) {
					changed |= reduce_N6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_E6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zl7uoUkXzW9z60XmE(i,automaton)) {
					changed |= reduce_E6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zl7uoUkXzW9z60XmE(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_N6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoUBXDy6gwUc1XHZcB(i,automaton)) {
					changed |= reduce_N6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoUBXDy6gwUc1XHZcB(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_N6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton)) {
					changed |= reduce_N6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_N6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton)) {
					changed |= reduce_N6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_NFYJ_O5GLxLPZClDgkY9xBXDykmEeVJZo2(i,automaton)) {
					changed |= reduce_NFYJ_O5GLxLPZClDgkY9xBXDykmEeVJZo2(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_86otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yW9A4u62(i,automaton)) {
					changed |= reduce_86otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yW9A4u62(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_R6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(i,automaton)) {
					changed |= reduce_R6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_R6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(i,automaton)) {
					changed |= reduce_R6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(i,automaton);
					if(changed) { break; } // reset
				}
				
				if(typeof_R63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(i,automaton)) {
					changed |= reduce_R63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(i,automaton);
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

	// ^Set(^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}])
	private static boolean typeof_R6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(int index, Automaton automaton) {
		return typeof_Q6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggc3B9z(automaton.get(index),automaton);
	}

	// ^And(^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,$39<^Type<Atom<$24|Not($25)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...})
	private static boolean typeof_E6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zl7uoUkXzW9z60XmE(int index, Automaton automaton) {
		return typeof_D6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zl7uoUkXzGwUB(automaton.get(index),automaton);
	}

	// ^Not(^Void)
	private static boolean typeof_NFYIjG6GLxLPZClDgkY9xBXDykmEeVJZo2(int index, Automaton automaton) {
		return typeof_6CDx5S3OpQdGq3vk1Eeon7us2Az370(automaton.get(index),automaton);
	}

	// Not(^Void)
	private static boolean typeof_6CDx5S3OpQdGq3vk1Eeon7us2Az370(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3GLxLPZCWDggY9w3x$(data,automaton)) { return true; }
		}
		return false;
	}

	// ^List(^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}])
	private static boolean typeof_R63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(int index, Automaton automaton) {
		return typeof_Q63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggc3B9z(automaton.get(index),automaton);
	}

	// ^Ref(^Void)
	private static boolean typeof_NFYJ_O5GLxLPZClDgkY9xBXDykmEeVJZo2(int index, Automaton automaton) {
		return typeof_6CHKaO3OpQdGq3vk1Eeon7us2Az370(automaton.get(index),automaton);
	}

	// Ref(^Void)
	private static boolean typeof_6CHKaO3OpQdGq3vk1Eeon7us2Az370(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3GLxLPZCWDggY9w3x$(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$9<Nominal(^[^string,$3<^Type<$9|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>])>
	private static boolean typeof_86otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yW9A4u62(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgoE(automaton.get(index),automaton);
	}

	// ^Or(^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>})
	private static boolean typeof_D6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v6Yc2z0Ax6esU3z62(int index, Automaton automaton) {
		return typeof_C6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v6Yc2z0Ax6WzlE(automaton.get(index),automaton);
	}

	// ^Not(^Any)
	private static boolean typeof_NFYIjG6C0tLTIg2AwcHEYcYEgwY9748x(int index, Automaton automaton) {
		return typeof_6CDx5So3ZQtClDgkY9xBXDykmE0H(automaton.get(index),automaton);
	}

	// Not(^Any)
	private static boolean typeof_6CDx5So3ZQtClDgkY9xBXDykmE0H(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_3C0tLTIc2Avc1EGE(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Or(^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_N6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int index, Automaton automaton) {
		return typeof_E6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(automaton.get(index),automaton);
	}

	// ^Or(^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_N6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int index, Automaton automaton) {
		return typeof_E6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(automaton.get(index),automaton);
	}

	// ^And(^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_N6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int index, Automaton automaton) {
		return typeof_E6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(automaton.get(index),automaton);
	}

	// ^And(^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_N6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzW979GXmE(int index, Automaton automaton) {
		return typeof_E6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(automaton.get(index),automaton);
	}

	// ^Bag(^[$2<^bool>,^{|^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...|}])
	private static boolean typeof_R6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZcBXD99ggcc1YHocB(int index, Automaton automaton) {
		return typeof_Q6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZcBXD99ggc3B9z(automaton.get(index),automaton);
	}

	// ^Not(^Proton<Any|Void|Bool|Int|Real|String>)
	private static boolean typeof_wFYIjG6C0tLTIg2AwFZKj_5OIs2AzFZFjx5QIZ3A9GIHiGr3BlHI38KOWlq3El1LZC4Sm_aQbCWLggKNxVoHD_4MQo4OF8rQoxaQYw_MhVa9dCXDelmPekLoq2(int index, Automaton automaton) {
		return typeof_vFYIjG6C0tLTIg2AwFZKj_5OIs2AzFZFjx5QIZ3A9GIHiGr3BlHI38KOWlq3El1LZC4Sm_aQbCWLggKNxVoHD_4MQo4OF8rQoxaQYw_MhVa9dCXDelmP0QE(automaton.get(index),automaton);
	}

	// Not(^Proton<Any|Void|Bool|Int|Real|String>)
	private static boolean typeof_vFYIjG6C0tLTIg2AwFZKj_5OIs2AzFZFjx5QIZ3A9GIHiGr3BlHI38KOWlq3El1LZC4Sm_aQbCWLggKNxVoHD_4MQo4OF8rQoxaQYw_MhVa9dCXDelmP0QE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>})
	private static boolean typeof_C6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v6Yc2z0Ax6WzlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_A6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v60zlE(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>}
	private static boolean typeof_A6ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyW9v60zlE(int index, Automaton automaton) {
		return typeof_96ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyGgUB(automaton.get(index),automaton);
	}

	// $9<Nominal(^[^string,$3<^Type<$9|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>])>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgoE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZoE(data,automaton)) { return true; }
		}
		return false;
	}

	// $7<^[^string,$3<^Type<Nominal($7)|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZoE(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVoE(automaton.get(index),automaton);
	}

	// $6<[^string,$3<^Type<Nominal(^$6)|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVoE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^string
	private static boolean typeof_W9O3u$(int index, Automaton automaton) {
		return typeof_FZ0(automaton.get(index),automaton);
	}

	// string
	private static boolean typeof_FZ0(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_STRING;
	}

	// $3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVpE(automaton.get(index),automaton);
	}

	// $14<Type<Nominal(^[^string,$3<^$14>])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVpE(Automaton.State state, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGooE(state,automaton);
	}

	// $11<Nominal(^[^string,$3<^Type<$11>>])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGooE(Automaton.State state, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgoE(state,automaton)
			|| typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcOB(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGoOB(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVPB(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVQB(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgQB(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgRB(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsSB(state,automaton)
			|| typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZUB(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$3...|}])|$95>>...]])>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZUB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwTB(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$3...|}])|List($93)>>...]]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwTB(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsTB(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$3...|}])|List(^$92)>>...]]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgTB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgTB(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcTB(automaton.get(index),automaton);
	}

	// $88<[$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67,$60])|$84|List(^[$67,^[$3...]])>>...|}])>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsSB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGkSB(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67,$60])|Bag($82)|List(^[$67,^[$3...]])>>...|}]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGkSB(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgSB(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67,$60])|Bag(^$81)|List(^[$67,^[$3...]])>>...|}]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgSB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVSB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,$78])|List(^[$67,^[$3...]])>>...|}>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVSB(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwRB(automaton.get(index),automaton);
	}

	// $77<{|$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^$77])|List(^[$67,^[$3...]])>>...|}>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$60<^{$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60)|And($60)|$73|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>])>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgRB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZRB(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$60<^{$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60)|And($60)|Set($71)|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZRB(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVRB(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$60<^{$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60)|And($60)|Set(^$70)|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>]>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^{$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(int index, Automaton automaton) {
		return typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGoPB(automaton.get(index),automaton);
	}

	// $59<{$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^$59>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGoPB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($60<^{$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60)|$65|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Or($60<^{$3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|$62|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Meta($3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|$54|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVPB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(data,automaton)) { return true; }
		}
		return false;
	}

	// $51<Ref($3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|$51|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGoOB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(data,automaton)) { return true; }
		}
		return false;
	}

	// $48<Not($3<^Type<Nominal(^[^string,$3])|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|$48|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcOB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76otoQh_aQWlqG_BJ0fGsdbGqlbRyG4Z5GbZNGdFY_HIncn$p$rNrNtkv7zN2a7XewVfgGRedLTX5bTYDKkB58AhNOfFs1nNYi797uTfhGymfLFYaVRAj8JY_p7merHYg8PY4r7HcrTYh8kY_wc88rcQXxNWEXZegkNQ1zNtEmaXDtHnhna7lV44YkNvCxu2lNlN252mVHircvCr5llclc_6YGme6LmBcGemBQZCYTNWnVca9Yik5lnc6LyB7t8tiGon7e5f5jkPcHfl7d5i5gwPFIFiGq7t5i5gcQFnJ_Gb9RBHjWfl7vsQZnjW976YkQZRkXm0C14qOHon7e5C6ssRcmnl7x5N6YgIqGDP6egSB1n0r0AS63lJPnGr3xBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(data,automaton)) { return true; }
		}
		return false;
	}

	// List(^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}])
	private static boolean typeof_Q63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggc3B9z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_O63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZc399z(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}]
	private static boolean typeof_O63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZc399z(int index, Automaton automaton) {
		return typeof_N63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79GXm(automaton.get(index),automaton);
	}

	// ^bool
	private static boolean typeof_W9R3u$(int index, Automaton automaton) {
		return typeof_Fk0(automaton.get(index),automaton);
	}

	// bool
	private static boolean typeof_Fk0(Automaton.State state, Automaton automaton) {
		return state.kind == Automaton.K_BOOL;
	}

	// Atom<Not(^$31<Proton<Any|Void|Bool|Int|Real|String>>)|$31>
	private static boolean typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(Automaton.State state, Automaton automaton) {
		return typeof_xFYIjGb9iCXDvk1EWoYQQs2C0tLTIV3A8GZKj_5OIg3ABGZFjx5QIs3AEGIHiGr3OlXL38KOWlq3RlHMZC4Sm_aQbClMgVLN9p3LQtKPQc5OF8rQoxaQYkqPhoLwn2(state,automaton);
	}

	// Not(^$28<Proton<Any|Void|Bool|Int|Real|String>>)|$28
	private static boolean typeof_xFYIjGb9iCXDvk1EWoYQQs2C0tLTIV3A8GZKj_5OIg3ABGZFjx5QIs3AEGIHiGr3OlXL38KOWlq3RlHMZC4Sm_aQbClMgVLN9p3LQtKPQc5OF8rQoxaQYkqPhoLwn2(Automaton.State state, Automaton automaton) {
		return typeof_vFYIjGb9gCXDvk1Eo3ZQtCWEgw2GLxLPZCGHgc3G1xqQgC0Igo3C8t5SIw3ANGZJ_45QIc4AQGqJo8MPiSq3SlXMWWoHD_4MUh0PZ0_RjGrQiCXPdpmPGEE(state,automaton)
			|| typeof_jFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(state,automaton);
	}

	// Not(^Proton<Any|Void|Bool|Int|Real|String>)
	private static boolean typeof_vFYIjGb9gCXDvk1Eo3ZQtCWEgw2GLxLPZCGHgc3G1xqQgC0Igo3C8t5SIw3ANGZJ_45QIc4AQGqJo8MPiSq3SlXMWWoHD_4MUh0PZ0_RjGrQiCXPdpmPGEE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_scHPo3ZQtClDgk2GLxLPZCWEgw2G1xqQgCGHgc3C8t5SIk3ACGZJ_45QIw3ANGqJo8MPiSq3PlmLWp2HAtJLRhGMZ0_RjGrQiCmMTp1PWDE(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton<Any|Void|Bool|Int|Real|String>
	private static boolean typeof_scHPo3ZQtClDgk2GLxLPZCWEgw2G1xqQgCGHgc3C8t5SIk3ACGZJ_45QIw3ANGqJo8MPiSq3PlmLWp2HAtJLRhGMZ0_RjGrQiCmMTp1PWDE(int index, Automaton automaton) {
		return typeof_jFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(automaton.get(index),automaton);
	}

	// Proton<Any|Void|Bool|Int|Real|String>
	private static boolean typeof_jFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(Automaton.State state, Automaton automaton) {
		return typeof_gFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5R4Sx(state,automaton);
	}

	// Any|Void|Bool|Int|Real|String
	private static boolean typeof_gFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5R4Sx(Automaton.State state, Automaton automaton) {
		return typeof_2C0tLTIc2Av3w$(state,automaton)
			|| typeof_2GLxLPZCWDggIk2(state,automaton)
			|| typeof_2G1xqQgCWDggIk2(state,automaton)
			|| typeof_2C8t5SIc2Av3w$(state,automaton)
			|| typeof_2GHKLNgCWDggIk2(state,automaton)
			|| typeof_2OIGbRdtqOIc2Av3w$(state,automaton);
	}

	// Int
	private static boolean typeof_2C8t5SIc2Av3w$(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Int) {
			return true;
		}
		return false;
	}

	// Real
	private static boolean typeof_2GHKLNgCWDggIk2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Real) {
			return true;
		}
		return false;
	}

	// String
	private static boolean typeof_2OIGbRdtqOIc2Av3w$(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_String) {
			return true;
		}
		return false;
	}

	// Any
	private static boolean typeof_2C0tLTIc2Av3w$(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Any) {
			return true;
		}
		return false;
	}

	// ^And(^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,^$28<Not($25)>,$40<^Type<Atom<$24|$28>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_N6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoUBXDy6gwUc1XHZcB(int index, Automaton automaton) {
		return typeof_E6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoUBXDy6gwU379z(automaton.get(index),automaton);
	}

	// And(^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,^$28<Not($25)>,$40<^Type<Atom<$24|$28>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_E6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoUBXDy6gwU379z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_C6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoU3y6z(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_E6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_C6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_C6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(int index, Automaton automaton) {
		return typeof_B6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(automaton.get(index),automaton);
	}

	// {^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_B6ZwZRMXguho7Q1TNd6Kw8ocA5Y62CYKYR2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAP1mhwakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_3GLxLPZCWDggY9w3x$(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(automaton.get(index),automaton);
	}

	// $9<Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39<^$9>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(Automaton.State state, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(state,automaton);
	}

	// $6<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39<^Type<$6>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(Automaton.State state, Automaton automaton) {
		return typeof_7GZKj_5OIc2AvFYIjGb9tCHEykmEWVJTQZ3G0GrQhCmH9p1Io3ZQtCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3s5z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(state,automaton)
			|| typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3h5z(automaton.get(index),automaton);
	}

	// $59<{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^$59>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3h5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($60<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|$65|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Or($60<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$62|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3s5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Nominal(^[^string,$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|$56|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<^[^string,$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal($54)|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(int index, Automaton automaton) {
		return typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(automaton.get(index),automaton);
	}

	// $53<[^string,$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^$53)|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMXguho7Q1TNdbF2iLBRBmBJCkCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_hc87rmQXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// Or(^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_E6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_C6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_C6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(int index, Automaton automaton) {
		return typeof_B6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(automaton.get(index),automaton);
	}

	// {^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($61<^{$40...}>)|And($61)|Set(^[$68<^bool>,$61])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_B6ZwZRMP1mhwaV6LuG4U1CuXlslFnNp7rVrVtsvFzN2Z7XewVfgGJ9dLLX5bTYDKHB5d8bNtdFk9lNYg797uTfhGqHfLsX7fTACmakALyX7WTvCqa7CLJYaZRYi8NY_q7Hem1Yh8TY4w7Q9uZACrDQ1xNOjkw0hpRAEyqcEmVHircvCr5tEzP0lF4a5d842LLBcGemBQZ5242LRB7edmcAgGkBJBN8nVDbTfhoRABqDeZl7S595YkOsOZnbW9c5YgOZPkXeG395Yg2flAh5esPBXDj5gVQFIFiGq7u5j5ggQFnJ_Gb9RBXjlfl7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_3C0tLTIc2Avc1EGE(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(automaton.get(index),automaton);
	}

	// $9<Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^$9>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(Automaton.State state, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(state,automaton);
	}

	// $6<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^Type<$6>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(Automaton.State state, Automaton automaton) {
		return typeof_7GIFi_r3ukmDosoQodHTYoYEgwI77_r58GJFoxLQYgZHhk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3s5z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(state,automaton)
			|| typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3h5z(automaton.get(index),automaton);
	}

	// $59<{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^$59>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3h5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|$65|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Or($60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$62|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3s5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Nominal(^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|$56|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal($54)|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(int index, Automaton automaton) {
		return typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(automaton.get(index),automaton);
	}

	// $53<[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^$53)|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMP1mhwaV6Lu8oVf426YB2K2RYSY_2jYwYCyW7em2im588ZPG8PRuCuJ0lN3aPX4b1t83YCkXcVcvhq5GfgPOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1nbjJfCwqQ2y729tTYirRQ3zuf_zJOdGN9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YhY9OB1bGXl7Q5S5tsOcmbl7P5c5gZP3GXl7vgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,^$28<Not($25)>,$40<^Type<Atom<$24|$28>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_C6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6eoU3y6z(int index, Automaton automaton) {
		return typeof_B6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6Gzl(automaton.get(index),automaton);
	}

	// {$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,^$28<Not($25)>,$40<^Type<Atom<$24|$28>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_B6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdCKRAo7lVlslFnNpFrVrVtsvFzN2w7XewVfgGoAjLqA3pRQYy797uTfhGynjLFBJWjGNIlV_42PZeBn5dlFlV462XPeBu1Q3B242LmBcndrPACr1YhY9OBHbWXl7R5T5twOc1el7Q5d5gcPFmIm4WXl7voPwXfW9j5YkPVQkHil7uVQkmi0CIK5Sekl7y5s5YkOwQZ2mW986YoQcRkmm0C14qOHcNBmDD6swRc1ql7y5O6YkOcSZnqW9R6YoRoSkXr0GB_qRoCWXl7vZTZYuW9f6YsQkTB1bGvGDi6ewTB1u0y0At6lgqQ95YgnylAw6Gzl(Automaton.State _state, Automaton automaton) {
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
							if(!typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(child,automaton)) { result=false; break; }
						}
						else if(i == s1) {
							if(!typeof_wFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmPekLoq2(child,automaton)) { result=false; break; }
						}
						else {
							if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
						}
					}
					if(result) { return true; } // found match
				}
			}
		}
		return false;
	}

	// $39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgrE(automaton.get(index),automaton);
	}

	// $33<Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39<^$33>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgrE(Automaton.State state, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVrE(state,automaton);
	}

	// $30<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39<^Type<$30>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVrE(Automaton.State state, Automaton automaton) {
		return typeof_7GIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmPWV5QQo5G0GrQhCmQip1TGTE(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgNB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsNB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZOB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcPB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZQB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgQB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgRB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsSB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZUB(state,automaton);
	}

	// Atom<$22<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$22)>
	private static boolean typeof_7GIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmPWV5QQo5G0GrQhCmQip1TGTE(Automaton.State state, Automaton automaton) {
		return typeof_xFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmPWV5QQoLsq2(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZUB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwTB(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwTB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsTB(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgTB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgTB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcTB(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsSB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGkSB(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGkSB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgSB(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgSB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVSB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVSB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwRB(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgRB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZRB(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZRB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVRB(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsPB(automaton.get(index),automaton);
	}

	// $60<{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^$60>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsPB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|$65|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Or($61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$63|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Nominal(^[^string,$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|$56|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcPB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<^[^string,$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal($54)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVPB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwOB(automaton.get(index),automaton);
	}

	// $53<[^string,$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^$53)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwOB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZOB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGCPRuCu5WhpNdhF23Y426YB2KYRYSY_2jYwYCTY7em2im58EwPGjFJnhCsYcVcvhq5ljyPtEKqQ3qHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^Proton<Any|Void|Bool|Int|Real|String>)
	private static boolean typeof_wFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmPekLoq2(int index, Automaton automaton) {
		return typeof_vFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmP0QE(automaton.get(index),automaton);
	}

	// Not(^Proton<Any|Void|Bool|Int|Real|String>)
	private static boolean typeof_vFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmP0QE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton<Any|Void|Bool|Int|Real|String>
	private static boolean typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(int index, Automaton automaton) {
		return typeof_jFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(automaton.get(index),automaton);
	}

	// Bag(^[$2<^bool>,^{|^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...|}])
	private static boolean typeof_Q6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZcBXD99ggc3B9z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_O6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZc399z(data,automaton)) { return true; }
		}
		return false;
	}

	// Atom<Not(^$31<Proton<Any|Void|Bool|Int|Real|String>>)|$31>
	private static boolean typeof_7GIFi_r3ukmDosoQodHTYoYEgwI77_r58GJFoxLQYgZHhk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(Automaton.State state, Automaton automaton) {
		return typeof_xFIFi_r3ukmDosoQodXQYoYEgwI77tq58GZKj_5OIg3ABGZFjx5QIs3AEGIHiGr3OlXL38KOWlq3RlHMZC4Sm_aQbClMgVLNwo3LQtKPQc5OF8rQoxaQYkqPhoLco2(state,automaton);
	}

	// Atom<Not(^$31<Proton<Void|Any|Bool|Int|Real|String>>)|$31>
	private static boolean typeof_7GZKj_5OIc2AvFYIjGb9tCHEykmEWVJTQZ3G0GrQhCmH9p1Io3ZQtCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(Automaton.State state, Automaton automaton) {
		return typeof_xFZKj_5OIc2AvFYIjGb9iCHEykmEWVZQQZ3C0tLTIg3ABGZFjx5QIs3AEGIHiGr3OlXL38KOWlq3RlHMZC4Sm_aQbClMgVLNwo3LQtKPQc5OF8rQoxaQYkqPhoLco2(state,automaton);
	}

	// ^$8<Or($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	private static boolean typeof_86ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WH2(automaton.get(index),automaton);
	}

	// $8<Or($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WH2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(data,automaton)) { return true; }
		}
		return false;
	}

	// $6<^{$2<^Type<Or($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lE2(automaton.get(index),automaton);
	}

	// $5<{$2<^Type<Or($6<^$5>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lE2(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lI2(automaton.get(index),automaton);
	}

	// $13<Type<Or($6<^{$2<^$13>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lI2(Automaton.State state, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60I2(state,automaton);
	}

	// $10<Or($6<^{$2<^Type<$10>>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60I2(Automaton.State state, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WH2(state,automaton)
			|| typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(state,automaton)
			|| typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67,$6])|Bag(^[$67,^{|$2...|}])|$95>>...]])>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67,$6])|Bag(^[$67,^{|$2...|}])|List($93)>>...]]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67,$6])|Bag(^[$67,^{|$2...|}])|List(^$92)>>...]]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(automaton.get(index),automaton);
	}

	// $88<[$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67,$6])|$84|List(^[$67,^[$2...]])>>...|}])>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67,$6])|Bag($82)|List(^[$67,^[$2...]])>>...|}]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67,$6])|Bag(^$81)|List(^[$67,^[$2...]])>>...|}]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,$78])|List(^[$67,^[$2...]])>>...|}>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(automaton.get(index),automaton);
	}

	// $77<{|$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^$77])|List(^[$67,^[$2...]])>>...|}>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$6<^{$2<^Type<Or($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|$73|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>])>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$6<^{$2<^Type<Or($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set($71)|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$6<^{$2<^Type<Or($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^$70)|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($6<^{$2<^Type<Or($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|$65|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|$62|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>])>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal($60)|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(int index, Automaton automaton) {
		return typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(automaton.get(index),automaton);
	}

	// $59<[^string,$2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^$59)|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>]>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $53<Meta($2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|$53|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>)>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<Ref($2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|$50|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>)>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<Not($2<^Type<Or($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|$47|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>)>
	private static boolean typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRMKT91T5ObTTl6KyO3SFX_Z1CJ1hB2DYE2RYSY_2jYwYCLX7em2im5d8aPl8PRuCuJtlN3gmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3gsW4B2PZeBn5GnV6LqB7edmcAgGuBsW4E2uRuhqHnhkNvcH5YcPkn7d5f5tkPcHfl7c5i5gwPFIFiGq7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$8<And($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	private static boolean typeof_86o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ecJcUw2(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WH2(automaton.get(index),automaton);
	}

	// $8<And($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WH2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(data,automaton)) { return true; }
		}
		return false;
	}

	// $6<^{$2<^Type<And($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lE2(automaton.get(index),automaton);
	}

	// $5<{$2<^Type<And($6<^$5>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lE2(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lI2(automaton.get(index),automaton);
	}

	// $13<Type<And($6<^{$2<^$13>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lI2(Automaton.State state, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60I2(state,automaton);
	}

	// $10<And($6<^{$2<^Type<$10>>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60I2(Automaton.State state, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WH2(state,automaton)
			|| typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(state,automaton)
			|| typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67,$6])|Bag(^[$67,^{|$2...|}])|$95>>...]])>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67,$6])|Bag(^[$67,^{|$2...|}])|List($93)>>...]]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67,$6])|Bag(^[$67,^{|$2...|}])|List(^$92)>>...]]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(automaton.get(index),automaton);
	}

	// $88<[$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67,$6])|$84|List(^[$67,^[$2...]])>>...|}])>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67,$6])|Bag($82)|List(^[$67,^[$2...]])>>...|}]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67,$6])|Bag(^$81)|List(^[$67,^[$2...]])>>...|}]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,$78])|List(^[$67,^[$2...]])>>...|}>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(automaton.get(index),automaton);
	}

	// $77<{|$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^$77])|List(^[$67,^[$2...]])>>...|}>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$6<^{$2<^Type<And($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|$73|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>])>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$6<^{$2<^Type<And($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set($71)|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$6<^{$2<^Type<And($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^$70)|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<Or($6<^{$2<^Type<And($6)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|$65|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|$62|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>])>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal($60)|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(int index, Automaton automaton) {
		return typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(automaton.get(index),automaton);
	}

	// $59<[^string,$2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^$59)|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>]>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $53<Meta($2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|$53|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>)>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<Ref($2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|$50|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>)>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<Not($2<^Type<And($6<^{$2...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|$47|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>)>
	private static boolean typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSKtd7sOZSut4UJtbGo08LH9oF8omBsByBkCqCLDwDmEqN8Xc9Es9QZaNe4bcldrcI_6YGk9ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGkX6LkBcGemBQ3C2TNOnVca9Yik5dnV6LwBcndrPACr1YhY9OBXe0EYZPgPZ2fW9h5YVPsPkmf08E8r7t57lXi0CIK5Sekl7x57CHeWjGDz5eVRB1jGm0A96o7JNb80EYgIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIkn7vVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60E2(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,$39<^Type<Atom<$24|Not($25)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...})
	private static boolean typeof_D6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zl7uoUkXzGwUB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_B6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zGoUB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Void
	private static boolean typeof_3GLxLPZCWDggY9w3x$(int index, Automaton automaton) {
		return typeof_2GLxLPZCWDggIk2(automaton.get(index),automaton);
	}

	// And(^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_E6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_C6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_C6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(int index, Automaton automaton) {
		return typeof_B6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(automaton.get(index),automaton);
	}

	// {^$4<Any>,$40<^Type<Atom<Not(^$39<Proton<$4|Void|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_B6o3ZQZSd87rmQ1TNd6Kw8ocA5Y62CYK2S2T2aYj2xYCFX7em2im5GdZPO8PRuCuJ8lNZaRX_b10f3YCmXcVcvhq5OAhPWAXguho7Q1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_3C0tLTIc2Avc1EGE(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(automaton.get(index),automaton);
	}

	// $9<Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^$9>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(Automaton.State state, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(state,automaton);
	}

	// $6<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^Type<$6>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(Automaton.State state, Automaton automaton) {
		return typeof_7GIFi_r3ukmDosoQodHTYoYEgwI77_r58GJFoxLQYgZHhk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t5z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(automaton.get(index),automaton);
	}

	// $60<{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^$60>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|$65|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Or($61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$63|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Nominal(^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|$56|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal($54)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(automaton.get(index),automaton);
	}

	// $53<[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^$53)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNdbF2iLBRBmBJCmCqCLDwDmEqs6Xc9Es9QZZFe4_cldrcI43YGNe8LR9ZbFBq$AX1XirPQ3hme_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// And(^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...})
	private static boolean typeof_E6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzl7usUkmzGVcB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_C6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_C6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkUcHzGsUB(int index, Automaton automaton) {
		return typeof_B6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(automaton.get(index),automaton);
	}

	// {^$4<Void>,$40<^Type<Atom<Not(^$39<Proton<$4|Any|Bool|Int|Real|String>>)|$39>|Not($40)|Ref($40)|Meta($40)|Nominal(^[^string,$40])|Or($62<^{$40...}>)|And($62)|Set(^[$68<^bool>,$62])|Bag(^[$68,^{|$40...|}])|List(^[$68,^[$40...]])>>...}
	private static boolean typeof_B6o3ZQZS8merHYg8sW_TJlbFYiNBTBoBLCoCsCNDyDoEq$8Xc9Es9Q3_He__cldrcI_3YGPm8LT93gHBq7AX1XirPQZhoe4ic87rmQ1jNtAX3uhrNQXoNGCPHmCuaVCLPY7d9ABqasCLkYgdcIioRvg8oY_x1n6oLnhwsQYy729tTYirRQZzwf432KNPZaJBNOlV5d9ngGPBJBNllVca9Yik50nFlV_B2uRuhqHnhkNvcH5YoOcNB1bWbGDU5eVPBmaGe0Ae5ZwZRGcNBmDh5jsPcmfl7g5s5gZQBXDs5ggQFnJ_Gb9RBXj0il7R5z5tVRcHml7x596ggRFYFWSa395YgYn0DE6eVSBXjGql7R5P6tgSc1rl7C6S6gsSF3IdC6SIcNBmDd6tcTcmul7y5g6YkOoTZYvW9j6YVTVUkHyG7zcNBmDv6jkU3x6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_3GLxLPZCWDggY9w3x$(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(automaton.get(index),automaton);
	}

	// $9<Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39<^$9>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3AC(Automaton.State state, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(state,automaton);
	}

	// $6<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39<^Type<$6>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU37C(Automaton.State state, Automaton automaton) {
		return typeof_7GZKj_5OIc2AvFYIjGb9tCHEykmEWVJTQZ3G0GrQhCmH9p1Io3ZQtCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t5z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(state,automaton)
			|| typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j6z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3f6z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3T6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3R6z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3Q6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3N6z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3E6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A6z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU386z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU376z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(automaton.get(index),automaton);
	}

	// $60<{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^$60>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3i5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($61<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|$65|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3v5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Or($61<^{$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$63|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3t5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3j5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Nominal(^[^string,$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|$56|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3e5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<^[^string,$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal($54)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3c5z(int index, Automaton automaton) {
		return typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(automaton.get(index),automaton);
	}

	// $53<[^string,$39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^$53)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3U5z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3O5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3D5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<Not(^$38<Proton<Void|Any|Bool|Int|Real|String>>)|$38>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU3A5z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS8merHYg8sW_T1CsPlkl7nFp7rNrNtkv7zNYU7XewVfgGH1dLJX5bTYDKFB5WdaNldFT1lN2g797uTfhGo9fLqXaVRAj8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABUyHhqkQjCsYBcZvCuTnhGwfjLyI__5Wd32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqDeZl7R585YgOoOZYbW9U5YcOVPkHe08E8M385Yg2flAh5esPBmelf0As5Ycnf0Au5oBKOod16YoQwPBmaWjGDz5eVRB1jGm0A96o7JNb8GXl7voRVYnW9E6YoQVSBmaGqGDP6egSB1n0r0AS63lJPnGr385Yg2uGDd6ecTBHjlul7Q5g6toTcXvl7U6j6gVU385z(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Any
	private static boolean typeof_3C0tLTIc2Avc1EGE(int index, Automaton automaton) {
		return typeof_2C0tLTIc2Av3w$(automaton.get(index),automaton);
	}

	// Set(^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}])
	private static boolean typeof_Q6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZcBXD99ggc3B9z(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_O6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZc399z(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}]
	private static boolean typeof_O6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79eZc399z(int index, Automaton automaton) {
		return typeof_N6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79GXm(automaton.get(index),automaton);
	}

	// $22<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$22)
	private static boolean typeof_xFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmPWV5QQoLsq2(Automaton.State state, Automaton automaton) {
		return typeof_jFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(state,automaton)
			|| typeof_vFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmP0QE(state,automaton);
	}

	// Bool
	private static boolean typeof_2G1xqQgCWDggIk2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bool) {
			return true;
		}
		return false;
	}

	// {$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>}
	private static boolean typeof_96ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vgPwXyGgUB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6WEz(automaton.get(index),automaton);
	}

	// $4<Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^$4>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6WEz(Automaton.State state, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lDz(state,automaton);
	}

	// $1<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37<^Type<$1>>)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lDz(Automaton.State state, Automaton automaton) {
		return typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6GXl(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60Yl(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lYl(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60el(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60il(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lil(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lml(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wrl(state,automaton)
			|| typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gyl(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$37...|}])|$95>>...]])>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gyl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lvl(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$37...|}])|List($93)>>...]]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lvl(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wvl(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$37...|}])|List(^$92)>>...]]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wvl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lul(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lul(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wul(automaton.get(index),automaton);
	}

	// $88<[$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wul(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67,$60])|$84|List(^[$67,^[$37...]])>>...|}])>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wrl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60rl(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67,$60])|Bag($82)|List(^[$67,^[$37...]])>>...|}]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60rl(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lql(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67,$60])|Bag(^$81)|List(^[$67,^[$37...]])>>...|}]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lql(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60ql(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,$78])|List(^[$67,^[$37...]])>>...|}>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60ql(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lnl(automaton.get(index),automaton);
	}

	// $77<{|$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^$77])|List(^[$67,^[$37...]])>>...|}>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lnl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$60<^{$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60)|And($60)|$73|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>...}>])>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lml(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gml(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$60<^{$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60)|And($60)|Set($71)|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>...}>]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gml(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60ml(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$60<^{$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60)|And($60)|Set(^$70)|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>...}>]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60ml(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wfl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^{$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>...}>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wfl(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gfl(automaton.get(index),automaton);
	}

	// $59<{$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^$59>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>...}>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gfl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($60<^{$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60)|$65|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>...}>)>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lil(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wfl(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Or($60<^{$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^[^string,$37])|$62|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>...}>)>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60il(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wfl(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<Nominal(^[^string,$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|$54|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>])>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60el(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wbl(data,automaton)) { return true; }
		}
		return false;
	}

	// $52<^[^string,$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal($52)|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Wbl(int index, Automaton automaton) {
		return typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gbl(automaton.get(index),automaton);
	}

	// $51<[^string,$37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|Meta($37)|Nominal(^$51)|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>]>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6Gbl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $45<Meta($37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|Ref($37)|$45|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>)>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lYl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(data,automaton)) { return true; }
		}
		return false;
	}

	// $42<Ref($37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|Not($37)|$42|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>)>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As60Yl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<Not($37<^Type<Atom<Not(^$36<Proton<Any|Void|Bool|Int|Real|String>>)|$36>|$39|Ref($37)|Meta($37)|Nominal(^[^string,$37])|Or($60<^{$37...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$37...|}])|List(^[$67,^[$37...]])>>)>
	private static boolean typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6GXl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76W5E_NkNwNVPVQgQgRsSZUgWD3GKTkKq7wgIAxFYIjGb9yCmE7lHHWcZUQg3G0GrQhCHIBpXIo3ZQtC0LgZ4GLxLPZClLgk4G1xqQgCWMgw4C8t5SIZ5AeGZJ_45QIk5AhGqJo8MPiSq3jl1TWdKMchaQthWTZ0_RjGrQiC1UvpHUesn7zw6A75o7KOaCXXlUggNFJI_GMNYoNw6AD5otoQh_aQWlqG_B5GnsjGmBqBmWnF_DYGkBwBNtnVZbZv5lUYcPgPw1fW9h5YZPsPkmf0C0t5OYZQsPkXi0CIK5Sekl7x5i5YZOsQZnjW976YkQZRkXm0C14qOHwr7e5C6ssRcmnl7x5N6YZOZSZYqW9Q6YkRkSkHr0GB_qRoClUYcPVTZIuW9e6YoQgTBHa0vGDh6esTBmrlv0As6lUz(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,$39<^Type<Atom<$24|Not($25)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}
	private static boolean typeof_B6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgUc1zGoUB(int index, Automaton automaton) {
		return typeof_A6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgU3w6z(automaton.get(index),automaton);
	}

	// {$25<^$24<Proton<Any|Void|Bool|Int|Real|String>>>,$39<^Type<Atom<$24|Not($25)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}
	private static boolean typeof_A6o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yG7f_NBmDu6jgU3w6z(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_sFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PGPE(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVrE(automaton.get(index),automaton);
	}

	// $30<Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39<^$30>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVrE(Automaton.State state, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGoqE(state,automaton);
	}

	// $27<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39<^Type<$27>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGoqE(Automaton.State state, Automaton automaton) {
		return typeof_7GIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAUd1PosoQoCXPdlmPWV5QQo5G0GrQhCmQip1TGTE(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgNB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsNB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZOB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcPB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZQB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgQB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgRB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsSB(state,automaton)
			|| typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZUB(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZUB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwTB(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwTB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsTB(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgTB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgTB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcTB(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsSB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGkSB(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGkSB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgSB(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgSB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVSB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVSB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwRB(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgRB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZRB(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZRB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVRB(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsPB(automaton.get(index),automaton);
	}

	// $60<{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^$60>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsPB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|$65|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Or($61<^{$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$63|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $56<Nominal(^[^string,$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|$56|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGcPB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $54<^[^string,$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal($54)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGVPB(int index, Automaton automaton) {
		return typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwOB(automaton.get(index),automaton);
	}

	// $53<[^string,$39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|Meta($39)|Nominal(^$53)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGwOB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZOB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGsNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<$24<Proton<Any|Void|Bool|Int|Real|String>>|Not(^$24)>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGgNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZSd87rmQ1TNd6Xguho7QXUN08X3uhrNQ1_NO8PHmCuac8LRX7d9ABqa$ALmXgdcIioRvg8qX4i1nbZNu8huP2j729tTYirRQ3oye_oJGhF23Y426YB2KYRYSY_2jYwYCNY7em2im5lhqPtCPRuCu58jpNGjFJQjCsYcVcvhq5ljyPtEKkQZwHBNGlV5d9ngGNBHBNdlVca9Yik5tl7lV4B2uRuhqHnhkNvcH5YkOZNBmaGbGDT5ewOBXa0e0Ad5ZwZRGZNBmDg5joPcXfl7f5j5gVQBXDj5gcQFnJ_Gb9RBHjlfl7Q5y5twQc1ml7w586gcRFYFWSa385YgIn0DD6ewRBHj0ql7Q5O6tcScmql7B6R6goSF3IdC6SIZNBmDc6tZTcXul7x5f6YgOkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^And(^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>})
	private static boolean typeof_D6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v6Yc2z0Ax6esU3z62(int index, Automaton automaton) {
		return typeof_C6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v6Yc2z0Ax6WzlE(automaton.get(index),automaton);
	}

	// And(^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>})
	private static boolean typeof_C6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v6Yc2z0Ax6WzlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_A6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v60zlE(data,automaton)) { return true; }
		}
		return false;
	}

	// ^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>}
	private static boolean typeof_A6o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyW9v60zlE(int index, Automaton automaton) {
		return typeof_96o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyGgUB(automaton.get(index),automaton);
	}

	// {$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>}
	private static boolean typeof_96o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yl7vkPwXyGgUB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVoE(automaton.get(index),automaton);
	}

	// $6<Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^$6>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVoE(Automaton.State state, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(state,automaton);
	}

	// $3<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^Type<$3>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(Automaton.State state, Automaton automaton) {
		return typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgNB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsNB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZOB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZPB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZQB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgQB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgRB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsSB(state,automaton)
			|| typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZUB(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZUB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwTB(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwTB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsTB(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgTB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgTB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcTB(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsSB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGkSB(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGkSB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgSB(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67,$61])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgSB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVSB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVSB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwRB(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgRB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZRB(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZRB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVRB(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwPB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwPB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(automaton.get(index),automaton);
	}

	// $60<{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^$60>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61)|$65|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $63<Or($61<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$63|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Nominal(^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|$55|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZPB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwOB(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal($53)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwOB(int index, Automaton automaton) {
		return typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsOB(automaton.get(index),automaton);
	}

	// $52<[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^$52)|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsOB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZOB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($61<^{$39...}>)|And($61)|Set(^[$67<^bool>,$61])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o3ZQZS1CmPlkl7n7p7rNrNtkv7zN2T7XewVfgGwdbLyW5bTYDKFB588_NOdFN1lNYa797uTfhGTmdLkXaVRAj8oX_h7merHYg8uX4j7HcrTYh8FY_oc88rcQXpNWCXZegkNQ1rNtCmaXDtHnhna7ELoABiyHhqkQjCsYBcZvCuTnhGwfjLyI4Z58d32LJBcGemBQ35Y32LPB7edmcAgGTBHBN0nVDbTfhoRABqJ8YGqBHB5O6DYRuBJlnN3CYE2LFC7t8tiGZNBme0flAh5esPBXelf0As5Ycnf0Au5oBKOod16YoQwPBmDy5twQc1ml7w586gcRFYFWSa385YgPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBme0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^$9<And($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>)
	private static boolean typeof_B6osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(int index, Automaton automaton) {
		return typeof_A6osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyGkUw2(automaton.get(index),automaton);
	}

	// Not(^$9<And($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>)
	private static boolean typeof_A6osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyGkUw2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_86osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6egJcUw2(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$9<And($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_86osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6egJcUw2(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lH2(automaton.get(index),automaton);
	}

	// $9<And($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lH2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(data,automaton)) { return true; }
		}
		return false;
	}

	// $7<^{$3<^Type<And($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(automaton.get(index),automaton);
	}

	// $6<{$3<^Type<And($7<^$6>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60L2(automaton.get(index),automaton);
	}

	// $14<Type<And($7<^{$3<^$14>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60L2(Automaton.State state, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GI2(state,automaton);
	}

	// $11<And($7<^{$3<^Type<$11>>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GI2(Automaton.State state, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lH2(state,automaton)
			|| typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(state,automaton)
			|| typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67,$7])|Bag(^[$67,^{|$3...|}])|$95>>...]])>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67,$7])|Bag(^[$67,^{|$3...|}])|List($93)>>...]]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67,$7])|Bag(^[$67,^{|$3...|}])|List(^$92)>>...]]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(automaton.get(index),automaton);
	}

	// $88<[$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67,$7])|$84|List(^[$67,^[$3...]])>>...|}])>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67,$7])|Bag($82)|List(^[$67,^[$3...]])>>...|}]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67,$7])|Bag(^$81)|List(^[$67,^[$3...]])>>...|}]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,$78])|List(^[$67,^[$3...]])>>...|}>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(automaton.get(index),automaton);
	}

	// $77<{|$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^$77])|List(^[$67,^[$3...]])>>...|}>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$7<^{$3<^Type<And($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|$73|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>])>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$7<^{$3<^Type<And($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set($71)|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$7<^{$3<^Type<And($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^$70)|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<Or($7<^{$3<^Type<And($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|$65|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|$62|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>])>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal($60)|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(int index, Automaton automaton) {
		return typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(automaton.get(index),automaton);
	}

	// $59<[^string,$3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^$59)|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>]>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $53<Meta($3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|$53|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<Ref($3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|$50|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<Not($3<^Type<And($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|$47|Ref($3)|Meta($3)|Nominal(^[^string,$3])|Or($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGIFiGqK4gXcbGslbMyG4Z5ObZNGdFY_FAnVnsn$rNrNtkv7zN2a7XewVfgGRedLTH_6YGo8ALo9ZhTBqVAX1XirPQ3jue_jc87rmQXoNGCXguho7Q1qNdCX3uhrNQXrN0EPHmCuaFELqY7d9ABqacELwYgdcIioRvg8FBN8l7gpRAEyyIlNY42mVHircvCr5dlVlc46YGod6LkBcGemBQ3CYTNOnVca9Yik5dnc6LwBcndrPACr1YhY9OBXeGEYZPgPZ2fW9h5YVPsPkmf08E8r7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Not(^$9<Or($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>)
	private static boolean typeof_B6osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyW9w6Gzl(int index, Automaton automaton) {
		return typeof_A6osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyGkUw2(automaton.get(index),automaton);
	}

	// Not(^$9<Or($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>)
	private static boolean typeof_A6osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6ego7ucUkmyGkUw2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_86osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6egJcUw2(data,automaton)) { return true; }
		}
		return false;
	}

	// ^$9<Or($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_86osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6egJcUw2(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lH2(automaton.get(index),automaton);
	}

	// $9<Or($7<^{$3<^Type<$9|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lH2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(data,automaton)) { return true; }
		}
		return false;
	}

	// $7<^{$3<^Type<Or($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(automaton.get(index),automaton);
	}

	// $6<{$3<^Type<Or($7<^$6>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60H2(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60L2(automaton.get(index),automaton);
	}

	// $14<Type<Or($7<^{$3<^$14>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60L2(Automaton.State state, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GI2(state,automaton);
	}

	// $11<Or($7<^{$3<^Type<$11>>...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GI2(Automaton.State state, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lH2(state,automaton)
			|| typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(state,automaton)
			|| typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67,$7])|Bag(^[$67,^{|$3...|}])|$95>>...]])>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GylE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67,$7])|Bag(^[$67,^{|$3...|}])|List($93)>>...]]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lvlE(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67,$7])|Bag(^[$67,^{|$3...|}])|List(^$92)>>...]]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WvlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lulE(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(automaton.get(index),automaton);
	}

	// $88<[$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WulE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67,$7])|$84|List(^[$67,^[$3...]])>>...|}])>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WrlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67,$7])|Bag($82)|List(^[$67,^[$3...]])>>...|}]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60rlE(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67,$7])|Bag(^$81)|List(^[$67,^[$3...]])>>...|}]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lqlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,$78])|List(^[$67,^[$3...]])>>...|}>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60qlE(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(automaton.get(index),automaton);
	}

	// $77<{|$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^$77])|List(^[$67,^[$3...]])>>...|}>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lnlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$7<^{$3<^Type<Or($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|$73|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>])>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lmlE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$7<^{$3<^Type<Or($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set($71)|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GmlE(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$7<^{$3<^Type<Or($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^$70)|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60mlE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($7<^{$3<^Type<Or($7)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^[^string,$3])|$65|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>...}>)>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GH2(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Nominal(^[^string,$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|$62|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>])>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60ilE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(data,automaton)) { return true; }
		}
		return false;
	}

	// $60<^[^string,$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal($60)|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6WflE(int index, Automaton automaton) {
		return typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(automaton.get(index),automaton);
	}

	// $59<[^string,$3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|Meta($3)|Nominal(^$59)|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>]>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GflE(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $53<Meta($3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|Ref($3)|$53|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6lblE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(data,automaton)) { return true; }
		}
		return false;
	}

	// $50<Ref($3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($3)|$50|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As60blE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(data,automaton)) { return true; }
		}
		return false;
	}

	// $47<Not($3<^Type<Or($7<^{$3...}>)|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|$47|Ref($3)|Meta($3)|Nominal(^[^string,$3])|And($7)|Set(^[$67<^bool>,$7])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>)>
	private static boolean typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GalE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76osoQoGmImSK0f7uO3Twt_UJ0dGq88LJ9oN8omBsByBkCqCLDwDmEqV8Xc9Es9Q3bPe_bJtlN3SmX4h1Of6YCsXcVcvhq5lfiPtAP1mhwa7CLJY7fTACmaVCLPY7WTvCqasCLkYaZRYi8oY_x7Hem1Yh8uY4z7Q9uZACrDQ132LHB1QCrmYjzJBqNlVBcZvCuTnhGPBNBPllN3SuW4B2PZeBn5Gnc6LqB7edmcAgGuBuW4E2uRuhqHnhkNvcH5YcPon7d5f5tkPcHfl7c5i5gwPFIFiGq7t58lXi0CIK5Sekl7x58CHeWjGDz5eVRB1jGm0A96o7JNb8GEYkIn0DD6ewRBHj0ql7d5O6tcScmql7B6R6goSF3IdC6SIon7wVTZIuW9e6YoQgTBHe0vGDh6esTBmrlv0As6GE2(data,automaton)) { return true; }
		}
		return false;
	}

	// ^[$2<^bool>,^{|^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...|}]
	private static boolean typeof_O6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79eZc399z(int index, Automaton automaton) {
		return typeof_N6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79GXm(automaton.get(index),automaton);
	}

	// [$2<^bool>,^{|^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...|}]
	private static boolean typeof_N6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoUB1Ey6YgnzGD79GXm(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_C6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoU3y6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{|^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2<^bool>,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...|}
	private static boolean typeof_C6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6eoU3y6z(int index, Automaton automaton) {
		return typeof_B6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6Gzl(automaton.get(index),automaton);
	}

	// {|^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2<^bool>,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...|}
	private static boolean typeof_B6o7JNbSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWjGnlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHj0CIK5SYkIil7vVRZImW996YwQgRk1nW3B5YoYn0DE6eVSB1EO6YgYqGDQ6ekSBXDS6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU7HHB5Yony0Dw6Gzl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_3GLxLPZCWDggY9w3x$(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GIz(automaton.get(index),automaton);
	}

	// $11<Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41<^$11>)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GIz(Automaton.State state, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6WHz(state,automaton);
	}

	// $8<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41<^Type<$8>>)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6WHz(Automaton.State state, Automaton automaton) {
		return typeof_7GZKj_5OIc2AvFYIjGb9tCHEykmEWVJTQZ3G0GrQhCmH9p1Io3ZQtCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GYl(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60al(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lal(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lel(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wil(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gjl(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60nl(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wrl(state,automaton)
			|| typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gyl(state,automaton);
	}

	// $95<List(^[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|$95>>...]])>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gyl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lvl(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|List($93)>>...]]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lvl(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wvl(automaton.get(index),automaton);
	}

	// $92<[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|List(^$92)>>...]]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wvl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lul(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,$89])>>...]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lul(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wul(automaton.get(index),automaton);
	}

	// $88<[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^$88])>>...]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wul(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|$84|List(^[$2,^[$41...]])>>...|}])>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wrl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60rl(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag($82)|List(^[$2,^[$41...]])>>...|}]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60rl(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lql(automaton.get(index),automaton);
	}

	// $81<[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^$81)|List(^[$2,^[$41...]])>>...|}]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lql(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60ql(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,$78])|List(^[$2,^[$41...]])>>...|}>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60ql(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lnl(automaton.get(index),automaton);
	}

	// $77<{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^$77])|List(^[$2,^[$41...]])>>...|}>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lnl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $74<Set(^[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|$74|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>])>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60nl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wml(data,automaton)) { return true; }
		}
		return false;
	}

	// $72<^[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set($72)|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wml(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gml(automaton.get(index),automaton);
	}

	// $71<[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set(^$71)|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gml(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lfl(automaton.get(index),automaton);
	}

	// $61<{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^$61>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lfl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<And($62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|$67|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>)>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gjl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<Or($62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|$64|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>)>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wil(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(data,automaton)) { return true; }
		}
		return false;
	}

	// $57<Nominal(^[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|$57|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>])>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lel(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gel(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<^[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal($55)|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gel(int index, Automaton automaton) {
		return typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60el(automaton.get(index),automaton);
	}

	// $54<[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^$54)|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>]>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60el(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $49<Meta($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|$49|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lal(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Ref($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|$46|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60al(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Not($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|$43|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GYl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76o7JNbSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5B6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQFnJ_Gr7wVQBmDz5tVRcHml7y596ggR7lXl7xoRVYnW9E6Yk2ql7vZSZYqW9Q6Yc2r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// [$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}]
	private static boolean typeof_N63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79GXm(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_C63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoU3y6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2<^bool>,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}
	private static boolean typeof_C63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6eoU3y6z(int index, Automaton automaton) {
		return typeof_B63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6Gzl(automaton.get(index),automaton);
	}

	// {^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2<^bool>,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}
	private static boolean typeof_B63lJPnGrG4Di3OpQdGq3ykmEeVJclMD5O5R5g5v5y5C6c6u6Qc3GJ_6R_C1IApHIosoQodmXl7EW4AO4XLA5Qg4G0GrQhCHMRpXMo3ZQtC0PgZ5G1xqQgClPgk5C8t5SIs5AjGZJ_45QIZ6AuGqJo8MPiSq3wlHUWWZPhWrTyhlUZ0_RjGrQiCHX0XGA95eso7ElNkHY0CHKaOYwNkNk1a0GCK5SWCXa0Y0AQ5otoQh_aQWla9OBXb0Yl7vwOZ2eW9d5YoOcPkme08E8M3B5YoYflAj5eVQBHfGi0Au5o3ZQZC1jGi0Ax5oBKOoC1Et5Yg2mGD86ecRBmjlm0AB6o7JNb80Yl7xwRV2qW9O6YkYql7vgSZ2rW9S6YsRsSkmrl3B5YoIuGDe6egTB1Eg6YgIvGDi6ewTBXDs6gZU3HHB5YonylAw6Gzl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_3GLxLPZCWDggY9w3x$(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6GIz(automaton.get(index),automaton);
	}

	// $11<Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41<^$11>)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6GIz(Automaton.State state, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6WHz(state,automaton);
	}

	// $8<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41<^Type<$8>>)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6WHz(Automaton.State state, Automaton automaton) {
		return typeof_7GZKj_5OIc2AvFYIjGb9tCHEykmEWVJTQZ3G0GrQhCmH9p1Io3ZQtCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6GYl(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60al(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lal(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lel(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wil(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gjl(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60nl(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lrl(state,automaton)
			|| typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gyl(state,automaton);
	}

	// $95<List(^[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|$95>>...]])>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gyl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lvl(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|List($93)>>...]]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lvl(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wvl(automaton.get(index),automaton);
	}

	// $92<[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|List(^$92)>>...]]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wvl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lul(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,$89])>>...]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lul(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wul(automaton.get(index),automaton);
	}

	// $88<[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^$88])>>...]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wul(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $85<Bag(^[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|$85|List(^[$2,^[$41...]])>>...|}])>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lrl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Grl(data,automaton)) { return true; }
		}
		return false;
	}

	// $83<^[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag($83)|List(^[$2,^[$41...]])>>...|}]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Grl(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60rl(automaton.get(index),automaton);
	}

	// $82<[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^$82)|List(^[$2,^[$41...]])>>...|}]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60rl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gql(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $79<^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,$79])|List(^[$2,^[$41...]])>>...|}>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gql(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60ql(automaton.get(index),automaton);
	}

	// $78<{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^$78])|List(^[$2,^[$41...]])>>...|}>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60ql(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $74<Set(^[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|$74|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>])>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60nl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wml(data,automaton)) { return true; }
		}
		return false;
	}

	// $72<^[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set($72)|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wml(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gml(automaton.get(index),automaton);
	}

	// $71<[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set(^$71)|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gml(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60il(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60il(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lfl(automaton.get(index),automaton);
	}

	// $61<{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^$61>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lfl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<And($62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|$67|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>)>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gjl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60il(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<Or($62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|$64|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>)>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Wil(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60il(data,automaton)) { return true; }
		}
		return false;
	}

	// $57<Nominal(^[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|$57|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>])>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lel(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gel(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<^[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal($55)|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6Gel(int index, Automaton automaton) {
		return typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60el(automaton.get(index),automaton);
	}

	// $54<[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^$54)|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>]>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60el(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $49<Meta($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|$49|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lal(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Ref($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|$46|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As60al(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Not($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|$43|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6GYl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_763lJPnGrG4Di3OpQdGq3ykmEW5TpNVOgOgPcQoQkRwSZUgGH3GKTkKq7AdJABGYIjGb995YsoIgVK7OdNgWL343Sjpq7RhKASGIFi_r3Ul1P38oQjlq3elmPoZZQoCGQgs5GHKLNgC0TgZ6OIGbRdtqOIg6Aw45HdlqQupr5yG5Jmx5Sjtq775zpHXW9CCXIA5gkNFYJ_Oq7D5A5gwNFJI_GMNYZOgNkXa0SDxLQdtLNgdH5YoOgNBmDT5twOc1el7R5d5gcPFmIm4lXl7xoPwXfW9j5YkPVQkHi0C0t5OYgQVQk1j0CIK5SYk2il7vwQZ2mW986YsQcRkmm0C14qOHgNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSBlXl7xVTZIuW9e6Yknul7vkTZIvW9i6Ycnv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// [$2<^bool>,^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}]
	private static boolean typeof_N6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoUB1Ey6YgnzGD79GXm(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_C6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoU3y6z(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// ^{^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2<^bool>,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}
	private static boolean typeof_C6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6eoU3y6z(int index, Automaton automaton) {
		return typeof_B6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6Gzl(automaton.get(index),automaton);
	}

	// {^$6<Void>,$42<^Type<Atom<Not(^$41<Proton<$6|Any|Bool|Int|Real|String>>)|$41>|Not($42)|Ref($42)|Meta($42)|Nominal(^[^string,$42])|Or($63<^{$42...}>)|And($63)|Set(^[$2<^bool>,$63])|Bag(^[$2,^{|$42...|}])|List(^[$2,^[$42...]])>>...}
	private static boolean typeof_B6oBKOoSJWYAGLxLPZCWEgwY974Pw_YGa0b0fliWj0nlrWyl59G3Kt0MOYkoHho3CDx5SegNBmINlHLWcpXl5QGJFoxLQYo4Mhs4C0tLTIV5AdGZFjx5QIg5AgGIHiGr3ilmQ38KOWlq3tlXTZC4Sm_aQbC0UgoMN7dLQshbUQw6OF8rQoxaQYZNVNoXXW9DCmIB5goNFYJ_Oq7E5B5gVOFJI_GMNYcOkNkma0SDxLQdtLNgdH5YsOkNBmDU5tVPcHel7S5e5ggPFmIm40Yl7xsPwmfW9s5YoPZQkXi0C0t5OYkQZQkHjl7wZQBmDz5tVRcHml7ucRkmm0C14qOHkNBHED6swRc1ql7wZSBmDP6tgSc1rl7C6S6gsSF3IdC6SIkNBHEd6tcTcmul7wkTBmDh6tsTcmvl7c6s6gZU3HHB5YonylAw6Gzl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 1) { return false; }
			for(int s0=0;s0 < state.size();++s0) {
				boolean result=true;
				for(int i=0;i!=state.size();++i) {
					int child = state.get(i);
					if(i == s0) {
						if(!typeof_3GLxLPZCWDggY9w3x$(child,automaton)) { result=false; break; }
					}
					else {
						if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
					}
				}
				if(result) { return true; } // found match
			}
		}
		return false;
	}

	// $41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GIz(automaton.get(index),automaton);
	}

	// $11<Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41<^$11>)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GIz(Automaton.State state, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6WHz(state,automaton);
	}

	// $8<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41<^Type<$8>>)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6WHz(Automaton.State state, Automaton automaton) {
		return typeof_7GZKj_5OIc2AvFYIjGb9tCHEykmEWVJTQZ3G0GrQhCmH9p1Io3ZQtCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWl2LQtKPghGQZ0_RjGrQiCmQip1TGIE(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GYl(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60al(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lal(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lel(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wil(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gjl(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lml(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wrl(state,automaton)
			|| typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gyl(state,automaton);
	}

	// $95<List(^[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|$95>>...]])>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gyl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lvl(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|List($93)>>...]]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lvl(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wvl(automaton.get(index),automaton);
	}

	// $92<[$2<^bool>,^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^[$2,^{|$41...|}])|List(^$92)>>...]]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wvl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lul(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,$89])>>...]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lul(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wul(automaton.get(index),automaton);
	}

	// $88<[$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^$88])>>...]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wul(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|$84|List(^[$2,^[$41...]])>>...|}])>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wrl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60rl(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag($82)|List(^[$2,^[$41...]])>>...|}]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60rl(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lql(automaton.get(index),automaton);
	}

	// $81<[$2<^bool>,^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2,$62])|Bag(^$81)|List(^[$2,^[$41...]])>>...|}]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lql(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60ql(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,$78])|List(^[$2,^[$41...]])>>...|}>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60ql(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lnl(automaton.get(index),automaton);
	}

	// $77<{|$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^$77])|List(^[$2,^[$41...]])>>...|}>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lnl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|$73|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>])>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lml(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gml(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set($71)|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gml(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60ml(automaton.get(index),automaton);
	}

	// $70<[$2<^bool>,$62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set(^$70)|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60ml(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lfl(automaton.get(index),automaton);
	}

	// $61<{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^$61>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lfl(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $67<And($62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62)|$67|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>)>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gjl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(data,automaton)) { return true; }
		}
		return false;
	}

	// $64<Or($62<^{$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^[^string,$41])|$64|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>...}>)>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Wil(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60il(data,automaton)) { return true; }
		}
		return false;
	}

	// $57<Nominal(^[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|$57|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>])>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lel(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gel(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<^[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal($55)|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6Gel(int index, Automaton automaton) {
		return typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60el(automaton.get(index),automaton);
	}

	// $54<[^string,$41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|Meta($41)|Nominal(^$54)|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>]>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60el(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $49<Meta($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|Ref($41)|$49|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lal(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// $46<Ref($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|Not($41)|$46|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As60al(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// $43<Not($41<^Type<Atom<Not(^$40<Proton<Void|Any|Bool|Int|Real|String>>)|$40>|$43|Ref($41)|Meta($41)|Nominal(^[^string,$41])|Or($62<^{$41...}>)|And($62)|Set(^[$2<^bool>,$62])|Bag(^[$2,^{|$41...|}])|List(^[$2,^[$41...]])>>)>
	private static boolean typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6GYl(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76oBKOoSJWYAGLxLPZCWEgwIcWMC5N5Q5f5u5x5A6T6t6QZ3GJ_6R_CmH9p1IosoQodXXl7Dx3AN4HL95Qc4G0GrQhC1MQpHMo3ZQtClMgV5G1xqQgCWPgg5C8t5SIo5AiGZJ_45QIV6AtGqJo8MPiSq3vl1UWWJPgxaTxhWUZ0_RjGrQiC1XlUhZNcHIYsoX0AB5o7KOaCXYlX0AE53pJOo4q7O5A5gcOFbIjpLPi45QeZl7S5A5YgYbGDU5eVPB1bGe0Ae5ZwZRGgNBHEh5jsPcmfl7g5s5gZQFIFiGq7v5s5gkQB1Es5YgYjGDz5eVRBXD86gcRFYFWSa3A5YoIn0DD6ewRB1EN6YgIqGDP6egSB1n0r0AS63lJPnGr3A5Yo2uGDd6ecTB1Ef6Yg2vGDh6esTBmrlv0As6lXl(data,automaton)) { return true; }
		}
		return false;
	}

	// $39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVoE(automaton.get(index),automaton);
	}

	// $6<Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^$6>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVoE(Automaton.State state, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(state,automaton);
	}

	// $3<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39<^Type<$3>>)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGonE(Automaton.State state, Automaton automaton) {
		return typeof_7GYIjGb9tCXDvk1EWoITQs2G0GrQhC1HzoHHo3ZQtClHgk3GLxLPZCWIgw3G1xqQgCGLgc4C8t5SIk4ASGZJ_45QIw4AcGqJo8MPiSq3elmPWp3LQtKPghGQZ0_RjGrQiCmQip1TWHE(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgNB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsNB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZOB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZPB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVQB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgQB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgRB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsSB(state,automaton)
			|| typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZUB(state,automaton);
	}

	// $95<List(^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|$95>>...]])>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZUB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_List) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwTB(data,automaton)) { return true; }
		}
		return false;
	}

	// $93<^[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|List($93)>>...]]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwTB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsTB(automaton.get(index),automaton);
	}

	// $92<[$67<^bool>,^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^[$67,^{|$39...|}])|List(^$92)>>...]]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgTB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $89<^[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,$89])>>...]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgTB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcTB(automaton.get(index),automaton);
	}

	// $88<[$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^$88])>>...]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGcTB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			int s0 = 0;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $84<Bag(^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|$84|List(^[$67,^[$39...]])>>...|}])>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsSB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Bag) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGkSB(data,automaton)) { return true; }
		}
		return false;
	}

	// $82<^[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag($82)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGkSB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgSB(automaton.get(index),automaton);
	}

	// $81<[$67<^bool>,^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67,$60])|Bag(^$81)|List(^[$67,^[$39...]])>>...|}]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgSB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVSB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $78<^{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,$78])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVSB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwRB(automaton.get(index),automaton);
	}

	// $77<{|$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^$77])|List(^[$67,^[$39...]])>>...|}>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $73<Set(^[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|$73|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>])>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgRB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Set) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZRB(data,automaton)) { return true; }
		}
		return false;
	}

	// $71<^[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set($71)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZRB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVRB(automaton.get(index),automaton);
	}

	// $70<[$67<^bool>,$60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set(^$70)|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVRB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9R3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGoPB(automaton.get(index),automaton);
	}

	// $59<{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^$59>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGoPB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() < 0) { return false; }
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				{
					if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $65<And($60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60)|$65|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_And) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $62<Or($60<^{$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^[^string,$39])|$62|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>...}>)>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGVQB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Or) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsPB(data,automaton)) { return true; }
		}
		return false;
	}

	// $55<Nominal(^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|$55|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>])>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZPB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Nominal) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwOB(data,automaton)) { return true; }
		}
		return false;
	}

	// $53<^[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal($53)|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGwOB(int index, Automaton automaton) {
		return typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsOB(automaton.get(index),automaton);
	}

	// $52<[^string,$39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|Meta($39)|Nominal(^$52)|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>]>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsOB(Automaton.State _state, Automaton automaton) {
		if(_state instanceof Automaton.Collection) {
			Automaton.Collection state = (Automaton.Collection) _state;
			if(state.size() != 2) { return false; }
			int s0 = 0;
			int s1 = 1;
			boolean result=true;
			for(int i=0;i!=state.size();++i) {
				int child = state.get(i);
				if(i == s0) {
					if(!typeof_W9O3u$(child,automaton)) { result=false; break; }
				}
				else if(i == s1) {
					if(!typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(child,automaton)) { result=false; break; }
				}
			}
			if(result) { return true; } // found match
		}
		return false;
	}

	// $47<Meta($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|Ref($39)|$47|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZOB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Meta) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $44<Ref($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|Not($39)|$44|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGsNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Ref) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// $41<Not($39<^Type<Atom<Not(^$38<Proton<Any|Void|Bool|Int|Real|String>>)|$38>|$41|Ref($39)|Meta($39)|Nominal(^[^string,$39])|Or($60<^{$39...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$39...|}])|List(^[$67,^[$39...]])>>)>
	private static boolean typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGgNB(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_76ZwZRbFYgLBRBmBHCkCqCLDwDmEqV6Xc9Es9Q3Uud_UcldrcI43YGHH8LL93aFBqc8X1XirPQZbRe4gc87rmQ1hNOAXguho7QXiNlAX3uhrNQ1oN8CPHmCuaNCLNY7d9ABqakCLTYgdcIioRvg8mY4x1YfjJfCwqQ2y729tTYirRQ3zuf_zJ0dGH9lV442PZeBn5Wl7lV_52XPeBu1QZ6Y32LkBcndrPACr1YCKmNZCY3YGqWn7bD2KwB5GnsnV4JYQ385YcPgPw1fW9h5YcYf0Aj5o3ZQZCHiWf0Au5oBKOod16YoQsPBmDy5twQc1ml7w586gcRFYFWSa385YcPoRVYnW9E6YoQVSBmDO6tcScmql7B6R6goSF3IdC6SIZNBXe0uGDd6ecTBHjlul7vkTZIvW9i6YwSwTk1yGZNB(data,automaton)) { return true; }
		}
		return false;
	}

	// Void
	private static boolean typeof_2GLxLPZCWDggIk2(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Void) {
			return true;
		}
		return false;
	}

	// Not(^$28<Proton<Void|Any|Bool|Int|Real|String>>)|$28
	private static boolean typeof_xFZKj_5OIc2AvFYIjGb9iCHEykmEWVZQQZ3C0tLTIg3ABGZFjx5QIs3AEGIHiGr3OlXL38KOWlq3RlHMZC4Sm_aQbClMgVLNwo3LQtKPQc5OF8rQoxaQYkqPhoLco2(Automaton.State state, Automaton automaton) {
		return typeof_vFZKj_5OIc2AvFYIjGb9gCHEykmEo3ZQtCGHgc3G1xqQgC0Igo3C8t5SIw3ANGZJ_45QIc4AQGqJo8MPiSq3SlXMWlnHD_4MUh0PZ0_RjGrQiCXPdpmP0HE(state,automaton)
			|| typeof_jFZKj_5OIc2AvFIFi_r3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(state,automaton);
	}

	// Proton<Void|Any|Bool|Int|Real|String>
	private static boolean typeof_jFZKj_5OIc2AvFIFi_r3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(Automaton.State state, Automaton automaton) {
		return typeof_gFZKj_5OIc2AvFIFi_r3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5R4Sx(state,automaton);
	}

	// Void|Any|Bool|Int|Real|String
	private static boolean typeof_gFZKj_5OIc2AvFIFi_r3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5R4Sx(Automaton.State state, Automaton automaton) {
		return typeof_2GLxLPZCWDggIk2(state,automaton)
			|| typeof_2C0tLTIc2Av3w$(state,automaton)
			|| typeof_2G1xqQgCWDggIk2(state,automaton)
			|| typeof_2C8t5SIc2Av3w$(state,automaton)
			|| typeof_2GHKLNgCWDggIk2(state,automaton)
			|| typeof_2OIGbRdtqOIc2Av3w$(state,automaton);
	}

	// Not(^Proton<Void|Any|Bool|Int|Real|String>)
	private static boolean typeof_vFZKj_5OIc2AvFYIjGb9gCHEykmEo3ZQtCGHgc3G1xqQgC0Igo3C8t5SIw3ANGZJ_45QIc4AQGqJo8MPiSq3SlXMWlnHD_4MUh0PZ0_RjGrQiCXPdpmP0HE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_sFZKj_5OIc2AvcHPo3ZQtCWEgw2G1xqQgCGHgc3C8t5SIk3ACGZJ_45QIw3ANGqJo8MPiSq3PlmLWl2HAtJLRhGMZ0_RjGrQiCmMTp1PGEE(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton<Void|Any|Bool|Int|Real|String>
	private static boolean typeof_sFZKj_5OIc2AvcHPo3ZQtCWEgw2G1xqQgCGHgc3C8t5SIk3ACGZJ_45QIw3ANGqJo8MPiSq3PlmLWl2HAtJLRhGMZ0_RjGrQiCmMTp1PGEE(int index, Automaton automaton) {
		return typeof_jFZKj_5OIc2AvFIFi_r3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(automaton.get(index),automaton);
	}

	// Not(^$28<Proton<Any|Void|Bool|Int|Real|String>>)|$28
	private static boolean typeof_xFIFi_r3ukmDosoQodXQYoYEgwI77tq58GZKj_5OIg3ABGZFjx5QIs3AEGIHiGr3OlXL38KOWlq3RlHMZC4Sm_aQbClMgVLNwo3LQtKPQc5OF8rQoxaQYkqPhoLco2(Automaton.State state, Automaton automaton) {
		return typeof_vFIFi_r3ukmDosoQod1QYoYEgw2GLxLPZCGHgc3G1xqQgC0Igo3C8t5SIw3ANGZJ_45QIc4AQGqJo8MPiSq3SlXMWlnHD_4MUh0PZ0_RjGrQiCXPdpmP0HE(state,automaton)
			|| typeof_jFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(state,automaton);
	}

	// Not(^Proton<Any|Void|Bool|Int|Real|String>)
	private static boolean typeof_vFIFi_r3ukmDosoQod1QYoYEgw2GLxLPZCGHgc3G1xqQgC0Igo3C8t5SIw3ANGZJ_45QIc4AQGqJo8MPiSq3SlXMWlnHD_4MUh0PZ0_RjGrQiCXPdpmP0HE(Automaton.State state, Automaton automaton) {
		if(state instanceof Automaton.Term && state.kind == K_Not) {
			int data = ((Automaton.Term)state).contents;
			if(typeof_sFIFi_r3ukmDeZ5GLxLPZCWEgw2G1xqQgCGHgc3C8t5SIk3ACGZJ_45QIw3ANGqJo8MPiSq3PlmLWl2HAtJLRhGMZ0_RjGrQiCmMTp1PGEE(data,automaton)) { return true; }
		}
		return false;
	}

	// ^Proton<Any|Void|Bool|Int|Real|String>
	private static boolean typeof_sFIFi_r3ukmDeZ5GLxLPZCWEgw2G1xqQgCGHgc3C8t5SIk3ACGZJ_45QIw3ANGqJo8MPiSq3PlmLWl2HAtJLRhGMZ0_RjGrQiCmMTp1PGEE(int index, Automaton automaton) {
		return typeof_jFIFi_r3ukmD3OpQdGq3xkXE38oQjlq37lHHoZZQoClHgk3GHKLNgCWIgw3OIGbRdtqOIZ4AP45EzcJINhp5RG5Jmx5Sjtq7TpKAU4cx(automaton.get(index),automaton);
	}

	// =========================================================================
	// Schema
	// =========================================================================

	public static final Schema SCHEMA = new Schema(new Schema.Term[]{
		// $3<Not($1<^Type<$3|Atom<Not(^$39<Proton<Any|Void|Bool|Int|Real|String>>)|$39>|Ref($1)|Meta($1)|Nominal(^[^string,$1])|Or($60<^{$1...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$1...|}])|List(^[$67,^[$1...]])>>)>
		Schema.Term("Not",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any))))),
		// $8<And($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
		Schema.Term("And",Schema.Set(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any)))))),
		// $8<Or($6<^{$2<^Type<$8|Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|And($6)|Set(^[$67<^bool>,$6])|Bag(^[$67,^{|$2...|}])|List(^[$67,^[$2...]])>>...}>)>
		Schema.Term("Or",Schema.Set(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any)))))),
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
		// $3<Ref($1<^Type<$3|Atom<Not(^$40<Proton<Any|Void|Bool|Int|Real|String>>)|$40>|Not($1)|Meta($1)|Nominal(^[^string,$1])|Or($60<^{$1...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$1...|}])|List(^[$67,^[$1...]])>>)>
		Schema.Term("Ref",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any))))),
		// $3<Meta($1<^Type<$3|Atom<Not(^$40<Proton<Any|Void|Bool|Int|Real|String>>)|$40>|Not($1)|Ref($1)|Nominal(^[^string,$1])|Or($60<^{$1...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$1...|}])|List(^[$67,^[$1...]])>>)>
		Schema.Term("Meta",Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any))))),
		// Term($7<^[^string,$3<^Type<Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Nominal($7)|Or($63<^{$3...}>)|And($63)|Set(^[$70<^bool>,$63])|Bag(^[$70,^{|$3...|}])|List(^[$70,^[$3...]])>>]>)
		Schema.Term("Term",Schema.List(true,Schema.String,Schema.Or(Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any)))))),
		// $9<Nominal(^[^string,$3<^Type<$9|Atom<Not(^$46<Proton<Any|Void|Bool|Int|Real|String>>)|$46>|Not($3)|Ref($3)|Meta($3)|Or($60<^{$3...}>)|And($60)|Set(^[$67<^bool>,$60])|Bag(^[$67,^{|$3...|}])|List(^[$67,^[$3...]])>>])>
		Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any)))))),
		// Fun(^[$2<^Type<Atom<Not(^$45<Proton<Any|Void|Bool|Int|Real|String>>)|$45>|Not($2)|Ref($2)|Meta($2)|Nominal(^[^string,$2])|Or($67<^{$2...}>)|And($67)|Set(^[$74<^bool>,$67])|Bag(^[$74,^{|$2...|}])|List(^[$74,^[$2...]])>>,$2])
		Schema.Term("Fun",Schema.List(true,Schema.Or(Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any)))),Schema.Any)),
		// $14<Set(^[$2<^bool>,$8<^{$4<^Type<$14|Atom<Not(^$51<Proton<Any|Void|Bool|Int|Real|String>>)|$51>|Not($4)|Ref($4)|Meta($4)|Nominal(^[^string,$4])|Or($8)|And($8)|Bag(^[$2,^{|$4...|}])|List(^[$2,^[$4...]])>>...}>])>
		Schema.Term("Set",Schema.List(true,Schema.Bool,Schema.Set(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Any), Schema.Term("And",Schema.Any), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any))), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any))))))),
		// $14<Bag(^[$2<^bool>,^{|$4<^Type<$14|Atom<Not(^$51<Proton<Any|Void|Bool|Int|Real|String>>)|$51>|Not($4)|Ref($4)|Meta($4)|Nominal(^[^string,$4])|Or($72<^{$4...}>)|And($72)|Set(^[$2,$72])|List(^[$2,^[$4...]])>>...|}])>
		Schema.Term("Bag",Schema.List(true,Schema.Bool,Schema.Bag(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Any)), Schema.Term("List",Schema.List(true,Schema.Any,Schema.List(true,Schema.Any))))))),
		// $14<List(^[$2<^bool>,^[$4<^Type<$14|Atom<Not(^$51<Proton<Any|Void|Bool|Int|Real|String>>)|$51>|Not($4)|Ref($4)|Meta($4)|Nominal(^[^string,$4])|Or($72<^{$4...}>)|And($72)|Set(^[$2,$72])|Bag(^[$2,^{|$4...|}])>>...]])>
		Schema.Term("List",Schema.List(true,Schema.Bool,Schema.List(true,Schema.Or(Schema.Any, Schema.Or(Schema.Term("Not",Schema.Or(Schema.Term("Any"), Schema.Term("Void"), Schema.Term("Bool"), Schema.Term("Int"), Schema.Term("Real"), Schema.Term("String"))), Schema.Any), Schema.Term("Not",Schema.Any), Schema.Term("Ref",Schema.Any), Schema.Term("Meta",Schema.Any), Schema.Term("Nominal",Schema.List(true,Schema.String,Schema.Any)), Schema.Term("Or",Schema.Set(true,Schema.Any)), Schema.Term("And",Schema.Any), Schema.Term("Set",Schema.List(true,Schema.Any,Schema.Any)), Schema.Term("Bag",Schema.List(true,Schema.Any,Schema.Bag(true,Schema.Any)))))))
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
