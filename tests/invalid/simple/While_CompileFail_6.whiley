[int] extract([int] ls):
    int i = 0
    [int] r = [1]
    // now do the reverse!
    while i < |ls| where |r| > 0:
        r = []
        i = i + 1
    return r

void System::main([string] args):
    [int] rs = extract([-2,-3,1,2,-23,3,2345,4,5])
    print str(rs)
