import whiley.lang.System

method main(System.Console sys) => void:
    [int] left = [1, 2]
    [int] right = [3, 4]
    [int] r = left ++ right
    left = left ++ [6]
    sys.out.println(Any.toString(left))
    sys.out.println(Any.toString(right))
    sys.out.println(Any.toString(r))
