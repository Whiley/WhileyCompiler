import whiley.lang.*

type state is {int y, int x}

type pState is &state

method send2(pState this, int x, System.Console sys) -> int:
    sys.out.println(x)
    return -1

method main(System.Console sys) -> void:
    int x = send2(new {y: 2, x: 1},1, sys)
    sys.out.println(x)
