import println from whiley.lang.System

function g(real x) => real
requires x <= 0.5
ensures $ <= 0.166666666666668:
    return x / 3

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(0.234)))
