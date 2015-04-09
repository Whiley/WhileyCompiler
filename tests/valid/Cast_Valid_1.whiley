import whiley.lang.*

function f(int i) -> real:
    return (real) i

method main(System.Console sys) -> void:
    assume f(1) == 1.0
