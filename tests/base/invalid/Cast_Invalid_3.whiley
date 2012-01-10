

define R2 as { real x }
define R1 as { int x }

R1 f(R2 i):
    return (R1) i

void ::main(System.Console sys):
    sys.out.println(Any.toString(f({x:123542.12})))
    
