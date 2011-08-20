import whiley.lang.*:*

define Rec1 as {int x}
define Rec2 as {real x}

int f(Rec2 rec):
    x,y = rec.x
    return x

void System::main([string] args):
    rec = {x: 1}
    this.out.println(str(rec))
    num = f(rec)
    this.out.println(str(rec))
    this.out.println(str(num))
