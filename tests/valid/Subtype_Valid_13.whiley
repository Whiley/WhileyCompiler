

type sr6nat is int

type sr6tup is {sr6nat f, int g}

public export method test() :
    sr6tup x = {f: 1, g: 5}
    x.g = 2
    assert x == {f: 1, g: 2}
