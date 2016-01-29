

type pos is int

type neg is int

type expr is pos | neg | int[]

function f(expr e) -> int:
    if (e is pos) && (e > 0):
        e = e + 1
        return e
    else:
        return 0

public export method test() :
    assume f(-1) == 0
    assume f(1) == 2
    assume f(1234) == 1235
