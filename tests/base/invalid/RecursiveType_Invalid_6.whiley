import * from whiley.lang.*

// the following line should simply not compile.
define junk as junk | int

int f(junk x):
    return x

void ::main(System sys,[string] args):
    x = 1
    sys.out.println(str(f(x)))