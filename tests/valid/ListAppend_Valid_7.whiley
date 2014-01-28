import whiley.lang.System

method main(System.Console sys) => void:
    left = [1, 2]
    right = [3, 4]
    r = left ++ right
    sys.out.println(Any.toString(r))
