import whiley.lang.System

function f(int | bool x) => int:
    if x is int:
        return x
    else:
        return 1

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(true)))
    sys.out.println(Any.toString(f(123)))
