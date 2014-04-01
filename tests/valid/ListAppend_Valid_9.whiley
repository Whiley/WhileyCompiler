import whiley.lang.System

method main(System.Console sys) => void:
    [int] l = [1, 2, 3]
    [real] r = [4.23, 5.5]
    r = r ++ l
    sys.out.println(Any.toString(r))
