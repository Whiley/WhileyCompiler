import whiley.lang.*

type intreal is real | int

function f(intreal e) -> bool:
    if e is int:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(1.134))
    sys.out.println(f(1.0))
