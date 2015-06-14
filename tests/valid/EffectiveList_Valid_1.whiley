

type rec is {int y, int x}

function f([int] xs) -> [bool | null]:
    [bool|null] r = []
    int i = 0
    while i < |xs|:
        if xs[i] < 0:
            r = r ++ [true]
        else:
            r = r ++ [null]
        i = i + 1
    return r

public export method test() -> void:
    [int] e = []
    assume f(e) == []
    e = [1, 2, 3, 4]
    assume f(e) == [null,null,null,null]
