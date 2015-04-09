import whiley.lang.*

method f([int] x) -> void:
    int z = |x|
    assume x[z - 1] == 3

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    f(arr)
