import * from whiley.lang.*

void ::f(System.Console sys, int x):
    sys.out.println(Any.toString(x))

void ::main(System.Console sys):
    f(sys,1)
    sys.out.print("")
