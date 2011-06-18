define Rtypes as {real x, real y} | {int x, int z}

string f(Rtypes e):
    if e ~= {int x, int z}:
        return "GOT IT"
    else:
        return "NOPE"

void System::main([string] args):
    out.println(f({x: 1.2, y: 1.2}))
    out.println(f({x: 1, y: 1}))
    out.println(f({x: 1, z: 1}))
