import println from whiley.lang.System

real f(int x):
    return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(123)))
