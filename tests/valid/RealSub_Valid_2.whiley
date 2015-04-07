import whiley.lang.*

function f(real x) -> real:
    return 0.0 - x

method main(System.Console sys) -> void:
    sys.out.println(f(1.234))
