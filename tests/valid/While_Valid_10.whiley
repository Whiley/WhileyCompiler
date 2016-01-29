

function extract(int[] ls) -> int[]:
    int i = 0
    int[] r = [0; |ls|]
    while i < |r| where i >= 0:
        r[i] = 1
        i = i + 1
    return r

public export method test() :
    int[] rs = extract([1, 2, 3, 4, 5, 6, 7])
    assume rs == [1, 1, 1, 1, 1, 1, 1]
    rs = extract([0;0])
    assume rs == [0;0]
