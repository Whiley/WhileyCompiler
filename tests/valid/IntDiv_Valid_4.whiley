import whiley.lang.System

function f(int x) => int:
    return x / 3

public method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(10)))
