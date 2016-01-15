

function f(int[] x, int i) 
requires |x| > 0:
    if (i < 0) || (i >= |x|):
        i = 0
    //
    int y = x[i]
    int z = x[i]
    assert y == z

public export method test() :
    int[] arr = [1, 2, 3]
    f(arr, 1)
    assume arr == [1,2,3]
    f(arr, 2)
    assume arr == [1,2,3]
    f(arr, 3)
    assume arr == [1,2,3]
    f(arr, -1)
    assume arr == [1,2,3]
    f(arr, 4)
