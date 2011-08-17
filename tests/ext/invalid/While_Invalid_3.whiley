[int] extract([int] ls,[int] r):
    i = 0
    // now do the reverse!
    while i < |ls| where |r| > 0:
        r = r + [ls[i]]
        i = i + 1
    return r

void System::main([string] args):
    rs = extract([-2,-3,1,2,-23,3,2345,4,5],[1])
    debug str(rs)
    rs = extract([-2,-3,1,2,-23,3,2345,4,5],[])
    debug str(rs)
