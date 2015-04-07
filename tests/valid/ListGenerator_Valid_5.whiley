import whiley.lang.*

function f([int] x) -> int:
    return |x|

method main(System.Console sys) -> void:
    [int] arr = []
    sys.out.println(f(arr))
