
method test(int x) :
    int[] arr
    //
    if x > 0:
        arr = [1, 2, 4]
    else:
        arr = [1, 2, 3]
    assert arr[0] < |arr|
    assert arr[1] < |arr|
    assert arr[2] != |arr|

public export method test():
    test(1)
