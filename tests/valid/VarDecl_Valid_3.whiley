

function g(int z) -> int:
    return z

function f(int x) -> int:
    int y = x + 1
    return g(y)

public export method test() :
    assume f(1) == 2
