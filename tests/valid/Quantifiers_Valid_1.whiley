import whiley.lang.*

function f({int} xs) -> {int}
requires no { w in xs | w < 0 }:
    return xs

method main(System.Console sys) -> void:
    {int} ys = {1, 2, 3}
    {int} zs = ys
    sys.out.println(f(zs))
