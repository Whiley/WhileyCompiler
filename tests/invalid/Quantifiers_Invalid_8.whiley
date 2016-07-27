
function f(int[] ls) -> bool
requires all { i in -1..4 | (i < 0) || (i >= |ls|) || (ls[i] >= 0) }:
    return true

function g(int[] ls) 
requires |ls| > 0:
    f(ls)
