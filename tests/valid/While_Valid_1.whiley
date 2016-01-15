

function reverse(int[] ls) -> int[]:
    int i = |ls|
    int[] r = [0; |ls|]
    while i > 0 where i <= |ls| && |r| == |ls|:
        int item = ls[|ls|-i]
        i = i - 1
        r[i] = item
    return r

public export method test() :
    int[] rs = reverse([1, 2, 3, 4, 5])
    assume rs == [5,4,3,2,1]
