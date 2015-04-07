import whiley.lang.*

function f([bool] x) -> [bool]
requires (|x| > 0) && x[0]:
    return x

method main(System.Console sys) -> void:
    sys.out.println(f([true]))
    sys.out.println(f([true, false]))
    sys.out.println(f([true, false, true]))
