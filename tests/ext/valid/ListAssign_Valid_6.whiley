import * from whiley.lang.*

string f([int] a) requires |a| > 0:
     a[0] = 5
     return toString(a)

void ::main(System sys,[string] args):
     b = [1,2,3]
     sys.out.println(toString(b))
     sys.out.println(f(b))
     sys.out.println(toString(b))
