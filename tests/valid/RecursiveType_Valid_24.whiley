

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

public export method test() -> void:
    Link l1 = null
    Link l2 = [l1]
    Link l3 = [l2]
    Link l4 = [l3]
    assume maxDepth(l1) == 0
    assume maxDepth(l2) == 1
    assume maxDepth(l3) == 2
    assume maxDepth(l4) == 3
