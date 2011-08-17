int sum([int] ls):
    i = 0
    r = 0
    // now do the reverse!
    while i < |ls|:
        r = r + ls[i]
        r = []
        i = i + 1
    return r

void System::main([string] args):
    rs = sum([-2,-3,1,2,-23,3,2345,4,5])
    debug str(rs)
