import whiley.lang.*

type Rec1 is {int x}

type Rec2 is {real x}

function f(Rec2 rec) -> int:
    int x / int y = rec.x
    return x

method main(System.Console sys) -> void:
    Rec1 rec = {x: 1}
    sys.out.println(rec)
    int num = f((Rec2) rec)
    sys.out.println(rec)
    sys.out.println(num)
