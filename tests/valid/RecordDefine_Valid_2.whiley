

type Point is {int y, int x}

function f(Point x) -> Point:
    return x

public export method test() :
    Point p = f({y: 1, x: 1})
    assume p == {y: 1, x: 1}
