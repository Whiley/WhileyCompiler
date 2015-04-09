import whiley.lang.*

type point is {int y, int x}

method main(System.Console sys) -> void:
    point p = {y: 1, x: 1}
    assert p.y == 1
    assert p.x == 1
