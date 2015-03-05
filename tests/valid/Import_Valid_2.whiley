import whiley.lang.System

function f(ASCII.string x) -> int:
    return x[0]

public method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f("1")))
