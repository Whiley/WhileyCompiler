

type dr2point is {real y, real x}

public export method test() :
    dr2point p = {y: 2.23, x: 1.0}
    assert p == {x:1.0, y:2.23}

