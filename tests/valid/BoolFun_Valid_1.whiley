import whiley.lang.*

function f(bool b) -> bool:
    return b

method main(System.Console sys) -> void:
    bool x = true
    sys.out.println(f(x))
    x = false
    sys.out.println(f(x))
