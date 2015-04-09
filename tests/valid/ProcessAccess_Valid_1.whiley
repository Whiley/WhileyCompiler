import whiley.lang.*

type etype is {int rest, int mode}

type Ptype is &etype

method get(Ptype this) -> (int, [int]):
    this->mode = 1
    this->rest = 123
    return (this->mode, Any.toString(*this))

method main(System.Console sys) -> void:
    Ptype p = new {rest: 2, mode: 2}
    assume (*p) == {rest: 2, mode: 2}
    int x, [int] s = get(p)
    assume (*p) == {rest: 123, mode: 1}
    assume x == 1

