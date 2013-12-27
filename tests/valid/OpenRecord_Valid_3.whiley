import println from whiley.lang.System

define Point as {int x, int y, ...}
define VecPoint as [Point] | Point

int sum(VecPoint vp):
    if vp is Point:
        return vp.x + vp.y
    else:
        r = 0
        for p in vp:
            r = r + sum(p)
        return r

void ::main(System.Console sys):
    vp = {x:1, y:2}
    sys.out.println(sum(vp))
    vp = [{x:1, y:2},{x:1, y:2}]
    sys.out.println(sum(vp))
