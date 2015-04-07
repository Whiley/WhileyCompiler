import whiley.lang.*

function g(int z) -> int:
    return z

function f(int x) -> int:
    int y = x + 1
    return g(y)

method main(System.Console sys) -> void:
    sys.out.println(f(1))
