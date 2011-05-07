define Link as null | [Link]

int maxDepth(Link l):
    if l ~= [Link]:
        r = 0
        for i in l:
            t = maxDepth(i)
            if t > r:
                r = t
        return r + 1
    else:
        return 0    

void System::main([string] args):
    l1 = null
    l2 = [l1]
    l3 = [l2]
    l4 = [l3]
    
    out<->println(str(maxDepth(l1)))
    out<->println(str(maxDepth(l2)))
    out<->println(str(maxDepth(l3)))
    out<->println(str(maxDepth(l4)))
