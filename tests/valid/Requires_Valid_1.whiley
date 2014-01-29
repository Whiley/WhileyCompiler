import whiley.lang.System

function f(int x) => int:
    return x + 1

function g(int x, int y) => (string, string)
requires y == f(x):
    //
    return (Any.toString(x), Any.toString(y))

method main(System.Console sys) => void:
    int x, int y = g(1, f(1))
    debug x ++ "\n"
    debug y ++ "\n"