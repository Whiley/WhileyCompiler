import whiley.lang.*

function f(real x) -> (real y)
requires x >= 0.2345
ensures y < -0.2:
    //
    return 0.0 - x

method main(System.Console sys) -> void:
    sys.out.println(f(1.234))
