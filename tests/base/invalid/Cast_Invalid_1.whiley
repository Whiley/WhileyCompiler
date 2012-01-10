import * from whiley.lang.*

define R1 as { real x }

int f(real i):
    return (int) i

void ::main(System.Console sys,[string] args):
    sys.out.println(Any.toString(f(1.01)))
    
