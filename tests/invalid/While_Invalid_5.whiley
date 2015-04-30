
function extract([int] ls) -> [int]:
    int i = 0
    int r = [1]
    //
    while i < |ls| where |r| > 0:
        r = []
        i = i + 1
    //
    return r
