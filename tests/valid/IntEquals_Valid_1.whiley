import whiley.lang.System

function f(int x, real y) => string:
    if x == y:
        return "EQUAL"
    else:
        return "NOT EQUAL"

method main(System.Console sys) => void:
    sys.out.println(f(1, 4.0))
    sys.out.println(f(1, 4.2))
    sys.out.println(f(0, 0))
