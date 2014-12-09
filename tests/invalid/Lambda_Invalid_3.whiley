import whiley.lang.System

function f(int x) -> int
requires x > 1:
    return x + 1

function g(int p) -> int
requires p >= 0:
    func = &(int x -> f(x + 1))
    return func(p)

method main(System.Console sys) -> void:
    x = g(5)
    sys.out.println(x)
