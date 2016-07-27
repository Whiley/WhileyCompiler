
function f(int[] ls) -> bool
requires all { i in 0..|ls| | ls[i] > 0 }:
    return true

method main() :
    f([0, 1, 2, 3])
