type nat is (int x) where x >= 0

function extract(int[] ls) -> nat[]:
    int i = 0
    int[] r = [0; |ls|]
    while i < |ls| 
        where i >= 0 && |r| == |ls|
        where all { j in 0..|r| | r[j] >= 0 }:
        //
        if ls[i] < 0:
            r[i] = -ls[i]
        else:
            r[i] = ls[i]
        i = i + 1
    return r

public export method test() :
    int[] rs = extract([-1, 2, 3, -4, 5, 6, 7, 23987, -23897, 0, -1, 1, -2389])
    assume rs == [1, 2, 3, 4, 5, 6, 7, 23987, 23897, 0, 1, 1, 2389]
    rs = extract([0;0])
    assume rs == [0;0]
