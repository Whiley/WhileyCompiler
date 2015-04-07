import whiley.lang.*

type Proc is &{int state}

method get(Proc this) -> int:
    return this->state

method f(Proc x) -> int:
    return get(x)

method main(System.Console sys) -> void:
    Proc proc = new {state: 123}
    sys.out.println(f(proc))
