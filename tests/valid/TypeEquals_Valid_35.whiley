type pos is int where true
type neg is int where true

type expr is pos | neg | int[]

function f(expr e) -> int:
    if (e is pos) && (e > 0):
        e = (pos) e + 1
        return e
    else:
        return 0

public export method test() :
    assume f((neg) -1) == 0
    assume f((pos) 1) == 2
    assume f((pos) 1234) == 1235
