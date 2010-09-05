[int] extract([int] ls) ensures |$| > 0:
    int i = 0
    [int] r = [1]
    // now do the reverse!
    while i < |ls| where |r| > 0:
        r = r + [1]
        i = i + 1
    return r

void System::main([string] args):
    [int] rs = extract([1,2,3,4,5,6,7])
    print str(rs)
    rs = extract([])
    print str(rs)
