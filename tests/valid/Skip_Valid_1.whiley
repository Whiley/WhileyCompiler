import whiley.lang.System

function f(int x) => int:
    if x > 0:
        skip
    else:
        return -1
    return x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(-10)))
