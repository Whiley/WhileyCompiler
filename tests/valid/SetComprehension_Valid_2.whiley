import whiley.lang.System

function f({int} xs) => string:
    return Any.toString(xs)

method main(System.Console sys) => void:
    {int} ys = {1, 2, 3}
    {int} zs = { z | z in ys, z > 1 }
    sys.out.println(f(zs))
