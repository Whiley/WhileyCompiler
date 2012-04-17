import println from whiley.lang.System

void ::f(System.Console sys, [int] x):
    z = |x|
    sys.out.println(Any.toString(z))
    sys.out.println(Any.toString(x[z-1]))

void ::main(System.Console sys):
     arr = [1,2,3]
     // following line should block
     f(sys,arr)
