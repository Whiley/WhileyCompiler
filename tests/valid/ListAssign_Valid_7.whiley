

function f(int i) -> {int[] f1,int[] f2}:
    int[] arr1 = [1, 2, 64]
    int[] arr2 = arr1
    if i != |arr1|:
        arr2[2] = 3
    else:
        arr2[2] = i
    assert arr2[2] == |arr1|
    return {f1: arr1, f2: arr2}

public export method test() :
    assume f(2) == {f1: [1, 2, 64], f2: [1, 2, 3]}
    assume f(3) == {f1: [1, 2, 64], f2: [1, 2, 3]}
