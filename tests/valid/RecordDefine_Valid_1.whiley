import println from whiley.lang.System

type point is {int y, int x} where x > 0 && y > 0

function f(point x) => point:
    return x

method main(System.Console sys) => void:
    p = f({y: 1, x: 1})
    sys.out.println(Any.toString(p))
