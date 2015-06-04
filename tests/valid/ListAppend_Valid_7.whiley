

public export method test() -> void:
    [int] left = [1, 2]
    [int] right = [3, 4]
    [int] r = left ++ right
    assert r == [1,2,3,4]
