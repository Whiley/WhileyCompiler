

type Point is {int y, int x, ...}

type VecPoint is [Point] | Point

function sum(VecPoint vp) -> int:
    if vp is Point:
        return vp.x + vp.y
    else:
        int r = 0
        int i = 0
        while i < |vp|:
            r = r + sum(vp[i])
            i = i + 1
        return r

public export method test() -> void:
    VecPoint vp = {y: 2, x: 1}
    assume sum(vp) == 3
    vp = [{y: 2, x: 1}, {y: 4, x: -1}]
    assume sum(vp) == 6
