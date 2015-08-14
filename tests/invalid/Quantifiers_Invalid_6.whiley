
function f(int[] ls) -> bool
requires no { i in 0..|ls| | ls[i] <= 0 }:
    return true

function g(int[] ls) -> void:
    f(ls)
