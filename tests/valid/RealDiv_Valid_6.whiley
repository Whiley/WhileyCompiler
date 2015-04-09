import whiley.lang.*

function g(real x) -> real:
    return x / 3.0

method main(System.Console sys) -> void:
    assume g(0.234) == (0.468/6.0)
