
type nat is (int n) where n >= 0

function extract([int] ls) -> [int]:
    int i = 0
    [int] r = []
    //
    while i < |ls|:
        r = r ++ [ls[i]]
        i = i + 1
    //
    return r
