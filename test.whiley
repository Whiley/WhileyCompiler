int f(int x) requires x >= 0, ensures $ > 0:
    bool a = x == 0
    if(a):
        return 1
    else:
        return x
