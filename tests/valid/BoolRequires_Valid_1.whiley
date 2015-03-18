import whiley.lang.System

function f([bool] x) -> ASCII.string
requires (|x| > 0) && x[0]:
    return Any.toString(x)

method main(System.Console sys) -> void:
    sys.out.println_s(f([true]))
    sys.out.println_s(f([true, false]))
    sys.out.println_s(f([true, false, true]))
