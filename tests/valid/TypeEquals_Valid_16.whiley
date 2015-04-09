import whiley.lang.*

type src is int | [src]

function f(src e) -> bool:
    if e is [any]:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    assume f([1]) == true
    assume f([[1]]) == true
    assume f([[[1]]]) == true
    assume f(1) == false
