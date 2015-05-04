

function zeroOut([int] items) -> [int]:
    int i = 0
    [int] oitems = items
    //
    while i < |items|
    //
    where i >= 0 && i <= |items| && |items| == |oitems|
    // Elements upto but not including i are zeroed
    where all { j in 0 .. i | items[j] == 0 }:
        //
        items[i] = 0
        i = i + 1
    //
    return items


public export method test():
    assume zeroOut([]) == []
    assume zeroOut([1]) == [0]
    assume zeroOut([1,2]) == [0,0]
    assume zeroOut([1,2,3]) == [0,0,0]
    assume zeroOut([1,2,3,4]) == [0,0,0,0]
