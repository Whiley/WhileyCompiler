unsafe function f(int x) -> (int r):
    return x

unsafe final int x = f(1)

unsafe public export method test():
    assert x == 1