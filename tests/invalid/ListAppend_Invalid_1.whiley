import * from whiley.lang.*

function f([string] r) -> int:
    return |r|

method main(System.Console sys) -> void:
    [string|int] r = sys.args ++ [1]
    f(r)
    sys.out.println(Any.toString(r))
