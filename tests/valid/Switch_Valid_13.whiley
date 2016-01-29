

function f(int x) -> int:
    switch x:
        case 1, 2:
            return -1
        case 3:
            return 1
    return 10

public export method test() :
    assume f(1) == -1
    assume f(2) == -1
    assume f(3) == 1
    assume f(-1) == 10
