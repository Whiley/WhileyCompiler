import println from whiley.lang.System

string g(int z):
    return Any.toString(z)

string f(int x):
    y = x + 1
    return g(y)

void ::main(System.Console sys):
    sys.out.println(f(1))
