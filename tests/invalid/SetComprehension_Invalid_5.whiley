import * from whiley.lang.*

function f({int} xs) => int:
    return |xs|

method main(System.Console sys) => void:
    {{int}} xs = {{1}, {1, 2, 3}}
    {int} zs = { {y: ys, x: x} | x in xs, ys in x }
    f(zs)
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
