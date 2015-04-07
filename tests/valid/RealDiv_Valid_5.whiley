import whiley.lang.*

function g(real x) -> (real y)
requires x <= 0.5
ensures y <= 0.166666666666668:
    //
    return x / 3.0

method main(System.Console sys) -> void:
    sys.out.println(g(0.234))
