

function sum(int[] xs) -> int
requires |xs| > 0:
    //
    int i = 0
    int r = 0
    do:
       r = r + xs[i]
       i = i + 1
    while i < |xs| where i >= 0
    //
    return r

public export method test() :
    assume sum([1]) == 1
    assume sum([1, 2]) == 3
    assume sum([1, 2, 3]) == 6

