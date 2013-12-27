import println from whiley.lang.System

real g(int x):
     return x / 3.0

string f(int x, int y) requires x>=0 && y>0:
    return Any.toString(g(x))

void ::main(System.Console sys):
     sys.out.println(f(1,2))
