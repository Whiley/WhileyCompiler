import whiley.lang.*

function f(int x, int y) -> int
requires y != 0:
    return x / y

method main(System.Console sys) -> void:
    assume f(10, 2) == 5
    assume f(9, 3) == 3
    assume f(10, 3) == 3

