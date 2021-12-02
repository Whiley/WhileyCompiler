type nat is (int x) where x >= 0

property sum(int[] a, int[] b, int[] c, int n) -> (bool r):
    return all { i in 0..n | c[i] == a[i] + b[i] }

function arraySum2(int[] a, int[] b) -> (int[] c)
requires |a| == |b|
ensures |c| == |a|
ensures sum(a,b,c,|a|):
    nat k = 0
    c = [0; |a|]
    while k < |a|
    where |c| == |a| && k <= |a|
    where sum(a,b,c,k):
        c[k] = a[k] + b[k]
        k = k + 1
    return c 

public export method test():
    //
    int[] xs = [1,2,3]
    int[] ys = [4,5,6]
    //
    assert arraySum2(xs,ys) == [5,7,9]
    //
    assert arraySum2(xs,xs) == [2,4,6]
    //
    assert arraySum2(ys,ys) == [8,10,12]
    //
    assert arraySum2(ys,xs) == [5,7,9]