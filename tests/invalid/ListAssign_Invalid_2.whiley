
method main(System.Console sys) -> void:
    [int] arr1 = [1, 2, 3]
    [int] arr2 = arr1
    arr2[2] = 2
    assert arr2[2] == |arr1|
    debug Any.toString(arr1)
    debug Any.toString(arr2)
