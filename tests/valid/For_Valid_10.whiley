

type listset is [int] | {int}

function f(listset ls) -> int:
    int r = 0
    for l in ls:
        r = r + l
    return r

public export method test() -> void:
    [int] ls = [1, 2, 3, 4, 5]
    assume f(ls) == 15
    {int} ss = {10, 20, 30, 40, 50}
    assume f(ss) == 150
