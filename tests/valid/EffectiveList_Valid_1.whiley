

type rec is {int y, int x}

function f([int] xs) -> [bool | null]:
    [bool|null] r = []
    for x in xs:
        if x < 0:
            r = r ++ [true]
        else:
            r = r ++ [null]
    return r

public export method test() -> void:
    [int] e = []
    assume f(e) == []
    e = [1, 2, 3, 4]
    assume f(e) == [null,null,null,null]
