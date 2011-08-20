import whiley.lang.*:*

[int] extract([int] ls):
    i = 0
    r = []
    // now do the reverse!
    while i < |ls| where |r| < 2:
        r = r + [ls[i]]
        i = i + 1
    return r

void System::main([string] args):
    rs = extract([-2,-3,1,2,-23,3,2345,4,5])
    debug str(rs)
