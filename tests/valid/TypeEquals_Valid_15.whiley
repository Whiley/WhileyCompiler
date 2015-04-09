import whiley.lang.*

type src is int | [int] | [[int]]

function f(src e) -> bool:
    if e is [any]:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    assume f([1, 2, 3]) == true
    assume f([[1], [2]]) == true
    assume f(1) == false
