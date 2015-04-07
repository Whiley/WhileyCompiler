import whiley.lang.*

function g(real x) -> real:
    return x / 3.0

method main(System.Console sys) -> void:
    sys.out.println(g(0.234))
