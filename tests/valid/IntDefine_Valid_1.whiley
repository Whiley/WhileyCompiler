import println from whiley.lang.System

type ir1nat is int where $ > 0

type pir1nat is ir1nat where $ > 1

function f(int x) => string:
    if x > 2:
        y = x
        return Any.toString(y)
    return ""

method main(System.Console sys) => void:
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
