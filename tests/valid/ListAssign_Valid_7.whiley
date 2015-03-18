import whiley.lang.System

function f(int i) -> ASCII.string:
    [int] arr1 = [1, 2, 64]
    [int] arr2 = arr1
    if i != |arr1|:
        arr2[2] = 3
    else:
        arr2[2] = i
    assert arr2[2] == |arr1|
    return Any.toString(arr1) ++ Any.toString(arr2)

method main(System.Console sys) -> void:
    sys.out.println_s(f(2))
    sys.out.println_s(f(3))
