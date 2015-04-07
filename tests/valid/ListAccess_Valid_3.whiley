import whiley.lang.*

function f([int] x, int i) -> void
requires |x| > 0:
    if (i < 0) || (i >= |x|):
        i = 0
    //
    int y = x[i]
    int z = x[i]
    assert y == z

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    f(arr, 1)
    sys.out.println(arr)
    f(arr, 2)
    sys.out.println(arr)
    f(arr, 3)
    sys.out.println(arr)
    f(arr, -1)
    sys.out.println(arr)
    f(arr, 4)
