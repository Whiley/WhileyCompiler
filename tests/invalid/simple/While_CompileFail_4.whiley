int sum([int] ls):
    int i = 0
    int|[void] r = 0
    // now do the reverse!
    while i < |ls|:
        r = r + ls[i]
        r = []
        i = i + 1
    return r

void System::main([string] args):
    int rs = sum([-2,-3,1,2,-23,3,2345,4,5])
    print str(rs)
