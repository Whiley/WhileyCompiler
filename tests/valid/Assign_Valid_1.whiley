function swap(int x, int y) -> (int u, int v)
ensures u == y && v == x:
    return y, x

function sort2(int x, int y) -> (int u, int v)
ensures u == y && v == x:
    (x, y) = swap(x, y)
    return x,y

public export method test():
    int a = 1
    int b = 2
    //
    (a,b) = swap(a,b)
    assume a == 2
    assume b == 1
    //
    (a,b) = sort2(a,b)
    assume a == 1
    assume b == 2    
