import * from whiley.lang.*

function f({real} xs) -> int:
    return |xs|

method main(System.Console sys) -> void:
    ys = {{1, 2}, {1}}
    xs = {1, 2, 3, 4}
    x = f(xs & ys)
    sys.out.println(Any.toString(x))
