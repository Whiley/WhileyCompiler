import whiley.lang.*

type bytes is {int b1, int b2}

function f(int a) -> bytes:
    bytes bs = {b1: a, b2: a + 1}
    return bs

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(9))
