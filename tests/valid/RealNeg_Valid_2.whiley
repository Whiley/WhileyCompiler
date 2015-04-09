import whiley.lang.*

function f(real x) -> real:
    return -x

method main(System.Console sys) -> void:
    assume f(1.2) == -1.2
    assume f(0.00001) == -0.00001
    assume f(5632.0) == -5632.0
