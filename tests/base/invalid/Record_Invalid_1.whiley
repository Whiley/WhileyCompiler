

define R1 as { int x }
define R2 as { int x, int y }

bool f(R1 r1, R2 r2):
    return r1 == r2

void ::main(System.Console sys):
    r1 = { x: 1}
    r2 = { x: 1, y: 2 }
    sys.out.println(Any.toString(f(r1,r2)))
