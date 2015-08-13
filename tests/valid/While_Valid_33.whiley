

function sum(int[] items) -> (int r)
// Every element of items must be non-negative
requires all { i in 0..|items| | items[i] >= 0 }
// Return value must be non-negative
ensures r >= 0:
    //
    int i = 0
    int r = 0
    while i < |items| where i >= 0 && r >= 0:
        r = r + items[i]
        i = i + 1
    //
    return r

public export method test():
    assume sum([0;0]) == 0
    assume sum([0]) == 0
    assume sum([1]) == 1
    assume sum([1,2,3]) == 6
