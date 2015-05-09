

function f([int] x) -> int:
    return |x|

public export method test() -> void:
    [int] arr = []
    assume f(arr) == 0
