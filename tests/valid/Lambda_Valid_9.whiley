

type Point is {
    int x,
    int y,
    function toString(Point)->(int),
    function getX(Point)->(int)
}

function toString(Point p) -> int:
    return p.x + p.y

function getX(Point p) -> int:
    return p.x

function Point(int x, int y) -> Point:
    return {
        x: x,
        y: y,
        toString: &toString,
        getX: &getX
    }

public export method test():
    Point p = Point(1,2)
    assume p.getX(p) == 1
    assume p.toString(p) == 3
