import whiley.lang.*

function f({int} xs) -> int
requires |xs| > 0:
    int r = 0
    for x in xs:
        r = r + x
    return r

method main(System.Console sys) -> void:
    {int} ys = {1, 2, 3}
    {int} zs = ys
    sys.out.println(f(zs))
