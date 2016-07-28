function g() -> (bool x, int r):
    return false, 1

function f() -> int:
    int x
    int y
    x,y = g()
    return 1
