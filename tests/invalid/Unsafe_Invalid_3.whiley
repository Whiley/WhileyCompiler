unsafe function f(int x) -> (int y):
    assert x >= 0
    return x

function g(int x) -> (int y):
    return f(x)

public export method test():
    int x = g(1)
    assert x == 1
