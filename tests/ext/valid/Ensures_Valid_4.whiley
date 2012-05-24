import println from whiley.lang.System

int f(int x) requires x>=0, ensures $>=0 && x>=0:
    return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(10)))
