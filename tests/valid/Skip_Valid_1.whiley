

function f(int x) -> int:
    if x > 0:
        skip
    else:
        return -1
    return x

public export method test() :
    assume f(1) == 1
    assume f(-10) == -1
