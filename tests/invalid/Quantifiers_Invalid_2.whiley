
function f(int[] ls) -> bool
requires no { i in 0..|ls| | ls[i] <= 0 }:
    return true

method main() -> void:
    f([0, 1, 2, 3])
