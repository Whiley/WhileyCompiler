import whiley.lang.*:*

define Rtypes as {real x, real y} | {int x, int z}

string f(Rtypes e):
    if e is {int x, int z}:
        return "GOT IT"
    else:
        return "NOPE"

void System::main([string] args):
    this.out.println(f({x: 1.2, y: 1.2}))
    this.out.println(f({x: 1, y: 1}))
    this.out.println(f({x: 1, z: 1}))
