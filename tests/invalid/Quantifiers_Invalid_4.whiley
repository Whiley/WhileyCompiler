
function f([int] ls) -> bool
requires some { i in [0, 1, 2, 3, 4] | (i >= 0) && ((i < |ls|) && (ls[i] < 0)) }:
    return true

method main() -> void:
    f([1, 2, 3])
