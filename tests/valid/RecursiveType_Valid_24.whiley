import whiley.lang.*

type Link is null | [Link]

function maxDepth(Link l) -> int:
    if l is [Link]:
        int r = 0
        for i in l:
            int t = maxDepth(i)
            if t > r:
                r = t
        return r + 1
    else:
        return 0

method main(System.Console sys) -> void:
    Link l1 = null
    Link l2 = [l1]
    Link l3 = [l2]
    Link l4 = [l3]
    sys.out.println(maxDepth(l1))
    sys.out.println(maxDepth(l2))
    sys.out.println(maxDepth(l3))
    sys.out.println(maxDepth(l4))
