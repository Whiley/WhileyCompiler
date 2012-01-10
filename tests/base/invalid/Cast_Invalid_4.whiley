

define R1 as { real x, int y }
define R2 as { int x, real y }
define R3 as { int x, int y }

R3 f(R1|R2 i):
    return (R3) i

void ::main(System.Console sys):
    sys.out.println(Any.toString(f({x:123542.0, y:123})))
    
