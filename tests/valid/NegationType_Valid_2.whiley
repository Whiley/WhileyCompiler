import whiley.lang.*

function f(int x) -> !null:
    return x

method main(System.Console sys) -> void:
    assume f(1) == 1
