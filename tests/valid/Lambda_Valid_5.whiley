

method f(int x) -> int:
    return x + 1

method g(method func(int) -> int, int p) -> int:
    return func(p)

public export method test() :
    int y = g(&(int x -> f(x + 1)), 5)
    assume y == 7
