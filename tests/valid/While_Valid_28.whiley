

//
// This example represents the expansion of a do-while loop
//

function sum(int[] xs) -> int
requires |xs| > 0:
    //
    int i = 0
    int r = 0
    //
    assert i >= 0
    r = r + xs[i]
    i = i + 1
    while i < |xs| where i >= 0:
       r = r + xs[i]
       i = i + 1
    //
    return r

public export method test() :
    assume sum([1]) == 1
    assume sum([1, 2]) == 3
    assume sum([1, 2, 3]) == 6



