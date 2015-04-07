import whiley.lang.*

type fr4nat is int

function g(fr4nat x) -> fr4nat:
    return x + 1

function f(fr4nat x) -> int:
    return x

method main(System.Console sys) -> void:
    int y = 1
    sys.out.println(f(g(y)))
