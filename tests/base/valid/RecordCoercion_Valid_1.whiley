import println from whiley.lang.System

define Rec1 as {int x}
define Rec2 as {real x}

int f(Rec2 rec):
    x,y = rec.x
    return x

void ::main(System.Console sys):
    rec = {x: 1}
    sys.out.println(Any.toString(rec))
    num = f(rec)
    sys.out.println(Any.toString(rec))
    sys.out.println(Any.toString(num))
