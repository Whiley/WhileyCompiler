import whiley.lang.System

type Point is {int y, int x}

function f(Point x) => Point:
    return x

method main(System.Console sys) => void:
    Point p = f({y: 1, x: 1})
    sys.out.println(Any.toString(p))
