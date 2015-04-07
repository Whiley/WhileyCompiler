import whiley.lang.*

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    assert |arr| == 3
    sys.out.println(arr[0])
