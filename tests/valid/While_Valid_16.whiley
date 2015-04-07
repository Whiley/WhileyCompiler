import whiley.lang.*

type nat is (int x) where x >= 0

function inc([nat] src) -> ([nat] result)
// Result must be same size as input
ensures |result| == |src|
// Every element of result must be positive
ensures no { x in 0 .. |src| | result[x] <= 0 }:
    //
    int i = 0
    while i < |src| where i >= 0 && i <= |src| && no { x in 0 .. i | src[x] <= 0 }:
        src[i] = src[i] + 1
        i = i + 1
    return src

method main(System.Console sys) -> void:
    [nat] xs = [1, 3, 5, 7, 9, 11]
    xs = inc(xs)
    sys.out.println(xs)
