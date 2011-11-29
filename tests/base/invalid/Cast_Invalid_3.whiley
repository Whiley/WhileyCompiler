import * from whiley.lang.*

define R2 as { real x }
define R1 as { int x }

R1 f(R2 i):
    return (R1) i

void ::main(System sys,[string] args):
    sys.out.println(toString(f({x:123542.12})))
    
