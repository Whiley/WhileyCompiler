import * from whiley.lang.*

function f({int} xs) => int:
    return |xs|

method main(System.Console sys) => void:
    xs = {{1}, {1, 2, 3}}
    zs = { {y: ys, x: x} | x in xs, ys in x }
    f(zs)
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
