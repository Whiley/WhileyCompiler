import whiley.lang.*

method main(System.Console sys) -> void:
    [int] l = [1, 2, 3]
    [real] r = [4.23, 5.5]
    r = r ++ (([real]) l)
    sys.out.println(r)
