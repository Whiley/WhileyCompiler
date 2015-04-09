import whiley.lang.*

function f(real x) -> real:
    return 0.0 - x

method main(System.Console sys) -> void:
    assume f(1.234) == -1.234
