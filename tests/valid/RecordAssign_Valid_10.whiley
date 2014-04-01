import whiley.lang.System

type bytes is {int b1, int b2}

function f(int a) => bytes:
    bytes bs = {b1: a, b2: a + 1}
    return bs

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(9)))
