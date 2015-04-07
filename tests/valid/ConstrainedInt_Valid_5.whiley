import whiley.lang.*

type nat is int

function f() -> nat:
    return 1

method main(System.Console sys) -> void:
    sys.out.println(f())
