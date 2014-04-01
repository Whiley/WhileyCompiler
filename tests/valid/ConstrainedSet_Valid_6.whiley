import whiley.lang.System

type posints is {int}

function f(posints x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    posints xs = {1, 2, 3}
    sys.out.println(f(xs))
