

function f(int[] xs) -> (int result)
// Input list cannot be empty
requires |xs| > 0
// Return must match some element from input list
ensures some { i in 0 .. |xs| | result == xs[i] }:
    //
    int r = xs[0]
    int i = 1
    while i < |xs| 
        where i >= 1 
        where some { j in 0 .. i | r == xs[j] }:
        r = xs[i]
        i = i + 1
    return r

public export method test() :
    assume f([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]) == 10
