
function extract([int] ls) -> [int]:
    int i = 0
    [int] r = []
    //
    while i < |ls| where |r| < 2:
        r = r ++ [ls[i]]
        i = i + 1
    return r
