

type listdict is [int] | {int=>int}

function f(listdict ls) -> int:
    int r = 0
    for l in ls:
        r = r + 1
    return r

public export method test() -> void:
    [int] ls = [1, 2, 3, 4, 5]
    assume f(ls) == 5
    {int=>int} ms = {10=>20, 30=>40}
    assume f(ms) == 2
