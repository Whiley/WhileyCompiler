import whiley.lang.*

function f(int | null x) -> bool:
    if x is null:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    int|null x = null
    sys.out.println(f(x))
    sys.out.println(f(1))
