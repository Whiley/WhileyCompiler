import whiley.lang.*

type sr6nat is int

type sr6tup is {sr6nat f, int g}

method main(System.Console sys) -> void:
    sr6tup x = {f: 1, g: 5}
    x.g = 2
    assert x == {f: 1, g: 2}
