import println from whiley.lang.System

define Rtypes as {int x, int y} | {int x, int z}

string f(Rtypes e):
    if e is {int x, int y}:
        return "GOT IT"
    else:
        return "NOPE"

void ::main(System.Console sys):
    sys.out.println(f({x: 1, y: 1}))
    sys.out.println(f({x: 1, z: 1}))
