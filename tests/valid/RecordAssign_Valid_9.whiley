import whiley.lang.*
import * from whiley.lang.Int

type bytes is {i8 b1, i8 b2}

function f(int a) -> bytes
requires a > 0 && a < 10:
    bytes bs = {b1: a, b2: a + 1}
    return bs

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(9))
