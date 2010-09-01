[int] reverse([int] ls):
    int i = |ls|
    [int] r = []
    // now do the reverse!
    while i > 0:
        i = i - 1
        r = r + [ls[i]]
    return r

void System::main([string] args):
    [int] rs = reverse([1,2,3,4,5])
    print str(rs)
