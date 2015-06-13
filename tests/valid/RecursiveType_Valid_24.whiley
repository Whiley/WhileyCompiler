

type Link is null | [Link]

function maxDepth(Link links) -> int:
    if links is [Link]:
        int r = 0
        int i = 0
        while i < |links|:
            Link l = links[i]
            int t = maxDepth(l)
            if t > r:
                r = t
            i = i + 1
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
