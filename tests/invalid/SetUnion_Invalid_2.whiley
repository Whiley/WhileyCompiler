import * from whiley.lang.*

function f({int} xs) -> int:
    return |xs|

method main(System.Console sys) -> void:
    {real} ys = {1.0234234, 1.12}
    {int} xs = {1, 2, 3, 4}
    f(xs + ys)
    sys.out.println(Any.toString(xs))
