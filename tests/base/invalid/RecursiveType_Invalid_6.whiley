import * from whiley.lang.*

// the following line should simply not compile.
define junk as junk | int

int f(junk x):
    return x

void ::main(System.Console sys):
    x = 1
    sys.out.println(Any.toString(f(x)))