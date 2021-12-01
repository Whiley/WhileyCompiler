property sum(int n, int[] xs, int i) -> (bool r):
    if (i >= |xs|):
        return n == 0
    else:
        return sum(n-xs[i],xs,i+1)

function fsum(int[] xs, int s, int[] ys) -> (int r)
requires sum(s,xs,0) && sum(s,ys,0)
ensures r == 0:
    //
    return 0

function f() -> (int r):
    return 0

public export method test():
    int x = f()
    assert fsum([x,x],2*x,[x,x]) == 0