
constant num is {1, 2, 3, 4}

constant bignum is {1, 2, 3, 4, 5, 6, 7}

function f(num x) -> int:
    y = x
    return y

function g({bignum} zs, int z) -> int
requires z in { x | x in zs, x < 6 }:
    return f(z)
