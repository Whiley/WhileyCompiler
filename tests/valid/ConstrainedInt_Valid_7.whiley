import println from whiley.lang.System

constant num is {1, 2, 3, 4}

function f(num x) => string:
    y = x
    return Any.toString(y)

function g(int x, int z) => string:
    return f(z)

method main(System.Console sys) => void:
    sys.out.println(g(1, 2))
