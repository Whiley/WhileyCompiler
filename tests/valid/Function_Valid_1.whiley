import whiley.lang.*

function f(real x) -> int:
    return 1

function f(int x) -> int:
    return 2

method main(System.Console sys) -> void:
    assume f(1) == 2
    assume f(1.23) == 1
