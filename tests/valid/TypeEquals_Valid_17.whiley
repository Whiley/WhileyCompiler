

type rlist is real | int[]

function f(rlist l) -> int:
    if l is real:
        return 0
    else:
        return |l|

public export method test() :
    assume f(123.0) == 0
    assume f(1.23) == 0
    assume f([1, 2, 3]) == 3
