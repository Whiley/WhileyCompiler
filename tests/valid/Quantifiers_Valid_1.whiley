

function f([int] xs) -> [int]
requires no { w in xs | w < 0 }:
    return xs

public export method test() -> void:
    [int] ys = [1, 2, 3]
    [int] zs = ys
    assume f(zs) == [1,2,3]
    assert ys == [1,2,3]
