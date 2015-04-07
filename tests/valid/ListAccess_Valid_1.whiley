import whiley.lang.*

function f([int] x) -> void
requires |x| > 0:
    int y = x[0]
    int z = x[0]
    assert y == z

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    f(arr)
    sys.out.println(arr[0])
