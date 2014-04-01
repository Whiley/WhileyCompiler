
type point is {int y, int x} where (x > 0) && (y > 0)

function f(point p) => point:
    return p

method main(System.Console sys) => void:
    point p = {y: 1, x: -1}
    p = f(p)
    debug Any.toString(p)
