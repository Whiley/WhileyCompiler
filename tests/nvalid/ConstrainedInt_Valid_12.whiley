import println from whiley.lang.System

type cr1nat is int

function f(cr1nat x) => string:
    y = x
    return Any.toString(y)

method main(System.Console sys) => void:
    sys.out.println(f(9))
