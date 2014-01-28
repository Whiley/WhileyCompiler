import whiley.lang.System

function f(any x) => !null:
    if x is null:
        return 1
    else:
        return x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f([1, 2, 3])))
