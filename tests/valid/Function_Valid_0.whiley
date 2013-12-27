import println from whiley.lang.System

string f(real x):
    return "GOT REAL"

string f(int x):
    return "GOT INT"

void ::main(System.Console sys):
    sys.out.println(f(1))
    sys.out.println(f(1.23))
