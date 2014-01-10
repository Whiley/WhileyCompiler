import println from whiley.lang.System

method f(int x) => int:
    return x + 1

type func_t is function (int) => int

method g(int p) => int:
    func_t func = &(int x -> f(x + 1))
    return func(p)

method main(System.Console sys) => void:
    x = g(5)
    sys.out.println(x)
