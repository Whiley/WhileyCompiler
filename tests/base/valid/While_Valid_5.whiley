[int] extract([int] ls):
    i = 0
    r = [1]
    // now do the reverse!
    while i < |ls|:
        r = r + [1]
        i = i + 1
    return r

void System::main([string] args):
    rs = extract([1,2,3,4,5,6,7])
    this.out.println(str(rs))
    rs = extract([])
    this.out.println(str(rs))
