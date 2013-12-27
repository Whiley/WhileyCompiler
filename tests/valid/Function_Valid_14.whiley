import println from whiley.lang.System

string f(int x):
    return "F(INT)"

string f(real y):
    return "F(REAL)"

string f([int] xs):
    return "F([int])"

string f({int} xs):
    return "F({int})"


void ::main(System.Console sys):
    sys.out.println(f(1.234))
    sys.out.println(f(1))
    sys.out.println(f([1,2,3]))
    sys.out.println(f({1,2,3}))
