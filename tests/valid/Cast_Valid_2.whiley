import whiley.lang.*

function f([int] xs) -> [real]:
    return ([real]) xs

method main(System.Console sys) -> void:
    assume f([1, 2, 3]) == [1.0, 2.0, 3.0]
