type Point is { int x, int y }

function fromXY(bool x, int y) -> (Point p):
    return Point{x:x, y:y}
