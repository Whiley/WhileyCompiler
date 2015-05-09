

type Point is {int y, int x} where x > 0 && y > 0

function f(Point x) -> Point:
    return x

public export method test() -> void:
    Point p = f({y: 1, x: 1})
    assume p == {y: 1, x: 1}
