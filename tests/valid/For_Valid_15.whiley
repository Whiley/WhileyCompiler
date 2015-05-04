

type nat is (int x) where x >= 0

public export method test() -> void:
    [int] xs = [1, 2, 3]
    int r = 0
    for x in xs where r >= 0:
        r = r + x
    assume r == 6
