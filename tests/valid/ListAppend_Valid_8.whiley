

public export method test() -> void:
    [int] left = [1, 2]
    [int] right = [3, 4]
    [int] r = left ++ right
    left = left ++ [6]
    assert r == [1,2,3,4]
    assert left == [1,2,6]
