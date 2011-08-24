import whiley.lang.*:*

define R1 as { real x }
define R2 as { int x }

R1 f(R2 i):
    return (R1) i

void ::main(System sys,[string] args):
    sys.out.println(str(f({x:123542})))
    
