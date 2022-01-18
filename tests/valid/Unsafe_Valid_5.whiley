unsafe function f(int x) -> (int r):
    return x

unsafe final int x = f(123)

unsafe public export method test():
    assert x == 123