[int] reverse([int] ls):
    i = |ls|
    r = []
    // now do the reverse!
    while i > 0:
        i = i - 1
        r = r + [ls[i]]
    return r

void System::main([string] args):
    rs = reverse([1,2,3,4,5])
    this.out.println(str(rs))
