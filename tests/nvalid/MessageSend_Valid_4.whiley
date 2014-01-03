import println from whiley.lang.System

type wmcr6tup is {int y, int x}

type Proc is ref {int state}

method get(Proc this) => int:
    return this->state

method f(Proc x, int y) => wmcr6tup:
    return {y: x.get(), x: y}

method main(System.Console sys) => void:
    proc = new {state: 1}
    sys.out.println(Any.toString(f(proc, 1)))
