

type sr6nat is (int n) where n > 0

type sr6tup is {sr6nat f, int g} where g > f

public export method test() -> void:
    sr6tup x = {f: 1, g: 5}
    x.f = 2
    assert x == {f: 2, g: 5}
