import whiley.lang.*

function f(real x) -> (real y)
requires x > 0.0
ensures y < 0.0:
    return -x

method main(System.Console sys) -> void:
    sys.out.println(f(1.2))
    sys.out.println(f(0.00001))
    sys.out.println(f(5632.0))
