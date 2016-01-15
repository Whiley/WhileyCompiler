

function f(int x, int y) -> int
    requires y != 0:
    return x % y

public export method test() :
    assume f(10, 5) == 0
    assume f(10, 4) == 2
    assume f(1, 4) == 1
    assume f(103, 2) == 1
    assume f(-10, 5) == 0
    assume f(-10, 4) == -2
    assume f(-1, 4) == -1
    assume f(-103, 2) == -1
    assume f(-10, -5) == 0
    assume f(-10, -4) == -2
    assume f(-1, -4) == -1
    assume f(-103, -2) == -1
    assume f(10, -5) == 0
    assume f(10, -4) == 2
    assume f(1, -4) == 1
    assume f(103, -2) == 1
