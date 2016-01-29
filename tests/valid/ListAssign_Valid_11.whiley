

function f(int[] a) -> int[]
// Input list cannot be empty
requires |a| > 0:
    //
    a[0] = 5
    return a

public export method test() :
    int[] b = [1, 2, 3]
    assume f(b) == [5,2,3]

