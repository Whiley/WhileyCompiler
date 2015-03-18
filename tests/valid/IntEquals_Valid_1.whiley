import whiley.lang.System

function f(int x, real y) -> ASCII.string:
    if ((real) x) == y:
        return "EQUAL"
    else:
        return "NOT EQUAL"

method main(System.Console sys) -> void:
    sys.out.println_s(f(1, 4.0))
    sys.out.println_s(f(1, 4.2))
    sys.out.println_s(f(0, 0.0))
