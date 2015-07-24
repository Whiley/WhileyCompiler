

type nat is (int x) where x >= 0

function extract([int] ls) -> [nat]:
    int i = 0
    [int] rs = []
    while i < |ls| 
        where i >= 0
        where no { j in 0..|rs| | rs[j] < 0 }:
        //
        if ls[i] >= 0:
            rs = rs ++ [ls[i]]
        i = i + 1
    return rs

public export method test() -> void:
    [int] rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    assume rs == [1, 2, 3, 2345, 4, 5]
