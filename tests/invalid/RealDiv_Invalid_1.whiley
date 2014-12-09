import * from whiley.lang.*

method main(System.Console sys) -> void:
    [int] ls = [1, 2, 3]
    real x = 2.0 / 1.0
    sys.out.println(Any.toString(ls)[x])
