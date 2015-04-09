import whiley.lang.*

type sr3nat is (int n) where n > 0

method main(System.Console sys) -> void:
    [int] x = [2]
    x[0] = 1
    assert x == [1]
