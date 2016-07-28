type Rec1 is {int x, int y}
type Rec2 is {int x}

function f(Rec2 rec) -> int:
    return rec.x

public export method test() :
    Rec1 rec = {x: 1, y: 2}
    assume f((Rec2) rec) == 1
    assert rec == {x:1, y: 2}
