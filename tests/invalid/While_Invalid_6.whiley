import * from whiley.lang.*

function extract([int] ls) => [void]:
    i = 0
    r = [1]
    while i < |ls| where |r| < 2:
        r = []
    return r
