
function f(int x) -> int
requires x >= 0:
    int y = 10 / x
    return y

public export method test() :
    f(10)
    f(0)
