import whiley.lang.*

method main(System.Console sys) -> void:
    [int] left = [1, 2]
    [int] right = [3, 4]
    [int] r = left ++ right
    sys.out.println(r)
