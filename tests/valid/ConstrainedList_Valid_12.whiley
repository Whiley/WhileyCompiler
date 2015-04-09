import whiley.lang.*

function f(int x) -> [int]:
    return [x]

method main(System.Console sys) -> void:
    assume f(0) == [0]
