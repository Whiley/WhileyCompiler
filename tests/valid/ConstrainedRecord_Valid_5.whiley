

type point is {int y, int x}

public export method test() :
    point p = {y: 1, x: 1}
    assert p.y == 1
    assert p.x == 1
