function max(int a, int b) -> int:
    if a >= b:
        return a
    else:
        return b

function max(int[] xs) -> (int result)
// Input list cannot be empty
requires |xs| > 0
// Return must be element of input list
ensures some { i in 0..|xs| | xs[i] == result}
// No element of input list is larger than return
ensures no { i in 0..|xs| | xs[i] > result }:
    //
    int r = xs[0]
    int i = 0
    while i < |xs|
        where some { j in 0..|xs| | xs[j] == r }
        where no { j in 0 .. i | xs[j] > r }:
        r = max(r, xs[i])
        i = i + 1
    return r

public export method test() :
    assume max([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]) == 10
    assume max([-8, 7, 9, 1, -1, 2, 5, 6, -200, 4]) == 9
    assume max([1]) == 1
