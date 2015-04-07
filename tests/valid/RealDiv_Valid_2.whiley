import whiley.lang.*

function g(int x) -> real:
    return ((real) x) / 3.123

function f(int x, int y) -> real:
    return g(x)

method main(System.Console sys) -> void:
    sys.out.println(f(1, 2))
