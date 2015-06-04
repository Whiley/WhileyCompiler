

type point is {
    int x,
    int y
} where x > 0 && y > 0

public export method test() -> void:
    point p = {y: 1, x: 1}
    assert p.y == 1
    assert p.x == 1
