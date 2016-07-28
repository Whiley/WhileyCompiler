function g() -> (int x, bool r):
    return 1, false

function f() -> int:
    int x
    int y
    x,y = g()
    return 1
