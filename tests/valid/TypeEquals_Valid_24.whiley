import whiley.lang.*

type T is [int] | int

function f(T x) -> int:
    if x is [int]:
        return |x|
    else:
        return x

public method main(System.Console sys) -> void:
    assume f([1, 2, 3, 4]) == 4
    assume f(123) == 123
