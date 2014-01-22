import println from whiley.lang.System

type Proc is &{int state}

method get(Proc this) => int:
    return this->state

method f(Proc x) => [int]:
    return [1, 2, 3, x.get()]

method main(System.Console sys) => void:
    proc = new {state: 1}
    sys.out.println(Any.toString(f(proc)))
