import whiley.lang.*

type sr3nat is int

method main(System.Console sys) -> void:
    [sr3nat] x = [1]
    x[0] = 1
    sys.out.println(x)
