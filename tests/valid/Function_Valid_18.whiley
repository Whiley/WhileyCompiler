import whiley.lang.*

type nat is (int x) where x >= 0

function abs(int item) -> nat:
    return Math.abs(item)

function nop(nat item) -> (nat r)
ensures item == r:
    //
    return Math.abs(item)

method main(System.Console sys) -> void:
    nat xs = abs(-123)
    assume xs == 123
    xs = nop(1)
    assume xs == 1
