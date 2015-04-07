
function extract([int] ls, [int] r) -> [int]:
    int i = 0
    //
    while i < |ls| where |r| > 0:
        r = r ++ [ls[i]]
        i = i + 1
    //
    return r
