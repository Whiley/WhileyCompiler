import whiley.lang.*

type bytes is {int b1, int b2}

function f(int b) -> bytes:
    return {b1: b, b2: 2}

method main(System.Console sys) -> void:
    int b = 1
    bytes bs = f(b)
    sys.out.println(bs)
    bs = {b1: b, b2: b}
    sys.out.println(bs)
