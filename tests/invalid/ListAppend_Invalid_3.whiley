
function f([int] xs) -> int
requires no { x in xs | x < 0 }:
    return |xs|

method main(System.Console sys) -> void:
    [int] left
    [int] right = [-1, 0, 1]
    //
    if |sys.args| > 1:
        left = [2, 3, 4]
    else:
        left = [1, 2, 3]
    //
    int r = f(left ++ right)
    //
    debug Any.toString(r)
