import * from whiley.lang.*

function f([string] args) => [string]:
    return r

method main(System.Console sys) => void:
    l = [1, 2, 3]
    r = args ++ l
    f(r)
    sys.out.println(Any.toString(r))
