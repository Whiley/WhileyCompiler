import * from whiley.lang.*

define Rtypes as {int x, int y}|{int x, int y, int z}

string f(Rtypes e):
    if e is {[int] x}:
        return "GOT IT"
    else:
        return "NOPE"

void ::main(System.Console sys,[string] args):
    sys.out.println(f({x: 1, y: 1}))
    sys.out.println(f({x: 1, y:1, z: 1}))
