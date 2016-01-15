

function f() -> (int[] rs)
// Returned list must have at least two elements
ensures |rs| > 1:
    //
    return [1, 2]

public export method test() :
    int[] a1 = f()
    int[] a2 = f()
    a2[0] = 0
    assume a1[0] == 1
    assume a1[1] == 2
    assert a2[0] == 0
    assume a2[1] == 2
