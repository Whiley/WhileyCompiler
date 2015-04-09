import whiley.lang.*

function f(int x) -> int:
    return x / 3

public method main(System.Console sys) -> void:
    assume f(10) == 3
