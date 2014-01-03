import println from whiley.lang.System

type state is {int y, int x}

type pState is ref state

method send2(pState this, int x, System.Console sys) => int:
    sys.out.println(Any.toString(x))
    return -1

method main(System.Console sys) => void:
    x = (new {y: 2, x: 1}).send2(1, sys)
    sys.out.println(Any.toString(x))
