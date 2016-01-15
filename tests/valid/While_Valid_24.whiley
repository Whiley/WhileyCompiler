

function indexOf(int[] xs, int x) -> (int|null result)
// Either result is null, or gives the index of x in xs
ensures result is null || xs[result] == x:
    //
    int i = 0
    while i < |xs| where i >= 0:
        if xs[i] == x:
            return i
        i = i + 1
    return null

public export method test() :
    assume indexOf([1, 2, 3], 1) == 0
    assume indexOf([1, 2, 3], 0) == null
