import whiley.lang.*

function reverse([int] xs) -> ([int] ys)
// size of lists are the same
ensures |xs| == |ys|
// Every element in returned list is in opposite position
ensures all { i in 0 .. |xs| | ys[i] == xs[|xs| - (i+1)] }:
    //
    int i = 0
    [int] zs = xs
    //
    while i < |xs|
    // Index is positive and at most one past length of list
    where i >= 0 && i <= |xs| && |xs| == |zs|
    // Every element upto (but not including) i is reversed
    where all { j in 0 .. i | xs[j] == zs[|xs| - (j+1)]}:
        //
        int j = |xs| - (i+1)
        xs[i] = zs[j]
        i = i + 1
    //
    return xs


method main(System.Console console):
    [int] before = [1,2,3,4,5,6]
    [int] after = reverse(before)
    console.out.println_s("REVERSE(" ++ Any.toString(before) ++ ") = " ++ Any.toString(after))

    before = [1,2,3]
    after = reverse(before)
    console.out.println_s("REVERSE(" ++ Any.toString(before) ++ ") = " ++ Any.toString(after))

    before = []
    after = reverse(before)
    console.out.println_s("REVERSE(" ++ Any.toString(before) ++ ") = " ++ Any.toString(after))
