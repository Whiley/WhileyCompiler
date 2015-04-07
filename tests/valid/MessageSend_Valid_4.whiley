import whiley.lang.*

type wmcr6tup is {int y, int x}

type Proc is &{int state}

method get(Proc this) -> int:
    return this->state

method f(Proc x, int y) -> wmcr6tup:
    return {y: get(x), x: y}

method main(System.Console sys) -> void:
    Proc proc = new {state: 1}
    sys.out.println(f(proc, 1))
