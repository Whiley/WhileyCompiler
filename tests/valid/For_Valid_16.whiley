

type nat is int

public export method test() -> void:
    [int] xs = [1, 2, 3]
    int r = 0
    for x in xs:
        r = r + x
    assume r == 6
