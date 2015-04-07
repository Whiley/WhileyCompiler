import whiley.lang.*

function f1(int x) -> int:
    return x + 1

function f2(int x) -> int:
    return x * 2

type func_t is function(int) -> int

function g(func_t func) -> int:
    return func(1234)

method main(System.Console sys) -> void:
    sys.out.println(g(&f1))
    sys.out.println(g(&f2))
