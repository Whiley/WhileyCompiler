import println from whiley.lang.System

function f(int x) => int:
    return x * 12

function g(real(int) func) => real:
    return func(1)

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(&f)))
