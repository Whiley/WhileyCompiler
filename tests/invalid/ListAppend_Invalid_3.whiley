
function f([int] xs) -> int
requires no { x in xs | x < 0 }:
    return |xs|

method main(bool flag) -> int:
    [int] left
    [int] right = [-1, 0, 1]
    //
    if flag:
        left = [2, 3, 4]
    else:
        left = [1, 2, 3]
    //
    return f(left ++ right)
