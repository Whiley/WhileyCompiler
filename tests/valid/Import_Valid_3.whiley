


function toInt([int] ls) -> int:
    int r = 0
    for i in ls:
        r = r + i
    return r

public export method test() -> void:
    [int] ls = [1, 2, 3, 4]
    assume toInt(ls) == 10
