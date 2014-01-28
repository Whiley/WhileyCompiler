import whiley.lang.System

type Rec1 is {int x}

type Rec2 is {real x}

function f(Rec2 rec) => int:
    x / y = rec.x
    return x

method main(System.Console sys) => void:
    rec = {x: 1}
    sys.out.println(Any.toString(rec))
    num = f(rec)
    sys.out.println(Any.toString(rec))
    sys.out.println(Any.toString(num))
