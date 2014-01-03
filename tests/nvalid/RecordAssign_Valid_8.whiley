import println from whiley.lang.System

type bytes is {int b1, int b2}

function f(int b) => bytes:
    return {b1: b, b2: 2}

method main(System.Console sys) => void:
    b = 1
    bs = f(b)
    sys.out.println(Any.toString(bs))
    bs = {b1: b, b2: b}
    sys.out.println(Any.toString(bs))
