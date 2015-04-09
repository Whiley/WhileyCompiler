import whiley.lang.*

type sr3nat is int

method main(System.Console sys) -> void:
    [sr3nat] x = [1,2]
    x[0] = 2
    assert x == [2,2]
