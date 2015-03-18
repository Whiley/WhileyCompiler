import whiley.lang.System

function f(real x) -> ASCII.string:
    return "GOT REAL"

function f(int x) -> ASCII.string:
    return "GOT INT"

method main(System.Console sys) -> void:
    sys.out.println_s(f(1))
    sys.out.println_s(f(1.23))
