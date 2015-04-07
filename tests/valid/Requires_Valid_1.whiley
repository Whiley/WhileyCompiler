import whiley.lang.*

function f(int x) -> int:
    return x + 1

function g(int x, int y) -> (int,int)
requires y == f(x):
    //
    return x,y

method main(System.Console sys) -> void:
    int x, int y = g(1, f(1))
    sys.out.println(x)
    sys.out.println(y)
