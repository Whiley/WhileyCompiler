import whiley.lang.*
import * from whiley.lang.Int

function extract([int] ls) -> [nat]:
    int i = 0
    [int] r = []
    while i < |ls| where (i >= 0) && no { x in r | x < 0 }:
        if ls[i] < 0:
            r = r ++ [-ls[i]]
        else:
            r = r ++ [ls[i]]
        i = i + 1
    return r

method main(System.Console sys) -> void:
    [int] rs = extract([-1, 2, 3, -4, 5, 6, 7, 23987, -23897, 0, -1, 1, -2389])
    sys.out.println(rs)
    rs = extract([])
    sys.out.println(rs)
