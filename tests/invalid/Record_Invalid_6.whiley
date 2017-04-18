type Point is { int x, int y }

function fromXY(int x, bool y) -> (Point p):
    return Point{x:x, y:y}
