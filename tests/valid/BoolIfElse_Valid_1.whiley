import whiley.lang.*

function f(bool b) -> bool:
    if b:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f(true))
    sys.out.println(f(false))
