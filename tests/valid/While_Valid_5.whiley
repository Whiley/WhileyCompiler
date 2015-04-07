import whiley.lang.*

type nat is (int x) where x >= 0

function extract([int] ls) -> [nat]:
    int i = 0
    [int] rs = []
    while i < |ls| where (i >= 0) && no { r in rs | r < 0 }:
        if ls[i] >= 0:
            rs = rs ++ [ls[i]]
        i = i + 1
    return rs

method main(System.Console sys) -> void:
    [int] rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    sys.out.println(rs)
