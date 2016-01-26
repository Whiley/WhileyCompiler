

function f(int x) -> (int r)
ensures r > x:
    //
    x = x + 1
    return x

public export method test() :
    assume f(1) == 2
