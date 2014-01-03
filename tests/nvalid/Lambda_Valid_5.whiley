import println from whiley.lang.System

method f(int x) => int:
    return x + 1

function g(int ::(int) func, int p) => int:
    return func(p)

method main(System.Console sys) => void:
    x = g(&(int x -> f(x + 1)), 5)
    sys.out.println(x)
