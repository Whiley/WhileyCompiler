import println from whiley.lang.System

define bytes as { int b1, int b2 }

bytes f(int a):
    bs = {b1:a,b2:a+1}
    return bs

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(9)))
