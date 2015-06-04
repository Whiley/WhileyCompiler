type Rec1 is {int y, real x}

type Rec2 is {real y, int x}

type uRec1Rec2 is Rec1 | Rec2

function f(uRec1Rec2 r) -> int:
    if r is Rec1:
        return r.y
    else:
        return r.x

function g() -> int:
    {int x, int y} rec = {y: 1, x: 1}
    return f( (uRec1Rec2) rec)
