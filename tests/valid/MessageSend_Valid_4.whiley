type Point is {int y, int x}

type Proc is &{int state}

method get(Proc _this) -> int:
    return _this->state

method f(Proc x, int y) -> Point:
    return {y: get(x), x: y}

public export method test() :
    Proc proc = new {state: 1}
    Point p = f(proc,2)
    assume p == {y: 1, x: 2}
