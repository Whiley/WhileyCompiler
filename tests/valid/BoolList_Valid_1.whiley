import whiley.lang.*

method main(System.Console sys) -> void:
    [bool] ls = [true, false, true]
    sys.out.println(ls)
    bool x = ls[0]
    sys.out.println(x)
    ls[0] = false
    sys.out.println(ls)
