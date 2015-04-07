
function f({int} xs) -> bool
requires |xs| > 4:
    return true

method main(int x) -> void:
    {int} ys
    {int} zs
    //
    if x > 1:
        ys = {1, 2, 3}
    else:
        ys = {1, 2}
    zs = ys
    f(zs)
