import whiley.lang.*

function f(int | bool x) -> int:
    if x is int:
        return x
    else:
        return 1

method main(System.Console sys) -> void:
    assume f(true) == 1
    assume f(123) == 123
