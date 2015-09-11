
function extract(int[] ls) -> int[]:
    int i = 0
    int[] r = [0; 0]
    //
    while i < |ls| where |r| < 2:
        r[i] = ls[i]
        i = i + 1
    return r
