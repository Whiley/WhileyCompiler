

function pred({int} xs) -> (bool b)
ensures b ==> no { z in xs | z < 0 }:
    //
    {int} zs = {}
    for y in xs:
        if y < 0:
            zs = zs + {y}
    return |zs| == 0

function countOver({int} xs, int y) -> int
requires pred(xs):
    {int} tmp = {}
    for x in xs:
        if x > y:
            tmp = tmp + {x}
    return |tmp|

public export method test() -> void:
    int c1 = countOver({1, 2, 3, 4}, 1)
    int c2 = countOver({1, 2, 3, 4}, 3)
    assume c1 == 3
    assume c2 == 1
