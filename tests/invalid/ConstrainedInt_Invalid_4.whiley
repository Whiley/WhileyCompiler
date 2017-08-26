function f(int x) -> (int y)
requires x != 0
ensures y != 1:
    //
    return x

public export method test():
    assume f(1) == 1
