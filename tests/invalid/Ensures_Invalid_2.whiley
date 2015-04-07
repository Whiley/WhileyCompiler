
method g(int x) -> int:
    return x + 1

method f(int x) -> int
requires x > g(x):
    return x

method main() -> int:
    return f(1)
