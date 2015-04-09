import whiley.lang.*

type fr4nat is (int x) where x >= 0

function g(fr4nat x) -> fr4nat:
    return x + 1

function f(fr4nat x) -> int:
    return x

method main(System.Console sys) -> void:
    int y = 1
    assume f(g(y)) == 2
