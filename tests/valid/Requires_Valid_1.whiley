

function f(int x) -> int:
    return x + 1

function g(int x, int y) -> (int,int)
requires y == f(x):
    //
    return x,y

public export method test() -> void:
    int x, int y = g(1, f(1))
    assume x == 1
    assume y == 2
