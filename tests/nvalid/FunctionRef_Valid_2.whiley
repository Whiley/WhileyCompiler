import println from whiley.lang.System

function f(real x) => real:
    return x + 1

function g(real(int) func) => real:
    return func(1)

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(&f)))
