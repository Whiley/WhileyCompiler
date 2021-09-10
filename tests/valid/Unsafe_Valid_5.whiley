unsafe function f(int x) -> (int r):
    return x

unsafe int x = f(123)

unsafe public export method test():
    assert x == 123