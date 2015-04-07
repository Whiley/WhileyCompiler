import whiley.lang.*

function g(int z) -> int
requires z > 1:
    return z

function f(int x) -> int
requires x > 0:
    int y = x + 1
    return g(y)

method main(System.Console sys) -> void:
    sys.out.println(f(1))
