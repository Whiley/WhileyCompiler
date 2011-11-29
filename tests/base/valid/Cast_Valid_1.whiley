import * from whiley.lang.*

define R1 as { real x }

real f(int i):
    return (real) i

void ::main(System sys,[string] args):
    sys.out.println(toString(f(1)))
    
