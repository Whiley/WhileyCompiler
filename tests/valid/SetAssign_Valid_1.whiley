

function f({int} xs) -> int
requires |xs| > 0:
    int r = 0
    for x in xs:
        r = r + x
    return r

public export method test() -> void:
    {int} ys = {1, 2, 3}
    {int} zs = ys
    assume f(zs) == 6
