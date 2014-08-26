import whiley.lang.System

function g(real x) => real:
    return x / 3.0

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(0.234)))
