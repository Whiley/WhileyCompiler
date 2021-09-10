unsafe function f(int x) -> (int r):
    return x

unsafe final int x = f(1)

public export method test():
    assert x == 1