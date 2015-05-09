

type sr3nat is int

public export method test() -> void:
    [sr3nat] x = [1,2]
    x[0] = 2
    assert x == [2,2]
