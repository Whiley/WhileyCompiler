import * from whiley.lang.*

function extract([int] ls) -> [void]:
    int i = 0
    int r = [1]
    //
    while i < |ls| where |r| < 2:
        r = []
    //
    return r
