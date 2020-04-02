type nat is (int x) where x >= 0

function sum(nat[] list) -> nat:
    nat r = 0
    int i = 0
    while i < |list| where i >= 0:
        int ith = list[i]
        assume r >= 0        
        r = r + i
        i = i + 1
    return r

public export method test() :
    nat rs = sum([0, 1, 2, 3])
    assume rs == 6
