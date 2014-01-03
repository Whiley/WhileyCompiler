import println from whiley.lang.System

function f1(int x) => int:
    return x + 1

function f2(int x) => int:
    return x * 2

function g(int(int) func) => int:
    return func(1234)

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(&f1)))
    sys.out.println(Any.toString(g(&f2)))
