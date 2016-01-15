

function g(int x) -> real:
    return (real) (x / 3)

function f(int x, int y) -> real:
    return g(x)

public export method test() :
    assume f(1, 2) == 0.0
