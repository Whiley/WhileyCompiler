import whiley.lang.System

function f(int b) => int:
    return b + 1

method main(System.Console sys) => void:
    b = f(10)
    sys.out.println(Any.toString(b))
