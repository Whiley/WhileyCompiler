import println from whiley.lang.System

real g(int x):
     return x / 3.123

string f(int x, int y):
    return Any.toString(g(x))

void ::main(System.Console sys):
     sys.out.println(f(1,2))
