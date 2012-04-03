import println from whiley.lang.System

// this is a comment!
define IntList as int|[int]

void ::f(System.Console sys, int y):
    sys.out.println(Any.toString(y))

void ::g(System.Console sys, [int] z):
    sys.out.println(Any.toString(z))

void ::main(System.Console sys):
    x = 123
    f(sys,x)
    x = [1,2,3]
    g(sys,x)
