import whiley.lang.*

function f(int x) -> (int r)
requires x >= 0
ensures r >= 0 && x >= 0:
    return x

method main(System.Console sys) -> void:
    assume f(10) == 10
