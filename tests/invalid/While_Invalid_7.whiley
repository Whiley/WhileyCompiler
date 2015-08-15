
type nat is (int n) where n >= 0

function extract(int[] ls) -> int[]:
    int i = 0
    int[] r = [0; |ls|]
    //
    while i < |ls|:
        r[i] = ls[i]
        i = i + 1
    //
    return r
