import * from whiley.lang.*

function f([string] args) -> [string]:
    return args

method main(System.Console sys) -> void:
    [int] l = [1, 2, 3]
    [string|int] r = sys.args ++ l
    f(r)
    sys.out.println(Any.toString(r))
