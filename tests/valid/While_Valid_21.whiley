

type nat is (int x) where x >= 0

function create(nat count, int value) -> (int[] result)
// Returned list must have count elements
ensures |result| == count:
    //
    int[] r = [0; count]
    int i = 0
    while i < count where i <= count && i >= 0 && count == |r|:
        r[i] = value
        i = i + 1
    return r

public export method test() :
    assume create(3, 3) == [3,3,3]
    assume create(2, 2) == [2,2]
    assume create(2, 1) == [1,1]
    assume create(1, 1) == [1]
    assume create(0, 0) == [0;0]
