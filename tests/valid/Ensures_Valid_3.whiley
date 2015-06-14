function pred([int] xs) -> (bool b)
ensures b ==> no { z in xs | z < 0 }:
    //
    [int] zs = []
    int i = 0
    while i < |xs|:
        if xs[i] < 0:
            zs = zs ++ [xs[i]]
        i = i + 1
    return |zs| == 0

function countOver([int] xs, int y) -> int
requires pred(xs):
    [int] tmp = []
    int i = 0
    while i < |xs|:
        if xs[i] > y:
            tmp = tmp ++ [xs[i]]
        i = i + 1
    return |tmp|

public export method test() -> void:
    int c1 = countOver([1, 2, 3, 4], 1)
    int c2 = countOver([1, 2, 3, 4], 3)
    assume c1 == 3
    assume c2 == 1
