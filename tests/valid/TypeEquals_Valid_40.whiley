

type pos is bool

type neg is int

type expr is pos | neg | int[]

function f(expr e) -> int:
    if (e is pos) && e == true:
        return 0
    else:
        if e is neg:
            return 1
        else:
            return 2

public export method test() :
    assume f(-1) == 1
    assume f(false) == 2
    assume f(true) == 0
    assume f([1, 2, 3]) == 2
