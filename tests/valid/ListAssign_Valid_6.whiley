

public export method test() :
    int[] arr1 = [1, 2, 4]
    int[] arr2 = arr1
    arr2[2] = 3
    assert arr2[2] == |arr1|
    assert arr1 == [1,2,4]
    assert arr2 == [1,2,3]
