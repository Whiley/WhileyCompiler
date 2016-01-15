

type pos is real

type neg is int

type expr is pos | neg | int[]

function f(expr e) -> int:
    if (e is pos) && (e > 0.0):
        return 0
    else:
        if e is neg:
            return 1
        else:
            return 2

public export method test() :
    assume f(-1) == 1
    assume f(1.0) == 0
    assume f(1.234) == 0
    assume f([1, 2, 3]) == 2
