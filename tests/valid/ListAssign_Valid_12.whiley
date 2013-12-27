import println from whiley.lang.System

string f([int] a):
     a[0] = 5
     return Any.toString(a)

void ::main(System.Console sys):
     b = [1,2,3]
     sys.out.println(Any.toString(b))
     sys.out.println(f(b))
     sys.out.println(Any.toString(b))
