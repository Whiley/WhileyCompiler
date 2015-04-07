import whiley.lang.*

method main(System.Console sys) -> void:
    [int] xs = [1, 2, 3]
    for st in xs:
        sys.out.println(st)
