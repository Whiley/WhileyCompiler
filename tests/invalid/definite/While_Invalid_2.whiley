define nat as int where $ >= 0

[nat] extract([int] ls):
    int i = 0
    [nat] r = []
    // now do the reverse!
    while i < |ls|:
        r = r + [ls[i]]
        i = i + 1
    return r

void System::main([string] args):
    [nat] rs = extract([-2,-3,1,2,-23,3,2345,4,5])
    print str(rs)
