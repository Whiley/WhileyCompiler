import whiley.lang.System

type Rec1 is {int x}

type Rec2 is {real x}

function f(Rec2 rec) => int:
    int x / int y = rec.x
    return x

method main(System.Console sys) => void:
    Rec1 rec = {x: 1}
    sys.out.println(Any.toString(rec))
    int num = f(rec)
    sys.out.println(Any.toString(rec))
    sys.out.println(Any.toString(num))
