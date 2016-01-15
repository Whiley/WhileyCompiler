

function extract(int[] ls) -> (int[] rs)
// Returned list cannot be empty
ensures |rs| > 0:
    //
    int i = 0
    int[] r = [1; |ls| + 1]
    while i < |ls| where |r| == |ls| + 1 && i >= 0:
        r[i] = ls[i]
        i = i + 1
    return r

public export method test() :
    int[] rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    assume rs == [-2, -3, 1, 2, -23, 3, 2345, 4, 5]
