import whiley.lang.System

type nat is int

constant num is {1, 2, 3, 4}

function f(num x) => string:
    y = x
    return Any.toString(y)

function g(int x, nat z) => string:
    return f(z)

method main(System.Console sys) => void:
    sys.out.println(g(1, 3))
