

type Rec1 is {int x}

type Rec2 is {real x}

function f(Rec2 rec) -> int:
    int x
    int y
    x/y = rec.x
    return x

public export method test() :
    Rec1 rec = {x: 1}
    int num = f((Rec2) rec)
    assume num == 1
    assert rec == {x:1}
