import * from whiley.lang.*

[void] extract([int] ls):
    i = 0
    r = [1]
    // now do the reverse!
    while i < |ls| where |r| < 2:
        r = []
    return r
