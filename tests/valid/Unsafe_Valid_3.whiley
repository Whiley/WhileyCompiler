unsafe function f(int x) -> (int y)
requires x >= 0
ensures y >= 0:
    // Following line won't verify
    return x - 1

public unsafe export method test():
    assert f(1) == 0

