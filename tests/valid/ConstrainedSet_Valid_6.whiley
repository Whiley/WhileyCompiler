import println from whiley.lang.System

type posints is {int}

function f(posints x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    xs = {1, 2, 3}
    sys.out.println(f(xs))
