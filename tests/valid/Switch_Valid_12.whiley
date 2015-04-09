import whiley.lang.*

function f([int] x) -> int:
    switch x:
        case []:
            return 0
        case [1]:
            return -1
    return 10

method main(System.Console sys) -> void:
    assume f([]) == 0
    assume f([1]) == -1
    assume f([3]) == 10
    assume f([1, 2, 3]) == 10
