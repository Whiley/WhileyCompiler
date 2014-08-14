type Point is {
    int x,
    int y,
    function toString(Point) => string,
    function getX(Point) => int
}

function toString(Point p) => string:
    return "(" ++ p.x ++ "," ++ p.y ++ ")"

function getX(Point p) => int:
    return p.x

function Point(int x, int y) => Point:
    return {
        x: x,
        y: y,
        toString: &toString,
        getX: &getX
    }

method main(System.Console console):
    Point p = Point(1,2)
    console.out.println("getX() = " ++ p.getX(p))
    console.out.println("toString() = " ++ p.toString(p))