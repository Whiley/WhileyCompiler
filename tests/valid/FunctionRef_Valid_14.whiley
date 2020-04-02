function f(int x) -> (int b):
    return x+1

public export method test():
    //
    {int x, ...} rec = {x:0, fn: &f}
    //
    assert rec.x == 0
    