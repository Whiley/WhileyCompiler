

type nat is (int x) where x >= 0

function extract(int[] ls) -> nat[]:
    int i = 0
    int[] rs = [0;|ls|]
    while i < |ls| 
        where i >= 0
        where |rs| == |ls|
        where no { j in 0..|rs| | rs[j] < 0 }:
        //
        if ls[i] >= 0:
            rs[i] = ls[i]
        i = i + 1
    return rs

public export method test() :
    int[] rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    assume rs ==       [ 0,  0, 1, 2,   0, 3, 2345, 4, 5]
