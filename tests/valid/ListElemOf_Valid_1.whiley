import whiley.lang.*

method main(System.Console sys) -> void:
    [int] xs = [1, 2, 3, 3, 3, 4]
    if 1 in xs:
        sys.out.println(1)
    if 5 in xs:
        sys.out.println(5)
