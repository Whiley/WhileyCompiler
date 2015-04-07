type point is {int y, int x} where (x > 0) && (y > 0)

function f(point p) -> point:
    return p

method main():
    point p = {y: 1, x: -1}
    f(p)
