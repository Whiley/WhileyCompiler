

type nat is (int x) where x >= 0

function inc(nat[] src) -> (nat[] result)
// Result must be same size as input
ensures |result| == |src|
// Every element of result must be positive
ensures no { x in 0 .. |src| | result[x] <= 0 }:
    //
    int i = 0
    while i < |src| 
        where i >= 0 && i <= |src| 
        where no { x in 0 .. i | src[x] <= 0 }:
        //
        src[i] = src[i] + 1
        i = i + 1
    return src

public export method test() :
    nat[] xs = [1, 3, 5, 7, 9, 11]
    xs = inc(xs)
    assume xs == [2, 4, 6, 8, 10, 12]
