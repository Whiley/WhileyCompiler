import * from whiley.lang.*

define R1 as { real x }

int f(real i):
    return (int) i

void ::main(System sys,[string] args):
    sys.out.println(toString(f(1.01)))
    
