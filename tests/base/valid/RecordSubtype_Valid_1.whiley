import * from whiley.lang.*

// this test tests distributivity of subtyping
define R1 as { null|int x }
define R2 as { int x }
define R3 as { null x }
define R4 as R2|R3


R4 f(R1 x):
    return x

void ::main(System sys,[string] args):
    z1 = f({x: 1})
    z2 = f({x: null})
    sys.out.println(toString(z1))
    sys.out.println(toString(z2))
    
