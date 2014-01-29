import * from whiley.lang.*

type IntList is int | [int]

function f([int] xs) => int:
    return |xs|

method main(System.Console sys) => void:
    int x
    int y
    //
    if |sys.args| == 0:
        x = 1
        y = x
    else:
        sys.out.println(Any.toString(y))
        x = [1, 2, 3]
        ys = x
    //
    int z = f(x)
    sys.out.println(Any.toString(z))
