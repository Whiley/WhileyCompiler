

function f(int x) -> int:
    if x < 10:
        return -1
    else:
        if x > 10:
            return 1
        else:
            return 0

public export method test() :
    assume f(1) == -1
    assume f(10) == 0
    assume f(11) == 1
    assume f(1212) == 1
    assume f(-1212) == -1
