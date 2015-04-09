import whiley.lang.*

function g(int x) -> real:
    return (real) (x / 3)

function f(int x, int y) -> real:
    return g(x)

method main(System.Console sys) -> void:
    assume f(1, 2) == 0.0
