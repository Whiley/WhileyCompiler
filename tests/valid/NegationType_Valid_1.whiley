import whiley.lang.*

function f(any x) -> !null:
    if x is null:
        return 1
    else:
        return x

method main(System.Console sys) -> void:
    assume f(1) == 1
    assume f([1, 2, 3]) == [1,2,3]
