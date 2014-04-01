import whiley.lang.System

method main(System.Console sys) => void:
    [bool] ls = [true, false, true]
    sys.out.println(Any.toString(ls))
    bool x = ls[0]
    sys.out.println(Any.toString(x))
    ls[0] = false
    sys.out.println(Any.toString(ls))
