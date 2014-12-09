import * from whiley.lang.*

function sum([int] ls) -> int:
    int i = 0
    int r = 0
    //
    while i < |ls|:
        r = r ++ [ls[i]]
        r = []
        i = i + 1
    //
    return r

method main(System.Console sys) -> void:
    rs = sum([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    debug Any.toString(rs)
