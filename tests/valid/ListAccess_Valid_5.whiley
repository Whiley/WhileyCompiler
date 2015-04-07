import whiley.lang.*

function f([int] x) -> int:
    return |x|

method main(System.Console sys) -> void:
    [[int]] arr = [[1, 2, 3]]
    sys.out.println(f(arr[0]))
