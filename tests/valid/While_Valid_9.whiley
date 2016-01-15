

function extract(int[] ls) -> (int[] result)
// Returned list cannot be empty
ensures |result| > 0:
    int i = 0
    int[] r = [0; |ls| + 1]
    while i < |ls| where i >= 0 && |r| == |ls| + 1:
        r[i] = 1
        i = i + 1
    return r

public export method test() :
    int[] rs = extract([1, 2, 3, 4, 5, 6, 7])
    assume rs == [1, 1, 1, 1, 1, 1, 1, 0]
    rs = extract([0;0])
    assume rs == [0]
