import whiley.lang.*
import * from whiley.lang.Int

type bytes is {i8 b1, i8 b2}

function f(i8 b) -> bytes:
    return {b1: b, b2: 2}

method main(System.Console sys) -> void:
    i8 b = 1
    bytes bs = f(b)
    sys.out.println(bs)
    bs = {b1: b, b2: b}
    sys.out.println(bs)
