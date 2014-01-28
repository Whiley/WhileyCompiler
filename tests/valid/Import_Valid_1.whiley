import whiley.lang.System

function f(int x) => Int.nat:
    if x < 0:
        return 0
    else:
        return x

public method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
