import println from whiley.lang.System

function f(int | null x) => string:
    if x is null:
        return "GOT NULL"
    else:
        return "GOT INT"

method main(System.Console sys) => void:
    x = null
    sys.out.println(f(x))
    sys.out.println(f(1))
