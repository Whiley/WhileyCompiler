import * from whiley.lang.*

function f(int x) -> int:
    return x + 1

function g(function func(real)->int) -> int:
    return func(1.2345)

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(g(&f)))
