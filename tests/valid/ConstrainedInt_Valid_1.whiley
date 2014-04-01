import whiley.lang.System

type cr1nat is (int x) where x < 10

function f(cr1nat x) => string:
    int y = x
    return Any.toString(y)

method main(System.Console sys) => void:
    sys.out.println(f(9))
