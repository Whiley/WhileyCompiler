

function f(int x) -> int:
    return x + 1

function g(int x, int y) -> {int nx, int ny}
requires y == f(x):
    //
    return {nx: x, ny: y}

public export method test() :
    {int nx, int ny} p = g(1, f(1))
    assume p.nx == 1
    assume p.ny == 2
