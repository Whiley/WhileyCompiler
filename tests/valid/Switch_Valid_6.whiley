

function f(int x) -> int:
    switch x:
        case 1:
            return 0
        case 2:
            return -1
    return 10

public export method test() :
    assume f(1) == 0
    assume f(2) == -1
    assume f(3) == 10
    assume f(-1) == 10
