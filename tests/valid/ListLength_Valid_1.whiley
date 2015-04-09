import whiley.lang.*

type nat is (int x) where x >= 0

function f([int] xs) -> nat:
    return |xs|

method main(System.Console sys) -> void:
    assume f([1, 2, 3]) == 3
    assume f([]) == 0
