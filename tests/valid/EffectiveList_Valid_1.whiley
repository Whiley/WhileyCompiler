

type rec is {int y, int x}

function f([int] xs) -> [bool | null]:
    [bool|null] r = [false; |xs|]
    int i = 0
    while i < |xs|
        where i >= 0
        where |xs| == |r|:
        //
        if xs[i] < 0:
            r[i] = true
        else:
            r[i] = null
        i = i + 1
    return r

public export method test() -> void:
    [int] e = []
    assume f(e) == []
    e = [1, 2, 3, 4]
    assume f(e) == [null,null,null,null]
