type nat is (int x) where x >= 0

function f(int|null x) -> nat:
    //
    if x is nat:
        return x
    else if x is null:
        return 0
    else:
        return -x

public export method test():
    assume f(-1) == 1
    assume f(0) == 0
    assume f(1) == 1
    assume f(null) == 0