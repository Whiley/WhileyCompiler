import whiley.lang.*

type IntList is int | [int]

method f(int y) -> int:
    return y

method g([int] z) -> [int]:
    return z

method main(System.Console sys) -> void:
    IntList x = 123
    assume f(x) == 123
    x = [1, 2, 3]
    assume g(x) == [1,2,3]
