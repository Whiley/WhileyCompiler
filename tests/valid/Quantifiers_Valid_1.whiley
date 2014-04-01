import whiley.lang.System

function f({int} xs) => string
requires no { w in xs | w < 0 }:
    return Any.toString(xs)

method main(System.Console sys) => void:
    {int} ys = {1, 2, 3}
    {int} zs = ys
    sys.out.println(f(zs))
