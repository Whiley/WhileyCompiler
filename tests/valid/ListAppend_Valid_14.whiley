

type nat is (int n) where n >= 0

function f([int] xs, [int] ys) -> nat:
    return |xs ++ ys|

public export method test() -> void:
    [int] left = [1, 2, 3]
    [int] right = [5, 6, 7]
    int r = f(left, right)
    assume r == 6
