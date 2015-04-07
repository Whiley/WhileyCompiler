import whiley.lang.*

method main(System.Console sys) -> void:
    [int] left = [1, 2]
    [int] right = [3, 4]
    [int] r = left ++ right
    left = left ++ [6]
    sys.out.println(left)
    sys.out.println(right)
    sys.out.println(r)
