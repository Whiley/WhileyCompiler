

function f(int x, int y) -> int:
    bool a = x == y
    if a:
        return 1
    else:
        return x + y

function g(int x, int y) -> int:
    bool a = x >= y
    if !a:
        return x + y
    else:
        return 1

public export method test() :
    assume f(1, 1) == 1
    assume f(0, 0) == 1
    assume f(4, 345) == 349
    assume g(1, 1) == 1
    assume g(0, 0) == 1
    assume g(4, 345) == 349
