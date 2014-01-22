import println from whiley.lang.System

function f(real x) => real
requires x >= 0.2345
ensures $ < -0.2:
    return 0.0 - x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1.234)))
