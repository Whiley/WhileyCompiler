import whiley.lang.*

function g(int x) -> real:
    return ((real) x) / 3.0

function f(int x, int y) -> real
requires (x >= 0) && (y > 0):
    return g(x)

method main(System.Console sys) -> void:
    sys.out.println(f(1, 2))
