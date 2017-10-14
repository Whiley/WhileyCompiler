type nat is (int x) where x >= 0

property mult(int[] a, int[] b, int[] c, int n)
where all { i in 0..n | c[i] == a[i] * b[i] }

function arrayMult(int[] a, int[] b) -> (int[] c)
requires |a| == |b|
ensures |c| == |a|
ensures mult(a,b,c,|a|):
    c = [0; |a|]
    nat k = 0
    while k < |a|
    where |c| == |a| && k <= |a|
    where mult(a,b,c,k):
        c[k] = a[k] * b[k]
        k = k + 1
    return c

public export method test():
    //
    int[] xs = [1,2,3]
    int[] ys = [4,5,6]
    //
    assert arrayMult(xs,ys) == [4,10,18]
    //
    assert arrayMult(xs,xs) == [1,4,9]
    //
    assert arrayMult(ys,ys) == [16,25,36]
    //
    assert arrayMult(ys,xs) == [4,10,18]