import whiley.lang.System

method main(System.Console sys) => void:
    [int] arr = [1, 2, 3]
    assert |arr| == 3
    sys.out.println(Any.toString(arr[0]))
