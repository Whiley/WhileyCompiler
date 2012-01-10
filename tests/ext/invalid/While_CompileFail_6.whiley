import * from whiley.lang.*

[int] extract([int] ls):
    i = 0
    r = [1]
    // now do the reverse!
    while i < |ls| where |r| > 0:
        r = []
        i = i + 1
    return r

void ::main(System.Console sys,[string] args):
    rs = extract([-2,-3,1,2,-23,3,2345,4,5])
    debug Any.toString(rs)
