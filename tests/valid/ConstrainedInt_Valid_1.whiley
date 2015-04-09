import whiley.lang.*

type cr1nat is (int x) where x < 10

function f(cr1nat x) -> int:
    int y = x
    return y

method main(System.Console sys) -> void:
    assume f(9) == 9
