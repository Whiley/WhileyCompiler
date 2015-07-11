
function f([int] ls) -> bool
requires some { i in 0..|ls| | ls[i] < 0 }:
    return true

method main() -> void:
    f([1, 2, 3])
