import whiley.lang.*

method main(System.Console sys) -> void:
    [int] r = [1, 2] ++ [3, 4]
    sys.out.println(r)
