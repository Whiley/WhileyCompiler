import println from whiley.lang.System

define R1 as { real x }
define R2 as { int x }

R1 f(R2 i):
    return (R1) i

void ::main(System.Console sys):
    sys.out.println(Any.toString(f({x:123542})))
    
