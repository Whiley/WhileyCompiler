import whiley.lang.*

method main(System.Console sys) -> void:
    [int] arr1 = [1, 2, 3]
    [int] arr2 = arr1
    arr2[2] = 2
    assert arr2[2] != |arr1|
    sys.out.println(arr1)
    sys.out.println(arr2)
