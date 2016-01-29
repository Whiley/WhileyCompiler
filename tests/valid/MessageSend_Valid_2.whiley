

type Proc is &{int state}

method get(Proc this) -> int:
    return this->state

method f(Proc x) -> int:
    return get(x)

public export method test() :
    Proc proc = new {state: 123}
    assume f(proc) == 123
