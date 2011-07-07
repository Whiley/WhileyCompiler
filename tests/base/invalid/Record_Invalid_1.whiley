define R1 as { int x }
define R2 as { int x, int y }

bool f(R1 r1, R2 r2):
    return r1 == r2

void System::main([string] args):
    r1 = { x: 1}
    r2 = { x: 1, y: 2 }
    out.println(str(f(r1,r2)))
