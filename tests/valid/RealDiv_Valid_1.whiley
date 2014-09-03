import whiley.lang.System

function g(int x) => real:
    return ((real) x) / 3.0

function f(int x, int y) => string
requires (x >= 0) && (y > 0):
    return Any.toString(g(x))

method main(System.Console sys) => void:
    sys.out.println(f(1, 2))
