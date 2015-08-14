
type nat is (int n) where n >= 0

function extract(int i, int[] ls) -> int
requires i >= 0:
    //
    int r = 0
    //
    while i < |ls|:
        r = r + ls[i]
        i = i - 1
    //
    return r
