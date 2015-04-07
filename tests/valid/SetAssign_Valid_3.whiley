import whiley.lang.*

function f({int} xs) -> int:
    int r = 0
    for x in xs:
        r = r + x
    return r

method main(System.Console sys) -> void:
    {int} ys = {1, 2, 3, 4, 5, 6, 7, 8, 9}
    {int} zs = ys
    sys.out.println(f(zs))
