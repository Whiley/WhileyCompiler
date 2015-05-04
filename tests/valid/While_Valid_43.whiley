

function add([int] items, int n) -> [int]
requires n > 0:
    //
    int i = 0
    [int] oitems = items
    //
    while i < |items|
    //
    where i >= 0 && i <= |items| && |items| == |oitems|
    // Elements upto but not including i are zeroed
    where all { j in 0 .. i | oitems[j] < items[j] }:
        //
        items[i] = oitems[i] + n
        i = i + 1
    //
    return items


public export method test():
    [int] ls = [1,2,3,4]
    assume add(ls,1) == [2, 3, 4, 5]
    assume add(ls,11) == [12, 13, 14, 15]
