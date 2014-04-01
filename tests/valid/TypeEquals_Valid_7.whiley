import whiley.lang.System

type intreal is real | int

function f(intreal e) => string:
    if e is int:
        return "int"
    else:
        return "real"

method main(System.Console sys) => void:
    sys.out.println(f(1))
    sys.out.println(f(1.134))
    sys.out.println(f(1.0))
