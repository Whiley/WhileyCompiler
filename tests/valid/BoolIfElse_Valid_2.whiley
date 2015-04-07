import whiley.lang.*

function f(bool b) -> int:
    if b:
        return 1
    else:
        return 0

method main(System.Console sys) -> void:
    sys.out.println(f(true))
    sys.out.println(f(false))
