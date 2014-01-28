import whiley.lang.System

type Link is null | [Link]

function maxDepth(Link l) => int:
    if l is [Link]:
        r = 0
        for i in l:
            t = maxDepth(i)
            if t > r:
                r = t
        return r + 1
    else:
        return 0

method main(System.Console sys) => void:
    l1 = null
    l2 = [l1]
    l3 = [l2]
    l4 = [l3]
    sys.out.println(Any.toString(maxDepth(l1)))
    sys.out.println(Any.toString(maxDepth(l2)))
    sys.out.println(Any.toString(maxDepth(l3)))
    sys.out.println(Any.toString(maxDepth(l4)))
