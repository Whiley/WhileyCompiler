
type tup is {int y, int x}

type point is ({int y, int x} r) where (r.x > 0) && (r.y > 0)

function f(point p) -> point:
    return p

public export method test(): 
    tup z = {y: -2, x: 1}
    point p = f((point) z)
