import println from whiley.lang.System

define Rtypes as {real x, real y} | {int x, int z}

string f(Rtypes e):
    if e is {int x, int z}:
        return "GOT IT"
    else:
        return "NOPE"

void ::main(System.Console sys):
    sys.out.println(f({x: 1.2, y: 1.2}))
    sys.out.println(f({x: 1, y: 1}))
    sys.out.println(f({x: 1, z: 1}))
