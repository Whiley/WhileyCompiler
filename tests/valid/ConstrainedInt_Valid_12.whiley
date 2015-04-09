import whiley.lang.*

type cr1nat is int

function f(cr1nat x) -> int:
    int y = x
    return y

method main(System.Console sys) -> void:
    assume f(9) == 9
