

function f(real x) -> int:
    switch x:
        case 1.23:
            return 0
        case 2.01:
            return -1
    return 10

public export method test() :
    assume f(1.23) == 0
    assume f(2.01) == -1
    assume f(3.0) == 10
    assume f(-1.0) == 10
