
function f(int[] ls) -> bool
requires some { i in 0 .. 4 | (i >= 0) && (i < |ls|) && (ls[i] < 0) }:
    return true

function g(int[] ls) -> void
requires |ls| > 0:
    f(ls)
