[int] sort([int] items):
    pivot = |items| / 2
    lhs = sort(items[0..pivot])
    rhs = sort(items[pivot..|items|])
    l,r,i = 0,0,0
    while i < |items|:
        if lhs[l] <= rhs[r]:
            items[i] = lhs[l] 
            l = l + 1
        else:
            items[i] = rhs[r] 
            r = r + 1
        i=i+1
    return items

void System::main([string] args):
    out->println(str(sort([5,4,2,1,7,2,4,5,1])))
    out->println(str(sort([-1,2,0,-3,1,0,3,9,11,6,4,2])))
