[nat] extract([int] ls):
    int i = 0
    [int] r = []
    // now do the reverse!
    while i < |ls| where no { x in r | x < 0 }:        
        if ls[i] < 0:
            r = r + [-ls[i]]
        else:
            r = r + [ls[i]]
        i = i + 1
    return r

void System::main([string] args):
    [int] rs = extract([-1,2,3,-4,5,6,7,23987,-23897,0,-1,1,-2389])
    print str(rs)
    rs = extract([])
    print str(rs)
