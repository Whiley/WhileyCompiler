import * from whiley.lang.*

function f(int x) => int:
    return x + 1

function g(real(int) func) => int:
    return func(1)

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(&f)))
