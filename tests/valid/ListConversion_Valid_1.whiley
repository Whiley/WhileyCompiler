import whiley.lang.*

function f([real] ls) -> [real]:
    return ls

method main(System.Console sys) -> void:
    [int] ls = [1,2,3]
    assume f(([real]) ls) == [1.0,2.0,3.0]