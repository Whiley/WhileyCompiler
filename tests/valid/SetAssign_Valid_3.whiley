

function f({int} xs) -> int:
    int r = 0
    for x in xs:
        r = r + x
    return r

public export method test() -> void:
    {int} ys = {1, 2, 3, 4, 5, 6, 7, 8, 9}
    {int} zs = ys
    assume f(zs) == 45
