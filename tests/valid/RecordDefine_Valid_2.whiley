import whiley.lang.System

type point is {int y, int x}

function f(point x) => point:
    return x

method main(System.Console sys) => void:
    p = f({y: 1, x: 1})
    sys.out.println(Any.toString(p))
