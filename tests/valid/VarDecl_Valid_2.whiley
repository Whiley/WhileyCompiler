import whiley.lang.System

function g(int z) -> ASCII.string
requires z > 1:
    return Any.toString(z)

function f(int x) -> ASCII.string
requires x > 0:
    int y = x + 1
    return g(y)

method main(System.Console sys) -> void:
    sys.out.println(f(1))
