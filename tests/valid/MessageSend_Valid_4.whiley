

type wmcr6tup is {int y, int x}

type Proc is &{int state}

method get(Proc this) -> int:
    return this->state

method f(Proc x, int y) -> wmcr6tup:
    return {y: get(x), x: y}

public export method test() :
    Proc proc = new {state: 1}
    assume f(proc, 2) == {y: 1, x: 2}
