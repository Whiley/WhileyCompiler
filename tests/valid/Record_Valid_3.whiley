type Point is { int x, int y }

function fromXY(int x, int y) -> (Point[] rs):
    return [Point{x:x, y:y}, Point{x:x, y:y}]

public export method test():
    Point[] ps = fromXY(1,2)
    //
    assert ps[0] == ps[1]
    //
    assert ps[0].x == 1 && ps[0].y == 2