

function f(int x) -> int:
    if x < 10:
        return 1
    else:
        return 2

public export method test() :
    assume f(1) == 1
    assume f(10) == 2
    assume f(11) == 2
    assume f(1212) == 2
    assume f(-1212) == 1
