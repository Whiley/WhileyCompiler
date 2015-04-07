import whiley.lang.*

type etype is {int rest, int mode}

type Ptype is &etype

method get(Ptype this) -> (int, [int]):
    this->mode = 1
    this->rest = 123
    return (this->mode, Any.toString(*this))

method main(System.Console sys) -> void:
    Ptype p = new {rest: 2, mode: 1}
    sys.out.println(*p)
    int x, [int] s = get(p)
    sys.out.println_s(s)
    sys.out.println(*p)
    sys.out.println(x)
