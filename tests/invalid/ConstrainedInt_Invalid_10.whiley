
constant num is {1, 2, 3, 4}

constant bignum is {1, 2, 3, 4, 5, 6, 7}

function f(num x) -> void:
    y = x
    debug Any.toString(y)

function g({bignum} zs, int z) -> void
requires z in { x | x in zs, x < 6 }:
    f(z)

method main(System.Console sys) -> void:
    g({1, 2, 3, 5}, 5)
