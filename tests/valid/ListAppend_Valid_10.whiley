

function f([int] xs) -> int
requires no { x in xs | x < 0 }:
    return |xs|

public export method test() -> void:
    [int] left = [1, 2, 3]
    [int] right = [5, 6, 7]
    int r = f(left ++ right)
    assume r == 6
