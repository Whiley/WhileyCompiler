
function extract(int[] ls, int[] r) -> int[] requires |ls| == |r|:
    int i = 0
    //
    while i < |ls| where |r| > 0:
        r[i] = ls[i]
        i = i + 1
    //
    return r
