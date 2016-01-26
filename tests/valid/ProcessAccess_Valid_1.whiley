type etype is {int rest, int mode}
type Ptype is &etype

method get(Ptype this) -> int:
    this->mode = 1
    this->rest = 123
    return this->mode

public export method test() :
    Ptype p = new {rest: 2, mode: 2}
    assume (*p) == {rest: 2, mode: 2}
    int x = get(p)
    assume (*p) == {rest: 123, mode: 1}
    assume x == 1

