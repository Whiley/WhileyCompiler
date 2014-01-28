import whiley.lang.System

function f(real x) => real:
    return 0.0 - x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1.234)))
