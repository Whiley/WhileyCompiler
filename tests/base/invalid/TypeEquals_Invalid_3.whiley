define Rtypes as {int x, int y}|{int x, int y, int z}

string f(Rtypes e):
    if e ~= {[int] x}:
        return "GOT IT"
    else:
        return "NOPE"

void System::main([string] args):
    out.println(f({x: 1, y: 1}))
    out.println(f({x: 1, y:1, z: 1}))
