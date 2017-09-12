
method g(int x) -> int:
    return x + 1

method f(int x) -> int
requires x > g(x):
    return x

public export method test() -> int:
    return f(1)
