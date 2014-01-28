import whiley.lang.System

type nat is (int x) where x >= 0

function f(nat x, int y) => nat
requires y > 0:
    if true:
        z = x / y
    else:
        z = y / x
    return z

method main(System.Console sys) => void:
    x = f(10, 2)
    sys.out.println(Any.toString(x))
