import * from whiley.lang.*

string f([int] a) requires |a| > 0:
     a[0] = 5
     return Any.toString(a)

void ::main(System.Console sys,[string] args):
     b = [1,2,3]
     sys.out.println(Any.toString(b))
     sys.out.println(f(b))
     sys.out.println(Any.toString(b))
