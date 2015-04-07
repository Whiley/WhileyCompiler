import whiley.lang.*

function f(int i) -> [int]:
    [int] arr1 = [1, 2, 64]
    [int] arr2 = arr1
    if i != |arr1|:
        arr2[2] = 3
    else:
        arr2[2] = i
    assert arr2[2] == |arr1|
    return arr1 ++ arr2

method main(System.Console sys) -> void:
    sys.out.println(f(2))
    sys.out.println(f(3))
