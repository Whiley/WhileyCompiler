import whiley.lang.System

function f([bool] x) => string
requires (|x| > 0) && x[0]:
    return Any.toString(x)

method main(System.Console sys) => void:
    sys.out.println(f([true]))
    sys.out.println(f([true, false]))
    sys.out.println(f([true, false, true]))
