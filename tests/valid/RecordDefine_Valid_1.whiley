import whiley.lang.*

type Point is {int y, int x} where x > 0 && y > 0

function f(Point x) -> Point:
    return x

method main(System.Console sys) -> void:
    Point p = f({y: 1, x: 1})
    sys.out.println(p)
