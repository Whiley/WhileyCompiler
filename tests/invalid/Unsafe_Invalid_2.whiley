unsafe method f(int x) -> (int y):
    assert x >= 0
    return x

public export method test():
    int x = f(1)
    assert x == 1
