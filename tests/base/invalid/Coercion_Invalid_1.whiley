import whiley.lang.*:*

define Rec1 as { real x, int y }
define Rec2 as { int x, real y }
define uRec1Rec2 as Rec1 | Rec2

int f(uRec1Rec2 r):
    if r is Rec1:
        return r.y
    else:
        return r.x

void System::main([string] args):
    rec = { x: 1, y: 1}
    ans = f(rec)
    this.out.println(str(ans))