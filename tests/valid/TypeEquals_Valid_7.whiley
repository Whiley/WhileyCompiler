import whiley.lang.*

type intreal is real | int

function f(intreal e) -> bool:
    if e is int:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    assume f(1) == true
    assume f(1.134) == false
    assume f(1.0) == false
