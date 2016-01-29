

function g(int z) -> int
requires z > 1:
    return z

function f(int x) -> int
requires x > 0:
    int y = x + 1
    return g(y)

public export method test() :
    assume f(1) == 2
