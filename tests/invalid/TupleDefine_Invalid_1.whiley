import * from whiley.lang.*

type point is {real y, real x}

function f(int x) -> int:
    return x

method main(System.Console sys) -> void:
    point p = {y: 2.23, x: 1.0}
    int x = f(p.y)
    sys.out.println(Any.toString(x))
