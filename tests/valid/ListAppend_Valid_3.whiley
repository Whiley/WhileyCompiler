

type nat is (int n) where n >= 0

function f([nat] xs, [nat] ys) -> [nat]:
    return xs ++ ys

public export method test() -> void:
    [nat] left = [1, 2, 3]
    [nat] right = [5, 6, 7]
    [nat] r = f(left, right)
    assume r == [1,2,3,5,6,7]
