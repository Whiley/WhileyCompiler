

function f(int x) -> (int y)
ensures y > x:
    x = x + 1
    return x

public export method test() :
    int y = f(1)
    assume y == 2
