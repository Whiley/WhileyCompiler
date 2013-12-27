import println from whiley.lang.System

define Link as null | [Link]

int maxDepth(Link l):
    if l is [Link]:
        r = 0
        for i in l:
            t = maxDepth(i)
            if t > r:
                r = t
        return r + 1
    else:
        return 0    

void ::main(System.Console sys):
    l1 = null
    l2 = [l1]
    l3 = [l2]
    l4 = [l3]
    
    sys.out.println(Any.toString(maxDepth(l1)))
    sys.out.println(Any.toString(maxDepth(l2)))
    sys.out.println(Any.toString(maxDepth(l3)))
    sys.out.println(Any.toString(maxDepth(l4)))
