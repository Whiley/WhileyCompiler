

type point is ({
    int x,
    int y
} p) where p.x > 0 && p.y > 0

public export method test() :
    point p = {y: 1, x: 1}
    assert p.y == 1
    assert p.x == 1
