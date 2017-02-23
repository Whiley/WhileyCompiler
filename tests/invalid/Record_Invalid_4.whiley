type Point is { int x, int y }
type Dimension is { int width, int height }

function fromXY(int x, int y) -> (Dimension d):
    return Point{x:x, y:y}
