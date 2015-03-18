type Point is {
    int x,
    int y,
    function toString(Point) -> ASCII.string,
    function getX(Point) -> int
}

function toString(Point p) -> ASCII.string:
    return "(" ++ Any.toString(p.x) ++ "," ++ Any.toString(p.y) ++ ")"

function getX(Point p) -> int:
    return p.x

function Point(int x, int y) -> Point:
    return {
        x: x,
        y: y,
        toString: &toString,
        getX: &getX
    }

method main(System.Console console):
    Point p = Point(1,2)
    console.out.println_s("getX() = " ++ Any.toString(p.getX(p)))
    console.out.println_s("toString() = " ++ p.toString(p))