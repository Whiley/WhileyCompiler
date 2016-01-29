

function f(int[] x) 
requires |x| > 0:
    int y = x[0]
    int z = x[0]
    assert y == z

public export method test() :
    int[] arr = [1, 2, 3]
    f(arr)
    assert arr[0] == 1
