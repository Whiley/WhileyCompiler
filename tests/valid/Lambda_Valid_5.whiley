import whiley.lang.*

method f(int x) -> int:
    return x + 1

method g(method func(int) -> int, int p) -> int:
    return func(p)

method main(System.Console sys) -> void:
    int y = g(&(int x -> f(x + 1)), 5)
    sys.out.println(y)
