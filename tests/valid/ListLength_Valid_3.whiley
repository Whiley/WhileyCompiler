import whiley.lang.*

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    assert arr[0] < |arr|
    assert arr[1] < |arr|
    assert arr[2] == |arr|
    sys.out.println(arr[0])
