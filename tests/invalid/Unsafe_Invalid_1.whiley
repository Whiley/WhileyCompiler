unsafe function f(int x) -> (int y):
    assert x >= 0
    return x

public export method test():
    assert f(1) == 1
