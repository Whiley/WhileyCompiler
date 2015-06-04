

type Point is {int y, int x, ...}

type VecPoint is [Point] | Point

function sum(VecPoint vp) -> int:
    if vp is [Point]:
        int r = 0
        for p in vp:
            r = r + sum(p)
        return r
    else:
        return vp.x + vp.y

public export method test() -> void:
    VecPoint vp = {y: 2, x: 1}
    assume sum(vp) == 3
    vp = [{y: 2, x: 1}, {y: 5, x: -10}]
    assume sum(vp) == -2
