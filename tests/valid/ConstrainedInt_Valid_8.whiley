import whiley.lang.System

constant num is {1, 2, 3, 4}

constant bignum is {1, 2, 3, 4, 5, 6, 7}

function f(num x) => string:
    int y = x
    return Any.toString(y)

function g({bignum} zs, int z) => string:
    if (z in zs) && (z in num):
        return f(z)
    else:
        return "MISS"

method main(System.Console sys) => void:
    sys.out.println(g({1, 2, 3, 5}, 3))
