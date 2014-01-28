import whiley.lang.System

function f({int} xs) => string
requires |xs| > 0:
    return Any.toString(xs)

method main(System.Console sys) => void:
    ys = {1, 2, 3}
    zs = { z | z in ys, z > 1 }
    sys.out.println(f(zs))
