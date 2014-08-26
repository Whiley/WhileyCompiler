import whiley.lang.System

function g(real x) => (real y)
requires x <= 0.5
ensures y <= 0.166666666666668:
    //
    return x / 3.0

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(0.234)))
