function f(int[] ls) -> bool
requires all { i in 0..5 | (i < 0) || (i >= |ls|) || (ls[i] >= 0) }:
    return true

public export method test() :
    f([-1, 0, 1, 2, 3])
