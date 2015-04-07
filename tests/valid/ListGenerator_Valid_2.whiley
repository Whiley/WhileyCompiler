import whiley.lang.*

method f(System.Console sys, [int] x) -> void:
    int z = |x|
    sys.out.println(z)
    sys.out.println(x[z - 1])

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    f(sys, arr)
