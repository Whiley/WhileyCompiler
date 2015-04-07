import whiley.lang.*

function f(int | bool x) -> int:
    if x is int:
        return x
    else:
        return 1

method main(System.Console sys) -> void:
    sys.out.println(f(true))
    sys.out.println(f(123))
