import whiley.lang.System

function f(Int.nat x) => int:
    return x - 1

public method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
