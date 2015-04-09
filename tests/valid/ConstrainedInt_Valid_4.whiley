import whiley.lang.*

type nat is (int x) where x < 10

function f() -> nat:
    return 1

method main(System.Console sys) -> void:
    assume f() == 1
