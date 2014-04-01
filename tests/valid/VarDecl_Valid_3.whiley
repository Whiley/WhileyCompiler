import whiley.lang.System

function g(int z) => string:
    return Any.toString(z)

function f(int x) => string:
    int y = x + 1
    return g(y)

method main(System.Console sys) => void:
    sys.out.println(f(1))
