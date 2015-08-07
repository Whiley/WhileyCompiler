function selectOver([int] xs) -> ([int] ys)
ensures |ys| <= |xs|
ensures no { i in 0..|ys| | ys[i] < 0 }:
    //
    int i = 0
    int count = 0
    while i < |xs| where i >= 0 && count >= 0:
        if xs[i] >= 0:
            count = count + 1
        i = i + 1
    //
    [int] zs = [0; count]
    i = 0
    int j = 0
    while i < |xs| && j < |zs|
        where i >= 0 && j >= 0
        where all { k in 0 .. |zs| | zs[k] >= 0 }:
        if xs[i] >= 0:
            zs[j] = xs[i]
            j = j + 1
        i = i + 1
    //
    return zs

public export method test() -> void:
    [int] a1 = selectOver([1, -2, 3, 4])
    [int] a2 = selectOver([1, -2, -3, 4])
    assume a1 == [1,3,4]
    assume a2 == [1,4]
