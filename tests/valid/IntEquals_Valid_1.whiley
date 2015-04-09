import whiley.lang.*

function f(int x, real y) -> bool:
    if ((real) x) == y:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    assume f(1, 4.0) == false
    assume f(1, 4.2) == false
    assume f(0, 0.0) == true
