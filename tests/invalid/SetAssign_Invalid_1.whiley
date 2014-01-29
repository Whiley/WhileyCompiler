
function f({int} xs) => void
requires |xs| > 4:
    debug Any.toString(xs)

method main(System.Console sys) => void:
    {int} ys
    {int} zs
    //
    if |sys.args| > 1:
        ys = {1, 2, 3}
    else:
        ys = {1, 2}
    zs = ys
    f(zs)
