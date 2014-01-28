import whiley.lang.System

constant num is {1, 2, 3, 4}

constant bignum is {1, 2, 3, 4, 5, 6, 7}

function f(num x) => string:
    int y = x
    return Any.toString(y)

function g({bignum} zs, int z) => string:
    return f(z)

method main(System.Console sys) => void:
    sys.out.println(g({1, 2, 3, 5}, 3))
