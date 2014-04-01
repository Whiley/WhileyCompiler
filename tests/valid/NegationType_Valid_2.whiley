import whiley.lang.System

function f(int x) => !null:
    return x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
