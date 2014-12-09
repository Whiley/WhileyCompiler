import * from whiley.lang.*

type state is {int y, int x}

type pState is &state

method f(pState this) -> void:
    this = new {y: 4, x: 3}

method main(System.Console sys) -> void:
    pState p = new {y: 2, x: 1}
    sys.out.println(Any.toString() * p)
    p.f()
    sys.out.println(Any.toString() * p)
