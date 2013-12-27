import println from whiley.lang.System

!null f(int x):
    return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1)))


