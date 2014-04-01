import whiley.lang.System

function f({int} xs) => string:
    return Any.toString(xs)

method main(System.Console sys) => void:
    {int} ys = {1, 2, 3, 4, 5, 6, 7, 8, 9}
    {int} zs = ys
    sys.out.println(f(zs))
