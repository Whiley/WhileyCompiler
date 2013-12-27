import println from whiley.lang.System

real f(real x):
    return -x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1.2)))
    sys.out.println(Any.toString(f(0.00001)))
    sys.out.println(Any.toString(f(5632)))
