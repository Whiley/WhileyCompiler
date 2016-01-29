function f(int x) -> (bool b, int y):
    return x < 0,x

function g(int x) -> (bool b, int y):
    return f(x)

public export method test():
    int x
    bool b
    b,x = g(1)
    assume x == 1
    assume b == false
    b,x = g(-1)
    assume x == -1
    assume b == true