import whiley.lang.*

function f(int x, real y) -> bool:
    if ((real) x) == y:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f(1, 4.0))
    sys.out.println(f(1, 4.2))
    sys.out.println(f(0, 0.0))
