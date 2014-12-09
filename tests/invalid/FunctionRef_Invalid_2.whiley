import * from whiley.lang.*

function f(int x) -> int:
    return x + 1

function g(function func(int)->real) -> int:
    return func(1)

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(g(&f)))
