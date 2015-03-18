import whiley.lang.System

function g(int x) -> real:
    return (real) (x / 3)

function f(int x, int y) -> ASCII.string:
    return Any.toString(g(x))

method main(System.Console sys) -> void:
    sys.out.println_s(f(1, 2))
