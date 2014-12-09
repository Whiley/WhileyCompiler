import * from whiley.lang.*

method main(System.Console sys) -> void:
    [int] xs = [1, 2, 3]
    if 1.23 in xs:
        sys.out.println(Any.toString(1))
