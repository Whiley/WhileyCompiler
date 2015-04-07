import whiley.lang.*

function f(int x) -> (int y)
// Return must be greater than input
ensures x < y:
    //
    return x + 1

function g(int x, int y) -> int
requires x > y:
    return x + y

method main(System.Console sys) -> void:
    int a = 2
    int b = 1
    if |sys.args| == 0:
        a = f(b)
    int x = g(a, b)
    sys.out.println(x)
