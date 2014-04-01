import whiley.lang.System

function f(real x) => string:
    return "GOT REAL"

function f(int x) => string:
    return "GOT INT"

method main(System.Console sys) => void:
    sys.out.println(f(1))
    sys.out.println(f(1.23))
