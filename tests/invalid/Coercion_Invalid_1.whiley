import * from whiley.lang.*

type Rec1 is {int y, real x}

type Rec2 is {real y, int x}

type uRec1Rec2 is Rec1 | Rec2

function f(uRec1Rec2 r) -> int:
    if r is Rec1:
        return r.y
    else:
        return r.x

method main(System.Console sys) -> void:
    {int x, int y} rec = {y: 1, x: 1}
    int ans = f( (uRec1Rec2) rec)
    sys.out.println(Any.toString(ans))
