import whiley.lang.*

function f(bool b) -> int:
    if b:
        return 1
    else:
        return 0

method main(System.Console sys) -> void:
    assume f(true) == 1
    assume f(false) == 0
