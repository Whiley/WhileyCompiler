import whiley.lang.System

function f(int x) -> ASCII.string:
    if x < 0:
        return Any.toString(0)
    else:
        return Any.toString(x)

public method main(System.Console sys) -> void:
    sys.out.println(f(1))
