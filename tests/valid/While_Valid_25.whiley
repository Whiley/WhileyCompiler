

function f(int n) -> int:
    int x = 0
    int y = 0
    while x < n where y == (2 * x):
        x = x + 1
        y = y + 2
    return x + y

public export method test() :
    assume f(10) == 30
