function arraySum1(int[] a, int[] b) -> (int[] c)
requires |a| == |b| && |a| >= 0 && |b| >= 0
ensures |c| == |a|
ensures all { i in 0..|c| | c[i] == a[i] + b[i] }:
    c = [0; |a|]
    int k = 0
    while k < |a| where |c| == |a| && 0 <= k && k <= |a| &&
                        all { i in 0..k | c[i] == a[i] + b[i] }:
        c[k] = a[k] + b[k]
        k = k+1
    return c

public export method test():
    //
    int[] xs = [1,2,3]
    int[] ys = [4,5,6]
    //
    assert arraySum1(xs,ys) == [5,7,9]
    //
    assert arraySum1(xs,xs) == [2,4,6]
    //
    assert arraySum1(ys,ys) == [8,10,12]
    //
    assert arraySum1(ys,xs) == [5,7,9]